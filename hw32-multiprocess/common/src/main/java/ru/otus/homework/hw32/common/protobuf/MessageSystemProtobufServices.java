package ru.otus.homework.hw32.common.protobuf;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.message.HandlersStoreSingleHandler;
import ru.otus.homework.hw32.common.protobuf.generated.*;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.MsClientImpl;
import ru.otus.messagesystem.message.MessageProtobufConverter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MessageSystemProtobufServices extends MessageSystemProtobufGrpc.MessageSystemProtobufImplBase {

    private final MessageSystem messageSystem;
    private final CallbackRegistry callbackRegistry;
    private final MessageProtobufConverter converter;
    private final Map<String, StreamObserver<MessageProto>> binding = new ConcurrentHashMap<>();

    public MessageSystemProtobufServices(MessageSystem messageSystem, CallbackRegistry callbackRegistry, MessageProtobufConverter converter) {
        this.messageSystem = messageSystem;
        this.callbackRegistry = callbackRegistry;
        this.converter = converter;
    }

    @Override
    public void addClient(MsClientMeta request, StreamObserver<MessageProto> responseObserver) {
        if (binding.containsKey(request.getName())) {
            responseObserver.onError(new RuntimeException("Already exist " + request.getName()));
        }
        try {
            var client = new MsClientImpl(request.getName(), messageSystem, new HandlersStoreSingleHandler(msg -> {
                responseObserver.onNext(converter.convert(msg));
                return Optional.empty();
            }), callbackRegistry);
            messageSystem.addClient(client);
            binding.put(request.getName(), responseObserver);
        } catch (Exception e) {
            log.error("", e);
            responseObserver.onError(e);
            binding.remove(request.getName());
        }
    }

    @Override
    public void newMessage(MessageProto request, StreamObserver<Empty> responseObserver) {
        try {
            messageSystem.newMessage(converter.convert(request));
        } catch (Exception e) {
            responseObserver.onError(e);
            return;
        }
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void removeClient(MsClientMeta request, StreamObserver<Empty> responseObserver) {
        try {
            var observer = binding.remove(request.getName());
            if (observer == null) {
                throw new RuntimeException("Not found: " + request.getName());
            }
            observer.onCompleted();
            messageSystem.removeClient(request.getName());
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void currentQueueSize(Empty request, StreamObserver<Int32Value> responseObserver) {
        try {
            responseObserver.onNext(Int32Value.newBuilder().setValue(messageSystem.currentQueueSize()).build());
        } catch (Exception e) {
            responseObserver.onError(e);

        }
        responseObserver.onCompleted();
    }
}
