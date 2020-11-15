package ru.otus.homework.hw32.common.tcp;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.otus.homework.hw32.common.helper.HelperSerializeObject;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class SignalTcpServerTest {

    private final static String HOST = "127.0.0.1";
    private final static int PORT = 12000;

    static Collection<TaskForTest> tasks() {
        byte[] objectByte = HelperSerializeObject.objectToByte(new Signal("Test", UUID.randomUUID(), null));
        byte[] source = new byte[(objectByte.length + 4) * 2];
        ByteBuffer bb = ByteBuffer.wrap(source);
        bb.putInt(objectByte.length);
        bb.put(objectByte);
        bb.putInt(objectByte.length);
        bb.put(objectByte);

        final int size = objectByte.length;

        return List.of(
                new TaskForTest("TwoObjectSplit", 10000, ByteBuffer.wrap(source).limit(source.length), new int[]{4 + size, 4 + size}, 3),
                new TaskForTest("TwoObjectSlowly", 10000, ByteBuffer.wrap(source).limit(source.length), new int[]{1, 2, 1, 10, size - 9, 3, size}, 3),
                new TaskForTest("ObjectWithSmallCommonBuffer", 1, ByteBuffer.wrap(source).limit(source.length), new int[]{4, size, 4, size}, 3),
                new TaskForTest("ObjectWithSmallCommonBuffer2", 50, ByteBuffer.wrap(source).limit(source.length), new int[]{size + 4, size + 4}, 3),
                new TaskForTest("TwoObjectTogether", 10000, ByteBuffer.wrap(source).limit(source.length), new int[]{(4 + size) * 2}, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("tasks")
    void testServerBySlice(TaskForTest task) throws Exception {
        var waitLatch = new CountDownLatch(task.countEvent);

        var executor = Executors.newSingleThreadExecutor();
        var sts = new SignalTcpServer(HOST, PORT, task.bufferSize);
        sts.addListener(new SignalServerListener() {
            @Override
            public void event(UUID connectIdentifier, Signal signal, SignalTcpServer server) {
                waitLatch.countDown();
            }

            @Override
            public void disconnect(UUID connectWitchClose, SignalTcpServer server) {
                waitLatch.countDown();
            }
        });
        var futureForServer = executor.submit(sts);
        try {
            Thread.sleep(1000);

            sendTcpWithDelay(500, task.objectForSend, task.partialSend);
            assertTrue(waitLatch.await(1, TimeUnit.SECONDS));
        } finally {
            futureForServer.cancel(true);
        }
    }

    @SneakyThrows
    void sendTcpWithDelay(long timeout, ByteBuffer body, int[] sizePartials) {
        try (var socket = new Socket(HOST, PORT)) {
            for (int size : sizePartials) {
                Thread.sleep(timeout);
                byte[] b = new byte[size];
                body.get(b);
                socket.getOutputStream().write(b);
                socket.getOutputStream().flush();
            }
        }
    }

    @Test
    void testServerForSend() throws Exception {
        final Exchanger<UUID> exchanger = new Exchanger<>();
        var server = new SignalTcpServer(HOST, PORT, 10000);
        server.addListener(new SignalServerListener() {
            @Override
            public void event(UUID connectIdentifier, Signal signal, SignalTcpServer server) {
                try {
                    exchanger.exchange(connectIdentifier, 5, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            @Override
            public void disconnect(UUID connectWitchClose, SignalTcpServer server) {
            }
        });
        var executor = Executors.newSingleThreadExecutor();
        var serverFuture = executor.submit(server);
        try {
            Thread.sleep(1000);
            try (var channel = SocketChannel.open(new InetSocketAddress(HOST, PORT))) {
                ByteBuffer buffer = ByteBuffer.allocate(5000);

                var signal = new Signal("Test", UUID.randomUUID(), null);
                byte[] body = HelperSerializeObject.objectToByte(signal);

                buffer.putInt(body.length);
                buffer.put(body);

                channel.write(buffer.flip());

                UUID uuid = exchanger.exchange(null, 5, TimeUnit.SECONDS);

                Signal signalCheck = new Signal("TEST OK", UUID.randomUUID(), null);
                server.send(uuid, signalCheck);
                Thread.sleep(1000);

                channel.read(buffer.clear());
                buffer.flip();
                buffer.getInt();
                assertEquals(signalCheck, HelperSerializeObject.readObjectFromByteBuffers(buffer));
            }
        } finally {
            serverFuture.cancel(true);
        }
    }

    @Test
    void testServerForSendAndGetAnswer() throws Exception {
        final Exchanger<UUID> exchanger = new Exchanger<>();
        var server = new SignalTcpServer(HOST, PORT, 10000);
        server.addListener(new SignalServerListener() {
            @Override
            public void event(UUID connectIdentifier, Signal signal, SignalTcpServer server) {
                try {
                    exchanger.exchange(connectIdentifier, 5, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            @Override
            public void disconnect(UUID connectWitchClose, SignalTcpServer server) {
            }
        });
        var executor = Executors.newFixedThreadPool(2);
        Future<?> serverFuture = executor.submit(server);
        try {
            Thread.sleep(1000);

            var clientFuture = executor.submit(new Runnable() {
                @Override
                public void run() {
                    try (var channel = SocketChannel.open(new InetSocketAddress(HOST, PORT))) {
                        ByteBuffer buffer = ByteBuffer.allocate(5000);

                        var signal = new Signal("Test", UUID.randomUUID(), null);
                        byte[] body = HelperSerializeObject.objectToByte(signal);

                        buffer.putInt(body.length);
                        buffer.put(body);
                        channel.write(buffer.flip());
                        channel.read(buffer.clear());
                        channel.write(buffer.flip());
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            });
            try {
                UUID uuid = exchanger.exchange(null, 5, TimeUnit.SECONDS);
                Signal signalCheck = new Signal("TEST OK", UUID.randomUUID(), null);
                assertEquals(signalCheck, server.sendAndGetAnswer(uuid, signalCheck));
            } finally {
                clientFuture.cancel(true);
            }
        } finally {
            serverFuture.cancel(true);
        }
    }

    @AllArgsConstructor
    static class TaskForTest {
        final String name;
        final int bufferSize;
        final ByteBuffer objectForSend;
        final int[] partialSend;
        final int countEvent;

        @Override
        public String toString() {
            return name;
        }
    }
}
