package ru.otus.homework.hw26.data.hibernate.sessionmanager;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.otus.homework.hw26.data.core.sessionmanager.DatabaseSession;

public class DatabaseSessionHibernate implements DatabaseSession {
    private final Session session;
    private final Transaction transaction;

    DatabaseSessionHibernate(Session session) {
        this.session = session;
        this.transaction = session.beginTransaction();
    }

    public Session getHibernateSession() {
        return session;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void close() {
        if (transaction.isActive()) {
            transaction.commit();
        }
        session.close();
    }
}
