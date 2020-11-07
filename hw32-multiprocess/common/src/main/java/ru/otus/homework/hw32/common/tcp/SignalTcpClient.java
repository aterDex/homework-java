package ru.otus.homework.hw32.common.tcp;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

@Slf4j
public class SignalTcpClient implements Runnable {

    private final String host;
    private final int port;

    public SignalTcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                try (var clientSocket = new Socket(host, port);
                     var bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
                     var bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
                     var dataOutputStream = new DataOutputStream(bufferedOutputStream);
                ) {
                    for (int i = 0; i < 5; i++) {
                        byte[] msg = signalToMsg(new Signal("Hello", false, UUID.randomUUID(), null));
                        dataOutputStream.writeInt(msg.length);
                        dataOutputStream.write(msg);
                        dataOutputStream.flush();
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception ex) {
                log.error("", ex);
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        log.info("exit from SignalTcpClient");
    }

    @SneakyThrows
    private byte[] signalToMsg(Signal signal) {
        return null;
    }
}
