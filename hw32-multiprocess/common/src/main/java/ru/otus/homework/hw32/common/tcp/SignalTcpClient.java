package ru.otus.homework.hw32.common.tcp;

import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.helper.HelperHw32;
import ru.otus.homework.hw32.common.helper.NonCloseableStream;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
public class SignalTcpClient implements Runnable {

    private final String host;
    private final int port;
    private final List<SignalClientListener> listeners = new CopyOnWriteArrayList<>();
    private final Map<UUID, Exchanger<Signal>> waitAnswer = new ConcurrentHashMap<>();
    private DataOutputStream outStream;
    private volatile boolean isConnected = false;

    public SignalTcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try (var clientSocket = new Socket(host, port);
             var bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
             var bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
             var dataOutputStream = new DataOutputStream(bufferedOutputStream);
        ) {
            isConnected = true;
            outStream = dataOutputStream;
            var forSize = new byte[4];
            while (!Thread.currentThread().isInterrupted()) {
                bufferedInputStream.read(forSize);
                try (var objectInputStream = new ObjectInputStream(new NonCloseableStream(bufferedInputStream))) {
                    var obj = objectInputStream.readObject();
                    if (obj == null) continue;
                    if (!(obj instanceof Signal)) {
                        log.warn("Unknown type '{}'. Stop client.", obj.getClass().getCanonicalName());
                        return;
                    }
                    process((Signal) obj);
                }
            }
        } catch (Exception ex) {
            log.error("", ex);
        } finally {
            isConnected = false;
            outStream = null;
            fireCloseConnect();
        }
        log.info("exit from SignalTcpClient");
    }

    public boolean isConnected() {
        return isConnected;
    }

    public synchronized void send(Signal signal) throws IOException {
        log.debug("<------ Client send signal: {}", signal);
        byte[] body = HelperHw32.objectToByte(signal);
        outStream.writeInt(body.length);
        outStream.write(body);
        outStream.flush();
    }

    public Signal sendAndGetAnswer(Signal signal) throws IOException, TimeoutException, InterruptedException {
        try {
            Exchanger<Signal> exchanger = registerAnswer(signal.getUuid());
            send(signal);
            return exchanger.exchange(null, 10, TimeUnit.SECONDS);
        } catch (Throwable t) {
            log.warn("Answer was lost for signal: {}", signal);
            throw t;
        } finally {
            unregisterAnswer(signal.getUuid());
        }
    }

    public void addListener(SignalClientListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SignalClientListener listener) {
        listeners.remove(listener);
    }

    private void process(Signal signal) {
        log.debug("------> Client get signal: {}", signal);
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
        fireEvent(signal);
        if (log.isDebugEnabled()) {
            log.debug("------ process time by {}s for {}", (System.currentTimeMillis() - time) / 1000.0, signal.getUuid());
        }
    }

    private void fireEvent(Signal signal) {
        for (SignalClientListener listener : listeners) {
            try {
                listener.event(signal, this);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    private void fireCloseConnect() {
        for (SignalClientListener listener : listeners) {
            try {
                listener.closeConnect(this);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    private void unregisterAnswer(UUID uuid) {
        waitAnswer.remove(uuid);
    }

    private Exchanger<Signal> registerAnswer(UUID uuid) {
        Exchanger<Signal> exchanger = new Exchanger<>();
        waitAnswer.put(uuid, exchanger);
        return exchanger;
    }
}
