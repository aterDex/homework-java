package ru.otus.homework.hw22.cache.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.homework.hw22.cachehw.HwCache;
import ru.otus.homework.hw22.core.model.User;
import ru.otus.homework.hw22.core.service.DBServiceUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DBServiceUserCacheTest {

    @Mock
    private DBServiceUser serviceUser;
    @Mock
    private HwCache hwCache;

    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new DBServiceUserCache(null, null));
        assertThrows(IllegalArgumentException.class, () -> new DBServiceUserCache(serviceUser, null));
        assertThrows(IllegalArgumentException.class, () -> new DBServiceUserCache(null, hwCache));
    }

    @Test
    void saveUser() {
        var cache = new DBServiceUserCache(serviceUser, hwCache);
        var user = new User();

        when(serviceUser.saveUser(eq(user))).thenReturn(10002L);
        assertEquals(10002L, cache.saveUser(user));
        verify(serviceUser).saveUser(eq(user));
        verifyNoMoreInteractions(serviceUser, hwCache);
    }

    @Test
    void getUserEmpty() {
        var cache = new DBServiceUserCache(serviceUser, hwCache);
        var user = new User();
        assertTrue(cache.getUser(10L).isEmpty());
        verify(hwCache).get(eq("10"));
        verify(serviceUser).getUser(eq(10L));
        verifyNoMoreInteractions(serviceUser, hwCache);
    }

    @Test
    void getUserFromCache() {
        var cache = new DBServiceUserCache(serviceUser, hwCache);
        var user = new User();
        when(hwCache.get(eq("10"))).thenReturn(user);
        assertEquals(user, cache.getUser(10L).get());
        verify(hwCache).get(eq("10"));
        verifyNoMoreInteractions(serviceUser, hwCache);
    }

    @Test
    void getUserFromServices() {
        var cache = new DBServiceUserCache(serviceUser, hwCache);
        var user = new User();
        when(serviceUser.getUser(eq(10L))).thenReturn(Optional.of(user));
        assertEquals(user, cache.getUser(10L).get());
        verify(hwCache).get(eq("10"));
        verify(serviceUser).getUser(eq(10L));
        verify(hwCache).put(eq("10"), eq(user));
        verifyNoMoreInteractions(serviceUser, hwCache);
    }
}