package ru.otus.homework.hw32.common.tcp;

import java.io.Serializable;
import java.util.function.Function;

class SignalMessageHelper {

    private SignalMessageHelper() {
    }

    static <R> R processAnswer(Signal signal, Function<Signal, Signal> sender, Function<Signal, R> converter) {
        Signal answer = sender.apply(signal);
        if ("error".equals(answer.getTag())) {
            throw new RuntimeException(String.valueOf(answer.getBody()));
        }
        if (signal.getTag().equals(answer.getTag())) {
            if (converter != null) {
                return converter.apply(signal);
            }
            return null;
        }
        throw new RuntimeException("Unknown answer: " + answer);
    }

    static Signal answerOk(Signal signal) {
        return answerOk(signal, null);
    }

    static Signal answerOk(Signal signal, Serializable body) {
        return new Signal(signal.getTag(), signal.getUuid(), body);
    }

    static Signal answerError(Signal signal, String error) {
        return new Signal("error", signal.getUuid(), error);
    }
}
