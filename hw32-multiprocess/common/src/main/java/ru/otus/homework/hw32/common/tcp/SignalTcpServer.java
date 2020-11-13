package ru.otus.homework.hw32.common.tcp;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.helper.HelperSerializeObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
public class SignalTcpServer implements Runnable {

    private final String host;
    private final int port;
    private final ByteBuffer generalReadyBuffer;
    private final List<SignalServerListener> listeners = new CopyOnWriteArrayList<>();
    private final Map<UUID, SocketChannel> binding = new ConcurrentHashMap<>();
    private final Map<UUID, Exchanger<Signal>> waitAnswer = new ConcurrentHashMap<>();
    private volatile ServerSocketChannel serverSocketChanel;

    public SignalTcpServer(String host, int port, int bufferSize) {
        this.host = host;
        this.port = port;
        this.generalReadyBuffer = ByteBuffer.allocateDirect(bufferSize);
    }

    public SocketAddress getLocalAddress() throws IOException {
        var localFix = serverSocketChanel;
        if (localFix != null) {
            return localFix.getLocalAddress();
        }
        throw new IOException("Server is closed");
    }

    @Override
    public void run() {
        try (var serverSocketChannel = ServerSocketChannel
                .open();
             var selector = Selector.open()
        ) {
            serverSocketChannel
                    .bind(new InetSocketAddress(host, port))
                    .configureBlocking(false);
            this.serverSocketChanel = serverSocketChannel;
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            listenTo(selector);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            this.serverSocketChanel = null;
        }
        log.info("SignalTcpServer stopped.");
    }

    public void send(UUID channelName, Signal signal) throws IOException {
        log.debug("<====== server send to '{}' signal: {}", channelName, signal);
        // Без блокировок т.к. "Socket channels are safe for use by multiple concurrent threads."
        var channel = binding.get(channelName);
        byte[] body = HelperSerializeObject.objectToByte(signal);
        var bodyB = ByteBuffer.allocate(body.length + 4);
        bodyB.putInt(body.length);
        bodyB.put(body);
        bodyB.flip();
        channel.write(bodyB);
    }

    public Signal sendAndGetAnswer(UUID channelName, Signal signal) throws IOException, TimeoutException, InterruptedException {
        try {
            var exchanger = registerAnswer(signal.getUuid());
            send(channelName, signal);
            return exchanger.exchange(null, 10, TimeUnit.SECONDS);
        } catch (Throwable t) {
            log.warn("Answer was lost for channelName: '{}' and signal: {}", channelName, signal);
            throw t;
        } finally {
            unregisterAnswer(signal.getUuid());
        }
    }

    public void addListener(SignalServerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SignalServerListener listener) {
        listeners.remove(listener);
    }

    private void unregisterAnswer(UUID uuid) {
        waitAnswer.remove(uuid);
    }

    private Exchanger<Signal> registerAnswer(UUID uuid) {
        var exchanger = new Exchanger<Signal>();
        waitAnswer.put(uuid, exchanger);
        return exchanger;
    }

