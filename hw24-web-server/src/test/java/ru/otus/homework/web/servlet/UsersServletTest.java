package ru.otus.homework.web.servlet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.homework.web.services.TemplateProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class UsersServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private TemplateProcessor templateProcessor;

    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new UsersServlet(null));
    }

    @Test
    void doGetOk() throws IOException {
        var servlet = new UsersServlet(templateProcessor);
        servlet.doGet(request, response);

        verify(response).setContentType(eq("text/html"));
        verify(response).getWriter();
        verifyNoMoreInteractions(response, request);
    }
}