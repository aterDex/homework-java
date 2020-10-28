package ru.otus.homework.hw30;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByMonitor<T> {

    private static final Logger log = LoggerFactory.getLogger(ByMonitor.class);

    private Iterable<T> seq;
    private T voice = null;

    public void start(Iterable<T> seq) {
        this.seq = seq;
        Thread th1 = new Thread(this::model, "Thread1");
        Thread th2 = new Thread(this::mimic, "Thread2");

        th1.start();
        th2.start();
        try {
            th1.join();
            th2.interrupt();
            th2.join();
        } catch (InterruptedException e) {
            log.error("", e);
            th1.interrupt();
            th2.interrupt();
        }
    }

    private synchronized void model() {
        try {
            for (T i : seq) {
                while (voice != null) {
                    this.wait();
                }
                log.info(String.valueOf(i));
                voice = i;
                this.notifyAll();
            }
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }

    private synchronized void mimic() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                while (voice == null) {
                    this.wait();
                }
                log.info(String.valueOf(voice));
                voice = null;
                this.notifyAll();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
