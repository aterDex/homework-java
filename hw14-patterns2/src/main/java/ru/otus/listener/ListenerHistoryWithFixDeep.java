package ru.otus.listener;

import ru.otus.Message;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

public class ListenerHistoryWithFixDeep implements Listener {

    private final Deque<HistoryRecord> history = new LinkedList<>();
    private final Collection<HistoryRecord> unmodifiableHistory = Collections.unmodifiableCollection(history);
    private final int maxDeep;

    public ListenerHistoryWithFixDeep(int maxDeep) {
        this.maxDeep = maxDeep;
    }

    public ListenerHistoryWithFixDeep() {
        this(100);
    }

    @Override
    public void onUpdated(Message oldMsg, Message newMsg) {
        while (history.size() >= maxDeep)
            history.removeFirst();
        history.add(new HistoryRecord(oldMsg, newMsg));
    }

    public Collection<HistoryRecord> getHistory() {
        return unmodifiableHistory;
    }
}
