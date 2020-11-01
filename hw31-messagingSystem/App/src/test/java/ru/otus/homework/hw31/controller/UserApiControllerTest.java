package ru.otus.homework.hw31.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.otus.homework.hw31.data.core.model.User;
import ru.otus.homework.hw31.data.core.service.DBServiceUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserApiControllerTest {

    private MockMvc mvc;

    @Mock
    private DBServiceUser dbServiceUser;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new UserApiController(dbServiceUser)).build();
    }

    @Test
    void getUsers() throws Exception {
        var user0 = new User();
        user0.setId(0);
        user0.setName("A");
        var user1 = new User();
        user1.setId(1);
        user1.setName("B");
        var list = List.<User>of(user0, user1);
        var mapper = new ObjectMapper();
        when(dbServiceUser.getUsers()).thenReturn(list);

        mvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));
    }

    @Test
    void addUser() throws Exception {
        mvc.perform(
                post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":100,\"name\":\"N\",\"login\":\"L\",\"password\":\"P\"}"))
                .andExpect(status().isOk());
        var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(dbServiceUser).saveUser(userCaptor.capture());
        assertEquals(100, userCaptor.getValue().getId());
        assertEquals("N", userCaptor.getValue().getName());
        assertEquals("L", userCaptor.getValue().getLogin());
        assertEquals("P", userCaptor.getValue().getPassword());
        verifyNoMoreInteractions(dbServiceUser);
    }
}