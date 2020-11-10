package ru.otus.homework.hw32.common;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.SneakyThrows;
import ru.otus.homework.hw32.common.message.MessageSystemRemote;
import ru.otus.homework.hw32.common.protobuf.MessageSystemProtobufServices;
import ru.otus.homework.hw32.common.protobuf.TransportByGRPC;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.message.MessageProtobufConverter;

import java.util.concurrent.Executors;

public class MessageSystemProviderGRPC implements MessageSystemProvider {

    private static final String description = "gRPC";
    private final String host;
    private final int port;

    public MessageSystemProviderGRPC(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public DisposableMessageSystem init(MessageSystem core) throws Exception {
        var converter = new MessageProtobufConverter();
        MessageSystemProtobufServices mes = new MessageSystemProtobufServices(core, null, converter);
        Server server = ServerBuilder.forPort(port).addService(mes).build();
        server.start();
        Thread.sleep(2000);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, server.getPort())
                .usePlaintext().build();

        var transport = new TransportByGRPC(channel, converter);
        var messageSystem = new MessageSystemRemote(transport, null);
        messageSystem.start();
        return new DisposableMessageSystem(description, messageSystem, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                messageSystem.dispose();
                transport.dispose();
                channel.shutdownNow();
                server.shutdownNow();
            }
        });
    }

    @Override
    public String getDescription() {
        return description;
    }
}
