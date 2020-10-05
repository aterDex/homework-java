package ru.otus.homework.hw25;

import ru.otus.homework.hw25.appcontainer.AppComponentsContainerImpl;
import ru.otus.homework.hw25.appcontainer.api.AppComponentsContainer;
import ru.otus.homework.hw25.config.AppConfig;
import ru.otus.homework.hw25.services.GameProcessor;

/*
В классе AppComponentsContainerImpl реализовать обработку, полученной в конструкторе конфигурации,
основываясь на разметке аннотациями из пакета appcontainer. Так же необходимо реализовать методы getAppComponent.
В итоге должно получиться работающее приложение. Менять можно только класс AppComponentsContainerImpl.

PS Приложение представляет из себя тренажер таблицы умножения)
*/

public class App {

    public static void main(String[] args) throws Exception {
        // Опциональные варианты
        //AppComponentsContainer container = new AppComponentsContainerImpl(AppConfig1.class, AppConfig2.class);

        // Тут можно использовать библиотеку Reflections (см. зависимости)
        //AppComponentsContainer container = new AppComponentsContainerImpl("ru.otus.config");

        // Обязательный вариант
        AppComponentsContainer container = new AppComponentsContainerImpl(AppConfig.class);

        // Приложение должно работать в каждом из указанных ниже вариантов
        GameProcessor gameProcessor = container.getAppComponent(GameProcessor.class);
        //GameProcessor gameProcessor = container.getAppComponent(GameProcessorImpl.class);
        //GameProcessor gameProcessor = container.getAppComponent("gameProcessor");

        gameProcessor.startGame();
    }
}
