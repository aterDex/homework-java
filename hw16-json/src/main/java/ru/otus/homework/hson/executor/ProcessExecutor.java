package ru.otus.homework.hson.executor;

import ru.otus.homework.hson.adpter.BuilderJsonAdapter;

public interface ProcessExecutor {

    void execute(Object value, BuilderJsonAdapter builder);
}
