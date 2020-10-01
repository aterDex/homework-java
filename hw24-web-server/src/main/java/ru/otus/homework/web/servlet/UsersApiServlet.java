package ru.otus.homework.web.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import ru.otus.homework.data.core.model.User;
import ru.otus.homework.data.core.service.DBServiceUser;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UsersApiServlet extends HttpServlet {

    private final DBServiceUser dbServiceUser;
    private final Gson gson;

    public UsersApiServlet(DBServiceUser dbServiceUser, Gson gson) {
        if (dbServiceUser == null) {
            throw new IllegalArgumentException("dbServiceUser mustn't be null.");
        }
        if (gson == null) {
            throw new IllegalArgumentException("gson mustn't be null.");
        }
        this.dbServiceUser = dbServiceUser;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getPathInfo() == null || "/".equals(request.getPathInfo())) {
            response.setContentType("application/json;charset=UTF-8");
            gson.toJson(dbServiceUser.getUsers(), response.getWriter());
            return;
        }
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            dbServiceUser.saveUser(gson.fromJson(request.getReader(), User.class));
        } catch (JsonParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
