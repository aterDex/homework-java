package ru.otus.homework.web.server;

import com.google.gson.Gson;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import ru.otus.homework.data.core.service.DBServiceUser;
import ru.otus.homework.web.services.TemplateProcessor;
import ru.otus.homework.web.servlet.UsersApiServlet;
import ru.otus.homework.web.servlet.UsersServlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsersWebServerWithBasicSecurity implements UsersWebServer {
    private static final String ROLE_NAME_ADMIN = "admin";
    private static final String CONSTRAINT_NAME = "auth";

    private static final String START_PAGE_NAME = "index.html";
    private static final String COMMON_RESOURCES_DIR = UsersWebServerWithBasicSecurity.class.getResource("/html/static").toExternalForm();
    private static final String USERS = "/users";
    private static final String USERS_API = "/api/user/*";

    private final DBServiceUser dbServiceUser;
    private final Gson gson;
    private final TemplateProcessor templateProcessor;
    private final Server server;
    private final LoginService loginService;

    public UsersWebServerWithBasicSecurity(int port, LoginService loginService, DBServiceUser dbServiceUser, Gson gson, TemplateProcessor templateProcessor) {
        this.loginService = loginService;
        this.dbServiceUser = dbServiceUser;
        this.gson = gson;
        this.templateProcessor = templateProcessor;
        this.server = new Server(port);
    }

    @Override
    public void start() throws Exception {
        if (server.getHandlers().length == 0) {
            initContext();
        }
        server.start();
    }

    @Override
    public void join() throws Exception {
        server.join();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }

    private Server initContext() {
        var resourceHandler = createResourceHandler();
        var servletContextHandler = createServletContextHandler();

        var handlers = new HandlerList();
        handlers.addHandler(resourceHandler);
        handlers.addHandler(applySecurity(servletContextHandler, USERS, USERS_API));

        server.setHandler(handlers);
        return server;
    }

    private ResourceHandler createResourceHandler() {
        var resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setWelcomeFiles(new String[]{START_PAGE_NAME});
        resourceHandler.setResourceBase(COMMON_RESOURCES_DIR);
        return resourceHandler;
    }

    private ServletContextHandler createServletContextHandler() {
        var servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.addServlet(new ServletHolder(new UsersServlet(templateProcessor)), USERS);
        servletContextHandler.addServlet(new ServletHolder(new UsersApiServlet(dbServiceUser, gson)), USERS_API);
        return servletContextHandler;
    }

    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        var constraint = new Constraint();
        constraint.setName(CONSTRAINT_NAME);
        constraint.setAuthenticate(true);
        constraint.setRoles(new String[]{ROLE_NAME_ADMIN});

        List<ConstraintMapping> constraintMappings = new ArrayList<>();
        Arrays.stream(paths).forEachOrdered(path -> {
            var mapping = new ConstraintMapping();
            mapping.setPathSpec(path);
            mapping.setConstraint(constraint);
            constraintMappings.add(mapping);
        });

        ConstraintSecurityHandler security = new ConstraintSecurityHandler();
        security.setAuthenticator(new BasicAuthenticator());
        security.setLoginService(loginService);
        security.setConstraintMappings(constraintMappings);
        security.setHandler(new HandlerList(servletContextHandler));
        return security;
    }
}
