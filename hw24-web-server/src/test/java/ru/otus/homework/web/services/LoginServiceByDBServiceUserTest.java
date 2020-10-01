package ru.otus.homework.web.services;

import org.eclipse.jetty.security.AbstractLoginService;
import org.junit.jupiter.api.Test;
import ru.otus.homework.data.core.model.User;
import ru.otus.homework.data.core.service.DBServiceUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceByDBServiceUserTest {

    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new LoginServiceByDBServiceUser(null));
    }

    @Test
    void loadRoleInfo() {
        String[] userRole = {"user"};
        String[] adminRole = {"admin", "user"};

        var servicesUser = mock(DBServiceUser.class);
        var service = new LoginServiceByDBServiceUser(servicesUser);
        assertArrayEquals(userRole, service.loadRoleInfo(new AbstractLoginService.UserPrincipal("user", null)));
        assertArrayEquals(adminRole, service.loadRoleInfo(new AbstractLoginService.UserPrincipal("admin", null)));
        assertArrayEquals(userRole, service.loadRoleInfo(new AbstractLoginService.UserPrincipal("Admin", null)));
    }

    @Test
    void loadUserInfo() {
        final var testLogin = "test";
        final var testPassword = "password";

        User user = new User();
        user.setLogin(testLogin);
        user.setPassword(testPassword);

        var servicesUser = mock(DBServiceUser.class);
        when(servicesUser.findByLogin(eq(testLogin))).thenReturn(Optional.of(user));

        var service = new LoginServiceByDBServiceUser(servicesUser);

        AbstractLoginService.UserPrincipal principalOk = service.loadUserInfo(testLogin);

        assertEquals(testLogin, principalOk.getName());
        assertTrue(principalOk.authenticate(testPassword));

        assertNull(service.loadUserInfo("notFound"));
    }
}