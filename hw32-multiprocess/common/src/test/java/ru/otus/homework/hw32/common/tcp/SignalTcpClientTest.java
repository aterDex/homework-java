package ru.otus.homework.hw32.common.tcp;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class SignalTcpClientTest {

    private final static String HOST = "127.0.0.1";
    private final static int PORT = 12002;

    @Test
    void send() throws Exception {
        Exchanger<Signal> exchanger = new Exchanger<>();

        var executor = Executors.newFixedThreadPool(2);

        var server = new SignalTcpServer(HOST, PORT, 10000);
        server.addListener(new SignalServerListener() {
            @Override
            public void event(UUID connectIdentifier, Signal signal, SignalTcpServer server) {
                try {
                    exchanger.exchange(signal, 5, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            @Override
            public void disconnect(UUID connectWitchClose, SignalTcpServer server) {
            }
        });

        var client = new SignalTcpClient(HOST, PORT);
        client.addListener(new SignalClientListener() {
            @Override
            public void event(Signal signal, SignalTcpClient client) {
            }

            @Override
            public void closeConnect(SignalTcpClient client) {
            }
        });

        var serverFuture = executor.submit(server);
        Thread.sleep(1000);
        var clientFuture = executor.submit(client);
        try {
            List<Signal> list = Stream.iterate(0, i -> i + 1)
                    .limit(1000)
                    .map(x -> new Signal("TEST" + x, UUID.randomUUID(), "body"))
                    .collect(Collectors.toList());
            for (Signal nextSignal : list) {
                client.send(nextSignal);
                assertEquals(nextSignal, exchanger.exchange(null, 5, TimeUnit.SECONDS));
            }
        } finally {
            serverFuture.cancel(true);
            clientFuture.cancel(true);
        }
    }

    @Test
    void handler() throws Exception {
        Exchanger<Signal> exchanger = new Exchanger<>();
        Exchanger<UUID> exchangerUUID = new Exchanger<>();

        var executor = Executors.newFixedThreadPool(2);

        var server = new SignalTcpServer(HOST, PORT, 10000);
        server.addListener(new SignalServerListener() {
            @Override
            public void event(UUID connectIdentifier, Signal signal, SignalTcpServer server) {
                try {
                    exchangerUUID.exchange(connectIdentifier, 5, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            @Override
            public void disconnect(UUID connectWitchClose, SignalTcpServer server) {
            }
        });

        var client = new SignalTcpClient(HOST, PORT);
        client.addListener(new SignalClientListener() {
            @Override
            public void event(Signal signal, SignalTcpClient client) {
                try {
                    exchanger.exchange(signal, 5, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            @Override
            public void closeConnect(SignalTcpClient client) {
            }
        });

        var serverFuture = executor.submit(server);
        Thread.sleep(1000);
        var clientFuture = executor.submit(client);
        try {
            List<Signal> list = Stream.iterate(0, i -> i + 1)
                    .limit(1000)
                    .map(x -> new Signal("TEST" + x, UUID.randomUUID(), "body"))
                    .collect(Collectors.toList());
            client.send(list.get(0));
            UUID uuid = exchangerUUID.exchange(null, 5, TimeUnit.SECONDS);

            for (Signal nextSignal : list) {
                server.send(uuid, nextSignal);
                assertEquals(nextSignal, exchanger.exchange(null, 5, TimeUnit.SECONDS));
            }
        } finally {
            serverFuture.cancel(true);
            clientFuture.cancel(true);
        }
    }

    @Test
    void sendAndHandler() throws Exception {
        Exchanger<Signal> exchanger = new Exchanger<>();

        var executor = Executors.newFixedThreadPool(2);

        var server = new SignalTcpServer(HOST, PORT, 10000);
        server.addListener(new SignalServerListener() {
            @Override
            public void event(UUID connectIdentifier, Signal signal, SignalTcpServer server) {
                try {
                    Signal nextSignal = exchanger.exchange(signal, 5, TimeUnit.SECONDS);
                    if (nextSignal != null) {
                        server.send(connectIdentifier, nextSignal);
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            @Override
            public void disconnect(UUID connectWitchClose, SignalTcpServer server) {
            }
        });

        var client = new SignalTcpClient(HOST, PORT);
        client.addListener(new SignalClientListener() {
            @Override
            public void event(Signal signal, SignalTcpClient client) {
                try {
                    Signal nextSignal = exchanger.exchange(signal, 5, TimeUnit.SECONDS);
                    if (nextSignal != null) {
                        client.send(nextSignal);
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            @Override
            public void closeConnect(SignalTcpClient client) {
            }
        });

        var serverFuture = executor.submit(server);
        Thread.sleep(1000);
        var clientFuture = executor.submit(client);
        try {
            var signal = new Signal("FIRST", UUID.randomUUID(), null);
            List<Signal> list = Stream.iterate(0, i -> i + 1)
                    .limit(1000)
                    .map(x -> new Signal("TEST" + x, UUID.randomUUID(), "body"))
                    .collect(Collectors.toList());
            client.send(signal);
            for (Signal nextSignal : list) {
                assertEquals(signal, exchanger.exchange(nextSignal, 5, TimeUnit.SECONDS));
                signal = nextSignal;
            }
            assertEquals(signal, exchanger.exchange(null, 5, TimeUnit.SECONDS));
        } finally {
            serverFuture.cancel(true);
            clientFuture.cancel(true);
        }
    }

    @Test
    void sendAndGetAnswer() throws Exception {
        AtomicInteger counter = new AtomicInteger();

        var executor = Executors.newFixedThreadPool(2);

        var server = new SignalTcpServer(HOST, PORT, 10000);
        server.addListener(new SignalServerListener() {
            @Override
            public void event(UUID connectIdentifier, Signal signal, SignalTcpServer server) {
                try {
                    server.send(connectIdentifier, new Signal("Answer", signal.getUuid(), null));
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            @Override
            public void disconnect(UUID connectWitchClose, SignalTcpServer server) {
            }
        });

        var client = new SignalTcpClient(HOST, PORT);
        client.addListener(new SignalClientListener() {
            @Override
            public void event(Signal signal, SignalTcpClient client) {
                counter.incrementAndGet();
            }

            @Override
            public void closeConnect(SignalTcpClient client) {
            }
        });

        var serverFuture = executor.submit(server);
        Thread.sleep(1000);
        var clientFuture = executor.submit(client);
        try {
            List<Signal> list = Stream.iterate(0, i -> i + 1)
                    .limit(1000)
                    .map(x -> new Signal("TEST" + x, UUID.randomUUID(), "body"))
                    .collect(Collectors.toList());
            for (Signal nextSignal : list) {
                assertEquals(nextSignal.getUuid(), client.sendAndGetAnswer(nextSignal).getUuid());
            }
            assertEquals(0, counter.get());
        } finally {
            serverFuture.cancel(true);
            clientFuture.cancel(true);
        }
    }

    @Test
    void handlerSendAndGetAnswer() throws Exception {

        AtomicInteger counter = new AtomicInteger();
        Exchanger<UUID> exchangerUUID = new Exchanger<>();

        var executor = Executors.newFixedThreadPool(2);

        var server = new SignalTcpServer(HOST, PORT, 10000);
        server.addListener(new SignalServerListener() {
            @Override
            public void event(UUID connectIdentifier, Signal signal, SignalTcpServer server) {
                if (counter.getAndIncrement() == 0) {
                    try {
                        exchangerUUID.exchange(connectIdentifier, 5, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            }

            @Override
            public void disconnect(UUID connectWitchClose, SignalTcpServer server) {
            }
        });

        var client = new SignalTcpClient(HOST, PORT);
        client.addListener(new SignalClientListener() {
            @Override
            public void event(Signal signal, SignalTcpClient client) {
                try {
                    client.send(new Signal("Answer", signal.getUuid(), null));
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            @Override
            public void closeConnect(SignalTcpClient client) {
            }
        });

        var serverFuture = executor.submit(server);
        Thread.sleep(1000);
        var clientFuture = executor.submit(client);
        try {
            List<Signal> list = Stream.iterate(0, i -> i + 1)
                    .limit(1000)
                    .map(x -> new Signal("TEST" + x, UUID.randomUUID(), "body"))
                    .collect(Collectors.toList());
            client.send(list.get(0));
            UUID uuid = exchangerUUID.exchange(null, 5, TimeUnit.SECONDS);

            for (Signal nextSignal : list) {
                assertEquals(nextSignal.getUuid(), server.sendAndGetAnswer(uuid, nextSignal).getUuid());
            }
            assertEquals(1, counter.get());
        } finally {
            serverFuture.cancel(true);
            clientFuture.cancel(true);
        }
    }
}