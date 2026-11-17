package client;

import chess.ChessGame;
import client.exceptions.AlreadyTakenException;
import client.exceptions.BadRequestException;
import client.exceptions.UnauthorizedException;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import io.javalin.Javalin;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class ServerFacade {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    String boilerURL;


    public ServerFacade(String host, int port) {
        boilerURL = "http://" + host + ":" + port;
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException, AlreadyTakenException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        switch (response.statusCode()) {
            case 200:
                return response;
            case 400:
                System.out.println("Bad request. Please modify input");
                throw new BadRequestException();
            case 401:
                System.out.println("Unauthorized action. Please check your inputs");
                throw new UnauthorizedException();
            case 403:
                System.out.println("Already taken. Please choose another");
                throw new AlreadyTakenException();
            case 500:
                System.out.println("Server error. Please try again.");
                throw new RuntimeException();
            default:
                System.out.println("Unknown error. Please try again.");
                throw new RuntimeException("Unknown error");
        }
    }

    public void clearDatabase() {
        String url = boilerURL + "/db";
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder().uri(new URI(url))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .DELETE().build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        try {
            sendRequest(request);
        } catch (IOException | InterruptedException | AlreadyTakenException e) {
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
        } catch (URISyntaxException | AlreadyTakenException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String loginUser(UserData user) {
        String url = boilerURL + "/session";
        String body = new Gson().toJson(
                Map.of("username", user.username(),
                        "password", user.password()
                ));
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            var responseBody = sendRequest(request).body();
            Map<String, String> myMap = new Gson().fromJson(responseBody, Map.class);
            return myMap.get("authToken");
        } catch (URISyntaxException | InterruptedException | AlreadyTakenException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void logoutUser() {
        String url = boilerURL + "/session";
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .DELETE()
                    .build();

            sendRequest(request);
        } catch (URISyntaxException | IOException | InterruptedException | AlreadyTakenException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<GameData> listGames(String authToken) {
        String url = boilerURL + "/game";
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .GET()
                    .header("Authorization", authToken)
                    .build();

            var responseBody = sendRequest(request).body();
            Gson gson = new Gson();

            Type type = new TypeToken<Map<String, ArrayList<GameData>>>() {
            }.getType();
            Map<String, ArrayList<GameData>> myMap = gson.fromJson(responseBody, type);
            return myMap.get("games");
        } catch (URISyntaxException | IOException | InterruptedException | AlreadyTakenException e) {
            throw new RuntimeException(e);
        }
    }

    public int createGame(String authToken, String gameName) {
        String url = boilerURL + "/game";
        String body = new Gson().toJson(
                Map.of(
                        "gameName", gameName
                ));
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Authorization", authToken)
                    .build();

            var responseBody = sendRequest(request).body();
            Map myMap = new Gson().fromJson(responseBody, Map.class);
            return ((Number) myMap.get("gameID")).intValue();
        } catch (URISyntaxException | InterruptedException | AlreadyTakenException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void joinGame(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        String url = boilerURL + "/game";
        String body = new Gson().toJson(
                Map.of(
                        "gameID", gameID,
                        "playerColor", playerColor
                ));
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .header("Authorization", authToken)
                    .build();

            sendRequest(request).body();
        } catch (URISyntaxException | InterruptedException | AlreadyTakenException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
