package ru.otus.homework.web.servlet;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.homework.data.core.model.User;
import ru.otus.homework.data.core.service.DBServiceUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersApiServletTest {

    @Mock
    private DBServiceUser dbServiceUser;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    private Gson gson = new Gson();

    static Stream<String> allowsPaths() {
        return Stream.of("/", null);
    }

    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new UsersApiServlet(null, null));
        assertThrows(IllegalArgumentException.class, () -> new UsersApiServlet(dbServiceUser, null));
        assertThrows(IllegalArgumentException.class, () -> new UsersApiServlet(null, gson));
    }

    @Test
    void doGetBadRequest() throws IOException {
        when(request.getPathInfo()).thenReturn("/anyPath");

        new UsersApiServlet(dbServiceUser, gson).doGet(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST));
        verifyNoMoreInteractions(dbServiceUser, response);
    }

    @ParameterizedTest
    @MethodSource("allowsPaths")
    void doGetRequest(String path) throws IOException {
        try (var baseWriter = new StringWriter();
             var writer = new PrintWriter(baseWriter)) {

            when(request.getPathInfo()).thenReturn(path);
            when(dbServiceUser.getUsers()).thenReturn(Collections.emptyList());
            when(response.getWriter()).thenReturn(writer);
            new UsersApiServlet(dbServiceUser, gson).doGet(request, response);

            assertEquals("[]", baseWriter.toString());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"error", "[]", "[{}]"})
    void doPostError(String text) throws IOException {
        try (var baseReader = new StringReader(text);
             var reader = new BufferedReader(baseReader)) {

            when(request.getReader()).thenReturn(reader);

            new UsersApiServlet(dbServiceUser, gson).doPost(request, response);

            verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST));
            verifyNoMoreInteractions(dbServiceUser, response);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"{name: \"n\", login: \"l\", password: \"p\"}", "{}"})
    void doPostOk(String userJson) throws IOException {
        try (var baseReader = new StringReader(userJson);
             var reader = new BufferedReader(baseReader)) {

            when(request.getReader()).thenReturn(reader);

            new UsersApiServlet(dbServiceUser, gson).doPost(request, response);

            verify(dbServiceUser).saveUser(any(User.class));
            verifyNoMoreInteractions(dbServiceUser, response);
        }
    }
}