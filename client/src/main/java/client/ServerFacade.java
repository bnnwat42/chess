package client;

import io.javalin.Javalin;

public class ServerFacade {

    private final Javalin javalinObj;

    public ServerFacade() {
        javalinObj = Javalin.create(config -> config.staticFiles.add("web"));
    }

    public int run(int desiredPort) {
        javalinObj.start(desiredPort);
        return javalinObj.port();
    }

    public void clearDatabase() {

    }

    public void registerUser() {

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
