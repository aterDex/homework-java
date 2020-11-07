package ru.otus.homework.hw32.common.tcp;

import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.helper.HelperHw32;
import ru.otus.homework.hw32.common.helper.NonCloseableStream;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class SignalTcpClient implements Runnable {

    private final String host;
    private final int port;
    private final SignalClientListener listener;
    private final Map<UUID, Exchanger<Signal>> waitAnswer = new ConcurrentHashMap<>();
    private DataOutputStream outStream;

    public SignalTcpClient(String host, int port, SignalClientListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try (var clientSocket = new Socket(host, port);
                 var bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
                 var bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
                 var dataOutputStream = new DataOutputStream(bufferedOutputStream);
            ) {
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
                outStream = null;
                listener.closeConnect(this);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }
        log.info("exit from SignalTcpClient");
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
        listener.event(signal, this);
        if (log.isDebugEnabled()) {
            log.debug("------ process time by {}s for {}", (System.currentTimeMillis() - time)/1000.0, signal.getUuid());
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
