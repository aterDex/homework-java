package ru.otus.homework.hw30;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByMonitorSingleMethod<T> {

    private static final Logger log = LoggerFactory.getLogger(ByMonitorSingleMethod.class);

    private Iterable<T> seq;
    private boolean isFirstSaid = false;

    public void start(Iterable<T> seq) {
        this.seq = seq;
        Thread th1 = new Thread(() -> print(true), "Thread1");
        Thread th2 = new Thread(() -> print(false), "Thread2");

        th1.start();
        th2.start();
        try {
            th1.join();
            th2.join();
        } catch (InterruptedException e) {
            log.error("", e);
            th1.interrupt();
            th2.interrupt();
        }
    }

    private synchronized void print(boolean isFirst) {
        try {
            for (T element : seq) {
                while (isFirst == isFirstSaid) {
                    this.wait();
                }
                log.info(String.valueOf(element));
                isFirstSaid = isFirst;
                this.notifyAll();
            }
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }
}
