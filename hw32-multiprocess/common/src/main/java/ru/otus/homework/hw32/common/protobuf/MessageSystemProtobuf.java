package ru.otus.homework.hw32.common.protobuf;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.protobuf.generated.MessageProto;
import ru.otus.homework.hw32.common.protobuf.generated.MessageSystemProtobufGrpc;
import ru.otus.homework.hw32.common.protobuf.generated.MsClientMeta;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageProtobufConverter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MessageSystemProtobuf implements MessageSystem {

    private final MessageSystemProtobufGrpc.MessageSystemProtobufStub stub;
    private final MessageSystemProtobufGrpc.MessageSystemProtobufBlockingStub blockStub;
    private final MessageProtobufConverter converter;
    private final Map<String, MsClient> localBindings = new ConcurrentHashMap<>();

    public MessageSystemProtobuf(MessageSystemProtobufGrpc.MessageSystemProtobufStub stub, MessageSystemProtobufGrpc.MessageSystemProtobufBlockingStub blockStub, MessageProtobufConverter converter) {
        this.stub = stub;
        this.blockStub = blockStub;
        this.converter = converter;
    }

    @Override
    public void addClient(MsClient msClient) {
        if (localBindings.containsKey(msClient.getName())) {
            throw new RuntimeException(String.format("Уже зарегистрирован msClient с именем %s", msClient.getName()));
        }
        try {
            localBindings.put(msClient.getName(), msClient);
            stub.addClient(MsClientMeta.newBuilder().setName(msClient.getName()).build(), new StreamObserver<MessageProto>() {
                @Override
                public void onNext(MessageProto value) {
                    msClient.handle(converter.convert(value));
                }

                @Override
                public void onError(Throwable t) {
                    log.error("", t);
                }

                @Override
                public void onCompleted() {
                }
            });
        } catch (Exception e) {
            localBindings.remove(msClient.getName());
            throw e;
        }
    }

    @Override
    public void removeClient(String clientId) {
        var client = localBindings.remove(clientId);
        if (client == null) {
            throw new RuntimeException(String.format("Не найден msClient с именем %s", clientId));
        }
        blockStub.removeClient(MsClientMeta.newBuilder().setName(clientId).build());
    }

    @Override
    public boolean newMessage(Message msg) {
        blockStub.newMessage(converter.convert(msg));
        return true;
    }

    @Override
    public void dispose() throws InterruptedException {
        dispose(() -> {
        });
    }

    @Override
    public void dispose(Runnable callback) throws InterruptedException {
        // todo не очень мне ту нравится...
        for (String name : localBindings.keySet()) {
            try {
                removeClient(name);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        callback.run();
    }

    @Override
    public void start() {
    }

    @Override
    public int currentQueueSize() {
        var i = blockStub.currentQueueSize(Empty.newBuilder().build());
        return i.getValue();
    }
}
