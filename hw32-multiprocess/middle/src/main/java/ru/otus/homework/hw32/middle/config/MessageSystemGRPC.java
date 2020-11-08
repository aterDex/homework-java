package ru.otus.homework.hw32.middle.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.otus.homework.hw32.common.protobuf.MessageSystemProtobufServices;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.message.MessageProtobufConverter;

@Configuration
@Profile("gRPC")
public class MessageSystemGRPC {

    @Value("${gRPC.port}")
    private int port;

    @Bean(initMethod = "start", destroyMethod = "shutdownNow")
    public Server grpcServer(MessageSystemProtobufServices services) throws Exception {
        return ServerBuilder.forPort(port).addService(services).build();
    }

    @Bean
    public MessageProtobufConverter converterMessageProtobuf() {
        return new MessageProtobufConverter();
    }

    @Bean()
    public MessageSystemProtobufServices messageSystemProtobufServices(MessageSystem messageSystem, MessageProtobufConverter converter) {
        return new MessageSystemProtobufServices(messageSystem, null, converter);
    }
}
