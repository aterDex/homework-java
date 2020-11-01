package ru.otus.homework.hw31;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.otus.homework.hw31.data.core.service.DBServiceUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
@WebAppConfiguration
public class IntegrationTest {

    private static final String DEFAULT_USERS =
            "[{\"id\":1,\"name\":\"Администратор\",\"login\":\"admin\",\"password\":\"password\"}," +
                    "{\"id\":2,\"name\":\"Пользователь\",\"login\":\"user\",\"password\":\"123\"}]";
    private static final String NEW_USER = "{\"id\":0,\"name\":\"N\",\"login\":\"L\",\"password\":\"P\"}";

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private DBServiceUser dbServiceUser;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testDefaultUsers() throws Exception {
        mvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(content().json(DEFAULT_USERS));
    }

    @Test
    void testSaveUsers() throws Exception {
        mvc.perform(
                post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NEW_USER))
                .andExpect(status().isOk());
        var user = dbServiceUser.findByLogin("L");
        assertTrue(user.isPresent());
        assertEquals("N", user.get().getName());
        assertEquals("P", user.get().getPassword());
    }

    @Test
    void testStaticContent() throws Exception {
        mvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML));
        mvc.perform(get("/res/img/logo.svg"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/svg+xml"));
    }

    @Test
    void testUsersTemplate() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    void testRedirectRoot() throws Exception {
        mvc.perform(get("/"))
                .andExpect(redirectedUrl("/index.html"));
    }

    @Test
    void testNotFound() throws Exception {
        mvc.perform(get("/pageWhichNotFound"))
                .andExpect(status().isNotFound());
    }
}
