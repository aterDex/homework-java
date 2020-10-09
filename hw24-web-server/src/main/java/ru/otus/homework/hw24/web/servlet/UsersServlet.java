package ru.otus.homework.hw24.web.servlet;

import ru.otus.homework.hw24.web.services.TemplateProcessor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class UsersServlet extends HttpServlet {

    private static final String USERS_PAGE_TEMPLATE = "users.html";

    private final TemplateProcessor templateProcessor;

    public UsersServlet(TemplateProcessor templateProcessor) {
        if (templateProcessor == null) {
            throw new IllegalArgumentException("templateProcessor mustn't be null.");
        }
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        templateProcessor.writePage(USERS_PAGE_TEMPLATE, Collections.emptyMap(), response.getWriter());
    }
}
