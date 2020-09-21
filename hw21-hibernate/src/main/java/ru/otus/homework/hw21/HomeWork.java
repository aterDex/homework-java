package ru.otus.homework.hw21;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.hw21.core.model.AddressDataSet;
import ru.otus.homework.hw21.core.model.PhoneDataSet;
import ru.otus.homework.hw21.core.model.User;
import ru.otus.homework.hw21.core.service.DbServiceUserImpl;
import ru.otus.homework.hw21.hibernate.HibernateUtils;
import ru.otus.homework.hw21.hibernate.dao.UserDaoHibernate;
import ru.otus.homework.hw21.hibernate.sessionmanager.SessionManagerHibernate;

import java.util.ArrayList;
import java.util.List;

public class HomeWork {

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";
    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
        var sessionFactory = HibernateUtils.buildSessionFactory(HIBERNATE_CFG_FILE, User.class, AddressDataSet.class, PhoneDataSet.class);
        try (var sessionManager = new SessionManagerHibernate(sessionFactory)) {
            var dbServiceUser = new DbServiceUserImpl(new UserDaoHibernate(sessionManager));
            var id = dbServiceUser.saveUser(new User(0, "dbServiceUser",
                    new AddressDataSet(0, "address"),
                    new ArrayList<>(List.of(
                            new PhoneDataSet(0, "8439543532"),
                            new PhoneDataSet(0, "3782432874872")
                    ))));
            dbServiceUser.getUser(id).ifPresentOrElse(
                    crUser -> logger.info("user created, {}", crUser.toString()),
                    () -> logger.info("user didn't create")
            );
        }
    }
}
