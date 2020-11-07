package ru.otus.homework.hw32.common.tcp;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.helper.HelperHw32;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

@Slf4j
public class SignalTcpServer implements Runnable {

    private final String host;
    private final int port;
    private final ByteBuffer readByteBuffer;
    private final SignalServerListener listener;
    private final Map<UUID, SocketChannel> binding = new HashMap<>();

    public SignalTcpServer(String host, int port, int bufferSize, SignalServerListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;
        this.readByteBuffer = ByteBuffer.allocateDirect(bufferSize);
    }

    @Override
    public void run() {
        try (var serverSocketChannel = ServerSocketChannel
                .open()
                .bind(new InetSocketAddress(host, port))
                .configureBlocking(false);
             var selector = Selector.open()
        ) {
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            listenTo(selector);
        } catch (Exception e) {
            log.error("", e);
        }
        log.info("SignalTcpServer stopped.");
    }

    @SneakyThrows
    private void listenTo(Selector selector) {
        while (!Thread.currentThread().isInterrupted()) {
            if (selector.select(20000) > 0) {
                Set<SelectionKey> sk = selector.selectedKeys();
                Iterator<SelectionKey> iterator = sk.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        acceptConnect(selector, (ServerSocketChannel) key.channel());
                    } else if (key.isReadable()) {
                        readFrom((SocketChannel) key.channel(), (ConnectionInfo) key.attachment());
                    }
                }
            }
        }
    }

    @SneakyThrows
    private void readFrom(SocketChannel channel, ConnectionInfo info) {
        try {
            var altBuffer = info.getAlternativeBuffer();
            if (altBuffer != null) {
                channel.read(altBuffer);
                if (altBuffer.hasRemaining()) {
                    return;
                }
                info.setAlternativeBuffer(null);
                altBuffer.flip();
                processBuffersWithObject(channel, info, altBuffer);
            }

            int read = channel.read(readByteBuffer);
            if (read < 0) {
                closeChanel(channel, info);
                return;
            }
            readByteBuffer.flip();

            while (readByteBuffer.hasRemaining()) {
                while (info.getForSize().hasRemaining() && readByteBuffer.hasRemaining()) {
                    info.getForSize().put(readByteBuffer.get());
                }
                if (info.getForSize().hasRemaining()) {
                    return;
                }
                info.getForSize().flip();
                int size = info.getForSize().getInt();
                info.getForSize().clear();
                if (size < 0) {
                    closeChanel(channel, info);
                }
                if (size > readByteBuffer.remaining()) {
                    ByteBuffer alt = ByteBuffer.allocateDirect(size);
                    alt.put(readByteBuffer);
                    info.setAlternativeBuffer(alt);
                    return;
                }
                processBuffersWithObject(channel, info, readByteBuffer);
            }
        } catch (Exception e) {
            log.info("", e);
            closeChanel(channel, info);
        } finally {
            readByteBuffer.clear();
        }
    }

    private void processBuffersWithObject(SocketChannel channel, ConnectionInfo info, ByteBuffer buffer) {
        Object obj = HelperHw32.readObjectFromByteBuffers(buffer);
        if (obj == null) {
            return;
        }
        if (!(obj instanceof Signal)) {
            throw new UnsupportedOperationException("Unsupported object " + obj.getClass().getCanonicalName());
        }
        Signal signal = (Signal) obj;
        fireSignal(channel, info, signal);
    }

    private void fireSignal(SocketChannel channel, ConnectionInfo info, Signal signal) {
        if (signal.isAnswer()) {

        } else {
            listener.event(info.getUuid(), signal);
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
        listener.closeConnect(info.getUuid());
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
