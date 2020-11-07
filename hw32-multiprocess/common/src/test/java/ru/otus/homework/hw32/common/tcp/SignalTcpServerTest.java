package ru.otus.homework.hw32.common.tcp;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.otus.homework.hw32.common.helper.HelperHw32;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class SignalTcpServerTest {

    private final static String HOST = "127.0.0.1";
    private final static int PORT = 12000;

    @ParameterizedTest
    @MethodSource("tasks")
    public void testServer(TaskForTest task) throws Exception {
        CountDownLatch waitLatch = new CountDownLatch(task.countEvent);

        ExecutorService ex = Executors.newSingleThreadExecutor();
        SignalTcpServer sts = new SignalTcpServer(HOST, PORT, task.bufferSize, new SignalServerListener() {
            @Override
            public void event(UUID connectIdentifier, Signal signal) {
                waitLatch.countDown();
            }

            @Override
            public void closeConnect(UUID connectWitchClose) {
                waitLatch.countDown();
            }
        });
        Future<?> futureForServer = ex.submit(sts);
        Thread.sleep(1000);

        sendTcpWithDelay(HOST, PORT, 500, task.objectForSend, task.partialSend);
        assertTrue(waitLatch.await(1, TimeUnit.SECONDS));
        futureForServer.cancel(true);
    }

    @SneakyThrows
    private void sendTcpWithDelay(String host, int port, long timeout, ByteBuffer body, int[] sizePartials) {
        try (var socket = new Socket(host, port);
        ) {
            for (int size : sizePartials) {
                Thread.sleep(timeout);
                byte[] b = new byte[size];
                body.get(b);
                socket.getOutputStream().write(b);
                socket.getOutputStream().flush();
            }
        }
    }

    private static Collection<TaskForTest> tasks() {
        byte[] objectByte = HelperHw32.objectToByte(new Signal("Test", false, UUID.randomUUID(), null));
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
                new TaskForTest("TwoObjectTogether", 10000, ByteBuffer.wrap(source).limit(source.length), new int[]{(4 + size) * 2}, 3)
        );
    }

    @AllArgsConstructor
    private static class TaskForTest {
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
