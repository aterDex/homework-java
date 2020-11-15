package ru.otus.messagesystem.message;

import com.google.protobuf.ByteString;
import ru.otus.homework.hw32.common.protobuf.generated.CallbackProtoId;
import ru.otus.homework.hw32.common.protobuf.generated.MessageProto;
import ru.otus.homework.hw32.common.protobuf.generated.MessageProtoId;
import ru.otus.messagesystem.client.CallbackId;

public class MessageProtobufConverter {

    public Message convert(MessageProto proto) {
        return new Message(
                new MessageId(proto.getId().getId()),
                proto.getFrom(),
                proto.getTo(),
                proto.getSourceMessageId().getId() == "" ? null :
                        new MessageId(proto.getSourceMessageId().getId()),
                proto.getType(),
                proto.getPayload().isEmpty() ? null :
                        proto.getPayload().toByteArray(),
                new CallbackId(proto.getCallbackId().getId())
        );
    }

    public MessageProto convert(Message message) {
        return MessageProto.newBuilder()
                .setId(MessageProtoId.newBuilder().setId(message.getId().getId()).build())
                .setFrom(message.getFrom())
                .setTo(message.getTo())
                .setSourceMessageId(MessageProtoId.newBuilder().setId(
                        message.getSourceMessageId().isPresent() ?
                                message.getSourceMessageId().get().getId() : ""
                ).build())
                .setType(message.getType())
                .setPayload(message.getPayload() == null ? ByteString.EMPTY : ByteString.copyFrom(message.getPayload()))
                .setCallbackId(CallbackProtoId.newBuilder().setId(message.getCallbackId().getId()).build())
                .build();
    }
}
