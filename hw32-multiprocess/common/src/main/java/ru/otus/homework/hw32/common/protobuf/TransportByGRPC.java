package ru.otus.homework.hw32.common.protobuf;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.message.TransportForMessageSystem;
import ru.otus.homework.hw32.common.message.TransportListener;
import ru.otus.homework.hw32.common.protobuf.generated.MessageProto;
import ru.otus.homework.hw32.common.protobuf.generated.MessageSystemProtobufGrpc;
import ru.otus.homework.hw32.common.protobuf.generated.MsClientMeta;
import ru.otus.homework.hw32.common.protobuf.generated.Session;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageProtobufConverter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class TransportByGRPC implements TransportForMessageSystem {

    private final ManagedChannel managedChannel;
    private final MessageSystemProtobufGrpc.MessageSystemProtobufStub stub;
    private final MessageSystemProtobufGrpc.MessageSystemProtobufBlockingStub blockStub;
    private final List<TransportListener> listeners = new CopyOnWriteArrayList<>();
    private final MessageProtobufConverter converter;
    private final Session session = Session.newBuilder().setUuid(UUID.randomUUID().toString()).build();

    public TransportByGRPC(ManagedChannel managedChannel, MessageProtobufConverter converter) {
        this.managedChannel = managedChannel;
        this.stub = MessageSystemProtobufGrpc.newStub(managedChannel);
        this.blockStub = MessageSystemProtobufGrpc.newBlockingStub(managedChannel);
        this.converter = converter;
        initHandler();
    }

    @Override
    public boolean sendNewMessage(Message msg) {
        blockStub.newMessage(converter.convert(msg));
        return true;
    }

    @Override
    public void addListener(TransportListener o) {
        listeners.add(o);
    }

    @Override
    public void removeListener(TransportListener o) {
        listeners.remove(o);
    }

    @Override
    public int sendCurrentQueueSize() {
        return blockStub.currentQueueSize(Empty.newBuilder().build()).getValue();
    }

    @Override
    public void sendRemoveClient(String clientId) {
        blockStub.removeClient(MsClientMeta.newBuilder()
                .setName(clientId)
                .setSession(session)
                .build());
    }

    @Override
    public void sendAddClient(String name) {
        blockStub.addClient(MsClientMeta.newBuilder()
                .setName(name)
                .setSession(session)
                .build());
    }

    @Override
    public boolean isConnected() {
        switch (managedChannel.getState(false)) {
            case READY:
            case IDLE:
            case CONNECTING:
                return true;
            default:
                return false;
        }
    }

    private void initHandler() {
        stub.getHandler(session, new StreamObserver<>() {
            @Override
            public void onNext(MessageProto value) {
                Message msg = converter.convert(value);
                for (TransportListener listener : listeners) {
                    try {
                        listener.handle(msg);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("", t);
            }

            @Override
            public void onCompleted() {
            }
        });
    }

    public void dispose() {
        stub.releaseHandler(session, null);
    }
}

