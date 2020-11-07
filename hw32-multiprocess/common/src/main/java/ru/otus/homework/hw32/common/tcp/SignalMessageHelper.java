package ru.otus.homework.hw32.common.tcp;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.function.Function;

@Slf4j
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
                return converter.apply(answer);
            }
            return null;
        }
        throw new RuntimeException("Unknown answer: " + answer);
    }

    static Signal answerOk(Signal signal) {
        return answerOk(signal, null);
    }

    static Signal answerOk(Signal signal, Serializable body) {
        Signal answer = new Signal(signal.getTag(), signal.getUuid(), body);
        log.debug("signal: '{}' answer: '{}'", signal, answer);
        return answer;
    }

    static Signal answerError(Signal signal, String error) {
        Signal answer = new Signal("error", signal.getUuid(), error);
        log.debug("signal: '{}' answer: '{}'", signal, answer);
        return answer;
    }
}
