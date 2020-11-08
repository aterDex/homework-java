package ru.otus.homework.hw32.common;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.SneakyThrows;
import ru.otus.homework.hw32.common.protobuf.MessageSystemProtobuf;
import ru.otus.homework.hw32.common.protobuf.MessageSystemProtobufServices;
import ru.otus.homework.hw32.common.protobuf.generated.MessageSystemProtobufGrpc;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.message.MessageProtobufConverter;

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

        var messageSystem = new MessageSystemProtobuf(
                MessageSystemProtobufGrpc.newStub(channel),
                MessageSystemProtobufGrpc.newBlockingStub(channel),
                converter
        );
        return new DisposableMessageSystem(description, core, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                messageSystem.dispose();
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
