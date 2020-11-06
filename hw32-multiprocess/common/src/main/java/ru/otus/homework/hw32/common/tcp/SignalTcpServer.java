package ru.otus.homework.hw32.common.tcp;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class SignalTcpServer implements Runnable {

    private final String host;
    private final int port;

    public SignalTcpServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                try (Socket clientSocket = serverSocket.accept();
                     var bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
                     var objectInputStream = new ObjectInputStream(bufferedInputStream);
                     var bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
                ) {
                    Signal hello = (Signal) objectInputStream.readObject();
                    if (!"Hello".equals(hello.getTag())) {return;}
                    log.info("Server ok");
                    try (
                            var objectOutputStream = new ObjectOutputStream(bufferedOutputStream)) {
                        log.info("Server ok2");
                        objectOutputStream.writeObject(hello);
                        objectOutputStream.flush();
                        while (true) {
                            Signal signal = (Signal) objectInputStream.readObject();
                            log.info("get signal {}", signal.getTag());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("", e);
        }
    }

    private void process(Signal readObject) {
    }
}
