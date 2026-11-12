package client;

import com.google.gson.Gson;
import io.javalin.Javalin;
import model.UserData;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Map;

public class ServerFacade {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    String boilerURL;


    public ServerFacade(String host, int port) {
        boilerURL = "http://" + host + ":" + port;
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        switch (response.statusCode()) {
            case 200:
                return response;
            case 400: //BadRequestException
                throw new RuntimeException();
            case 401: //UnauthorizedException
                throw new RuntimeException();
            case 403: //AlreadyTakenException
                throw new RuntimeException();
            case 500: //Other
                throw new RuntimeException();
            default:
                throw new RuntimeException("Unknown error");
        }
    }

    public void clearDatabase() {
        String url = boilerURL + "/db";
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .DELETE().build();

            sendRequest(request);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String registerUser(UserData user) {
        String url = boilerURL + "/user";
        String body = new Gson().toJson(
                Map.of("username", user.username(),
                        "password", user.password(),
                        "email", user.email()
                ));
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            var responseBody = sendRequest(request).body();
            Map<String, String> myMap = new Gson().fromJson(responseBody, Map.class);
            return myMap.get("authToken");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void loginUser() {

    }

    public void logoutUser() {

    }

    public void listGames() {

    }

    public void createGame() {

    }

    public void joinGame() {

    }
}
