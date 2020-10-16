package ru.otus.homework.hw25;

import ru.otus.homework.hw25.appcontainer.AppComponentsContainerImpl;
import ru.otus.homework.hw25.appcontainer.api.AppComponentsContainer;
import ru.otus.homework.hw25.config.AppConfig;
import ru.otus.homework.hw25.config2.AppConfig1;
import ru.otus.homework.hw25.config2.AppConfig2;
import ru.otus.homework.hw25.services.GameProcessor;
import ru.otus.homework.hw25.services.GameProcessorImpl;

import java.util.Scanner;

public class App {

    public static void main(String[] args) throws Exception {
        System.out.println("How to init AppComponentsContainer?");
        System.out.println("1 - AppConfig.class (default)");
        System.out.println("2 - AppConfig1.class, AppConfig2.class");
        System.out.println("3 - \"ru.otus.homework.hw25.config.\"");
        System.out.println("4 - \"ru.otus.homework.hw25.config2.\"");
        System.out.print("> ");
        var scanner = new Scanner(System.in);
        AppComponentsContainer container = initContainer(scanner.next());

        System.out.println("How to get GameProcessor?");
        System.out.println("1 - GameProcessor.class (default)");
        System.out.println("2 - GameProcessorImpl.class");
        System.out.println("3 - \"gameProcessor\"");
        System.out.print("> ");
        GameProcessor gameProcessor = initGameProcessor(container, scanner.next());

        gameProcessor.startGame();
    }

    private static AppComponentsContainer initContainer(String val) {
        switch (val) {
            default:
            case "1":
                return new AppComponentsContainerImpl(AppConfig.class);
            case "2":
                return new AppComponentsContainerImpl(AppConfig1.class, AppConfig2.class);
            case "3":
                return new AppComponentsContainerImpl("ru.otus.homework.hw25.config.");
            case "4":
                return new AppComponentsContainerImpl("ru.otus.homework.hw25.config2.");
        }
    }

    private static GameProcessor initGameProcessor(AppComponentsContainer container, String val) {
        switch (val) {
            default:
            case "1":
                return container.getAppComponent(GameProcessor.class);
            case "2":
                return container.getAppComponent(GameProcessorImpl.class);
            case "3":
                return container.getAppComponent("gameProcessor");
        }
    }
}
