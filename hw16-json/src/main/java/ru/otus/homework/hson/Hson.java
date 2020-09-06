package ru.otus.homework.hson;

import ru.otus.homework.hson.adpter.StringBuilderAdapter;
import ru.otus.homework.hson.executor.DefaultExecutor;
import ru.otus.homework.hson.executor.ProcessExecutor;

public final class Hson {

    private final ProcessExecutor processExecutor;

    public Hson() {
        this(DefaultExecutor.getExecutor());
    }

    public Hson(ProcessExecutor processExecutor) {
        if (processExecutor == null) throw new IllegalArgumentException("processExecutor mustn't be null.");
        this.processExecutor = processExecutor;
    }

    public String toJson(Object obj) {
        var adapter = new StringBuilderAdapter();
        processExecutor.execute(obj, adapter);
        return adapter.toString();
    }
}
