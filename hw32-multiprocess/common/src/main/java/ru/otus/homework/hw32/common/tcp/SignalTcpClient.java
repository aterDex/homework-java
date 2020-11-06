package ru.otus.homework.hw32.common.tcp;

import lombok.extern.slf4j.Slf4j;
import ru.otus.messagesystem.message.Message;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

@Slf4j
public class SignalTcpClient implements Runnable {

    private final String host;
    private final int port;
    private final Consumer<Message> handler;
    private ObjectOutputStream out;

    public SignalTcpClient(String host, int port, Consumer<Message> handler) {
        this.host = host;
        this.port = port;
        this.handler = handler;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                try (var clientSocket = new Socket(host, port);
                     var bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
                     var objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
                     var bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
                ) {
                    out = objectOutputStream;
                    sendSignal(new Signal("Hello", null));
                    log.info("client ok");
                    try (var objectInputStream = new ObjectInputStream(bufferedInputStream)) {
                        log.info("client ok2");
                        Signal hello = (Signal) objectInputStream.readObject();
                        if (!"Hello".equals(hello.getTag())) {
                            return;
                        }
                        while (true) {
                            sendSignal(new Signal("Hello", null));
                            Thread.sleep(1000);
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("", ex);
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    break;
                }
            } finally {
                out = null;
            }
        }
        log.info("exit from MessageSystemTcpClient");
    }

    private synchronized void sendSignal(Signal signal) throws IOException {
        out.writeObject(signal);
        out.flush();
    }

    private void process(Signal signal) {
        switch (signal.getTag()) {
            case "msg":
                handler.accept((Message) signal.getBody());
                break;
            default:
                log.info("Unknown signal with tag {}", signal.getTag());
                break;
        }
    }
}
