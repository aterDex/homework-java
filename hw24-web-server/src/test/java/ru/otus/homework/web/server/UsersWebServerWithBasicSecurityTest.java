package ru.otus.homework.web.server;

import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.util.security.Password;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.otus.homework.data.core.service.DBServiceUser;
import ru.otus.homework.web.services.TemplateProcessor;
import ru.otus.homework.web.services.TemplateProcessorImpl;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class UsersWebServerWithBasicSecurityTest {

    private static final int WEB_SERVER_PORT = 18201;
    private static final String TEMPLATES_DIR = "/html/templates/";
    private static final String ADMIN = "admin";
    private static final String ADMIN_PASSWORD = "admin_438DSJKH37dd";
    private static final String USER = "user";
    private static final String USER_PASSWORD = "user_438DSJKH37dd";
    private static final String USER_PAGE = "/users";
    private static final String USER_API_PAGE = "/api/user";
    private static final String WEB_SERVER_URL = "http://localhost:" + WEB_SERVER_PORT;
    private static final String AUTH_HEADER_ADMIN = "Basic " + Base64.getEncoder().encodeToString((ADMIN + ":" + ADMIN_PASSWORD).getBytes());
    private static final String AUTH_HEADER_ADMIN_WRONG_PASSWORD = "Basic " + Base64.getEncoder().encodeToString((ADMIN + ":" + ADMIN_PASSWORD + "suffix").getBytes());
    private static final String AUTH_HEADER_USER = "Basic " + Base64.getEncoder().encodeToString((USER + ":" + USER_PASSWORD).getBytes());
    private static final String AUTH_HEADER_UNKNOWN_USER = "Basic " + Base64.getEncoder().encodeToString(("KKK:SAS").getBytes());

    private static UsersWebServerWithBasicSecurity webServer;
    private static LoginService loginService;
    private static DBServiceUser dbServiceUser;
    private static TemplateProcessor templateProcessor;

    private HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();

    @BeforeAll
    static void beforeAll() throws Exception {
        loginService = new TestLoginService();
        dbServiceUser = mock(DBServiceUser.class);
        templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);

        webServer = new UsersWebServerWithBasicSecurity(WEB_SERVER_PORT, loginService, dbServiceUser, new Gson(), templateProcessor);
        webServer.start();
    }

    @AfterAll
    static void afterAll() throws Exception {
        webServer.stop();
    }

    static Stream<HttpRequest> onlyAdminRequests(Consumer<HttpRequest.Builder> builderConsumer) {
        return Stream.of(
                HttpRequest.newBuilder().GET()
                        .uri(URI.create(WEB_SERVER_URL + USER_PAGE)),
                HttpRequest.newBuilder().GET()
                        .uri(URI.create(WEB_SERVER_URL + USER_API_PAGE)),
                HttpRequest.newBuilder().GET()
                        .uri(URI.create(WEB_SERVER_URL + USER_API_PAGE + "/")),
                HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString("{}"))
                        .uri(URI.create(WEB_SERVER_URL + USER_API_PAGE))
        ).map(x -> {
            if (builderConsumer != null) {
                builderConsumer.accept(x);
            }
            return x.build();
        });
    }

    static Stream<HttpRequest> adminsRequestsWithoutAuth() {
        return onlyAdminRequests(null);
    }

    static Stream<HttpRequest> adminsRequests() {
        return onlyAdminRequests(x -> x.header(HttpHeader.AUTHORIZATION.asString(), AUTH_HEADER_ADMIN));
    }

    static Stream<HttpRequest> userRequests() {
        return onlyAdminRequests(x -> x.header(HttpHeader.AUTHORIZATION.asString(), AUTH_HEADER_USER));
    }

    static Stream<HttpRequest> adminsRequestsWrongPassword() {
        return onlyAdminRequests(x -> x.header(HttpHeader.AUTHORIZATION.asString(), AUTH_HEADER_ADMIN_WRONG_PASSWORD));
    }

    static Stream<HttpRequest> unknownUserRequests() {
        return onlyAdminRequests(x -> x.header(HttpHeader.AUTHORIZATION.asString(), AUTH_HEADER_UNKNOWN_USER));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "", "/"})
    void defaultAndIndexPage(String path) throws Exception {
        var request = HttpRequest.newBuilder().GET()
                .uri(URI.create(WEB_SERVER_URL + path))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HttpURLConnection.HTTP_OK);

        var document = Jsoup.parse(response.body());
        assertEquals("Index", document.select("title").first().text());
        assertEquals(1, document.select("html > body > div.top").size());
    }

    @Test
    void checkImgResource() throws Exception {
        var request = HttpRequest.newBuilder().GET()
                .uri(URI.create(WEB_SERVER_URL + "/img/logo.svg"))
                .build();
        HttpResponse<Void> response = http.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(response.statusCode(), HttpURLConnection.HTTP_OK);
    }

    @Test
    void checkNotFoundResource() throws Exception {
        var request = HttpRequest.newBuilder().GET()
                .uri(URI.create(WEB_SERVER_URL + "/unknownResources"))
                .build();
        HttpResponse<Void> response = http.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(response.statusCode(), HttpURLConnection.HTTP_NOT_FOUND);
    }

    @ParameterizedTest
    @MethodSource("adminsRequestsWithoutAuth")
    void unauthorized(HttpRequest request) throws Exception {
        HttpResponse<Void> response = http.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(response.statusCode(), HttpURLConnection.HTTP_UNAUTHORIZED);
    }

    @ParameterizedTest
    @MethodSource("adminsRequests")
    void authorized(HttpRequest request) throws Exception {
        HttpResponse<Void> response = http.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(response.statusCode(), HttpURLConnection.HTTP_OK);
    }

    @ParameterizedTest
    @MethodSource("userRequests")
    void forbidden(HttpRequest request) throws Exception {
        HttpResponse<Void> response = http.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(response.statusCode(), HttpURLConnection.HTTP_FORBIDDEN);
    }

    @ParameterizedTest
    @MethodSource("adminsRequestsWrongPassword")
    void unauthorizedWrong(HttpRequest request) throws Exception {
        HttpResponse<Void> response = http.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(response.statusCode(), HttpURLConnection.HTTP_UNAUTHORIZED);
    }

    @ParameterizedTest
    @MethodSource("unknownUserRequests")
    void unauthorizedUnknownUser(HttpRequest request) throws Exception {
        HttpResponse<Void> response = http.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(response.statusCode(), HttpURLConnection.HTTP_UNAUTHORIZED);
    }

    private static class TestLoginService extends AbstractLoginService {

        private Map<String, UserPrincipal> maps = Map.of(
                ADMIN, new UserPrincipal(ADMIN, new Password(ADMIN_PASSWORD)),
                USER, new UserPrincipal(USER, new Password(USER_PASSWORD))
        );

        @Override
        protected String[] loadRoleInfo(UserPrincipal user) {
            return "admin".equals(user.getName()) ? new String[]{"admin", "user"} : new String[]{"user"};
        }

        @Override
        protected UserPrincipal loadUserInfo(String username) {
            return maps.get(username);
        }
    }
}