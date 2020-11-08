package ru.otus.homework.hw32.back.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.otus.homework.hw32.common.protobuf.MessageSystemProtobuf;
import ru.otus.homework.hw32.common.protobuf.generated.MessageSystemProtobufGrpc;
import ru.otus.messagesystem.message.MessageProtobufConverter;

@Configuration
@Profile("gRPC")
public class MessageSystemGRPC {

    @Value("${gRPC.port}")
    private int port;

    @Value("${gRPC.host}")
    private String host;

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel grpcChannel() {
        return ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    }

    @Bean
    public MessageProtobufConverter converterMessageProtobuf() {
        return new MessageProtobufConverter();
    }

    @Bean()
    public MessageSystemProtobuf messageSystem(MessageProtobufConverter converter, ManagedChannel channel) {
        return new MessageSystemProtobuf(
                MessageSystemProtobufGrpc.newStub(channel),
                MessageSystemProtobufGrpc.newBlockingStub(channel),
                converter
        );
    }
}
