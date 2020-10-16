package ru.otus.homework.hw25.config2;

import ru.otus.homework.hw25.appcontainer.api.AppComponent;
import ru.otus.homework.hw25.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.homework.hw25.services.*;

@AppComponentsContainerConfig(order = 1)
public class AppConfig2 {

    @AppComponent(order = 0, name = "playerService")
    public PlayerService playerService(IOService ioService) {
        return new PlayerServiceImpl(ioService);
    }

    @AppComponent(order = 1, name = "gameProcessor")
    public GameProcessor gameProcessor(IOService ioService,
                                       PlayerService playerService,
                                       EquationPreparer equationPreparer) {
        return new GameProcessorImpl(ioService, equationPreparer, playerService);
    }

}