    @SneakyThrows
    private void listenTo(Selector selector) {
        while (!Thread.currentThread().isInterrupted()) {
            if (selector.select(20000) > 0) {
                var keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    var key = keys.next();
                    keys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        acceptConnect(selector, (ServerSocketChannel) key.channel());
                    } else if (key.isReadable()) {
                        readFromChannel((SocketChannel) key.channel(), (ConnectionInfo) key.attachment());
                    }
                }
            }
        }
    }

    /**
     * Боремся с возможной фрагментацией пакета.
     * Если все в порядке то вся посылка помещается в общий буфер.
     * Если нет, то для этого есть два дополнительных буфера.
     * Один маленький и существет всегда для того чтобы считать размер следущего пакета (если фрагментация в друг придется имеено на эту часть)
     * Второй создается по необходимости в двух случаях:
     * 1) если объект которые мы ожидаем больше чем общий буфер.
     * 2) если мы получили объект не полность и должны сохранить уже полученные данные.
     *
     * @param channel
     * @param info
     */
    @SneakyThrows
    private void readFromChannel(SocketChannel channel, ConnectionInfo info) {
        try {
            var altBuffer = info.getAlternativeBuffer();
            if (altBuffer != null) {
                channel.read(altBuffer);
                if (altBuffer.hasRemaining()) {
                    return;
                }
                info.setAlternativeBuffer(null);
                processBuffersWithObject(channel, info, altBuffer.flip());
            }

            int read = channel.read(generalReadyBuffer);
            if (read < 0) {
                closeChanel(channel, info);
                return;
            }
            generalReadyBuffer.flip();

            while (generalReadyBuffer.hasRemaining()) {
                var sizeBuffer = info.getForSize();
                while (sizeBuffer.hasRemaining() && generalReadyBuffer.hasRemaining()) {
                    sizeBuffer.put(generalReadyBuffer.get());
                }
                if (sizeBuffer.hasRemaining()) {
                    return;
                }
                int size = sizeBuffer.flip().getInt();
                sizeBuffer.clear();

                if (size < 0) {
                    closeChanel(channel, info);
                }
                if (size > generalReadyBuffer.remaining()) {
                    info.setAlternativeBuffer(
                            ByteBuffer.allocateDirect(size).put(generalReadyBuffer));
                    return;
                }
                processBuffersWithObject(channel, info, generalReadyBuffer);
            }
        } catch (Exception e) {
            log.info("", e);
            closeChanel(channel, info);
        } finally {
            generalReadyBuffer.clear();
        }
    }

    private void processBuffersWithObject(SocketChannel channel, ConnectionInfo info, ByteBuffer buffer) {
        Object obj = HelperSerializeObject.readObjectFromByteBuffers(buffer);
        if (obj == null) {
            return;
        }
        if (!(obj instanceof Signal)) {
            throw new UnsupportedOperationException("Unsupported object " + obj.getClass().getCanonicalName());
        }
        fireSignal(channel, info, (Signal) obj);
    }

    private void fireSignal(SocketChannel channel, ConnectionInfo info, Signal signal) {
        log.debug("======> Server get signal: {}", signal);
        long time = System.currentTimeMillis();
        Exchanger<Signal> exchanger = waitAnswer.remove(signal.getUuid());
        if (exchanger != null) {
            try {
                exchanger.exchange(signal, 1, TimeUnit.SECONDS);
                return;
            } catch (InterruptedException e) {
                log.error("", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("", e);
            }
        }
        fireEvent(info.getUuid(), signal);
        if (log.isDebugEnabled()) {
            log.debug("====== process time by {}s for {}", (System.currentTimeMillis() - time) / 1000.0, signal.getUuid());
        }
    }

    @SneakyThrows
    private void acceptConnect(Selector selector, ServerSocketChannel channel) {
        var socket = channel.accept();
        socket.configureBlocking(false);
        var connectionInfo = new ConnectionInfo();
        socket.register(selector, SelectionKey.OP_READ, connectionInfo);
        binding.put(connectionInfo.getUuid(), socket);
    }

    @SneakyThrows
    private void closeChanel(SocketChannel channel, ConnectionInfo info) {
        channel.close();
        binding.remove(info.getUuid());
        fireDisconnect(info.getUuid());
    }

    private void fireDisconnect(UUID uuid) {
        for (SignalServerListener listener : listeners) {
            try {
                listener.disconnect(uuid, this);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    private void fireEvent(UUID uuid, Signal signal) {
        for (SignalServerListener listener : listeners) {
            try {
                listener.event(uuid, signal, this);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    private class ConnectionInfo {
        @Getter
        private final UUID uuid = UUID.randomUUID();
        @Getter
        @Setter
        private ByteBuffer alternativeBuffer;
        @Getter
        private final ByteBuffer forSize = ByteBuffer.allocateDirect(4);
    }
}
