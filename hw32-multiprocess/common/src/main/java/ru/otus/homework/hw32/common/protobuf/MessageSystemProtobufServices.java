package ru.otus.homework.hw32.common.protobuf;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.message.HandlersStoreSingleHandler;
import ru.otus.homework.hw32.common.protobuf.generated.MessageProto;
import ru.otus.homework.hw32.common.protobuf.generated.MessageSystemProtobufGrpc;
import ru.otus.homework.hw32.common.protobuf.generated.MsClientMeta;
import ru.otus.homework.hw32.common.protobuf.generated.Session;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.RequestHandler;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.MsClientImpl;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageProtobufConverter;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MessageSystemProtobufServices extends MessageSystemProtobufGrpc.MessageSystemProtobufImplBase {

    private final MessageSystem messageSystem;
    private final CallbackRegistry callbackRegistry;
    private final MessageProtobufConverter converter;
    private final Map<UUID, StreamObserver<MessageProto>> bindings = new ConcurrentHashMap<>();

    public MessageSystemProtobufServices(MessageSystem messageSystem, CallbackRegistry callbackRegistry, MessageProtobufConverter converter) {
        this.messageSystem = messageSystem;
        this.callbackRegistry = callbackRegistry;
        this.converter = converter;
    }

    @Override
    public void getHandler(Session session, StreamObserver<MessageProto> responseObserver) {
        bindings.put(getInd(session), responseObserver);
    }

    @Override
    public void releaseHandler(Session request, StreamObserver<Empty> responseObserver) {
        try {
            var obs = bindings.remove(getInd(request));
            obs.onCompleted();
        } catch (Exception e) {
            log.error("", e);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void addClient(MsClientMeta request, StreamObserver<Empty> responseObserver) {
        final UUID sessionId = getInd(request.getSession());
        var client = new MsClientImpl(request.getName(), messageSystem, new HandlersStoreSingleHandler(new RequestHandler<ResultDataType>() {
            @Override
            public Optional<Message> handle(Message msg) {
                // Если какие ошибки и произойдут обрабатываем их ниже по стэку
                // c StreamObserver нельзя работать асинхронно
                var obs = bindings.get(sessionId);
                var body = converter.convert(msg);
                synchronized (sessionId) {
                    obs.onNext(body);
                }
                return Optional.empty();
            }
        }), callbackRegistry);
        messageSystem.addClient(client);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void newMessage(MessageProto request, StreamObserver<Empty> responseObserver) {
        messageSystem.newMessage(converter.convert(request));
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void removeClient(MsClientMeta request, StreamObserver<Empty> responseObserver) {
        messageSystem.removeClient(request.getName());
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void currentQueueSize(Empty request, StreamObserver<Int32Value> responseObserver) {
        responseObserver.onNext(Int32Value.newBuilder().setValue(messageSystem.currentQueueSize()).build());
        responseObserver.onCompleted();
    }

    private UUID getInd(Session session) {
        return UUID.fromString(session.getUuid());
    }
}
