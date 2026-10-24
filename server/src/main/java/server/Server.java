package server;

import Handlers.GameHandler;
import Handlers.UserHandler;
import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import io.javalin.*;
import io.javalin.http.*;
import model.AuthData;
import model.GameData;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;


public class Server {

    private final Javalin javalinObj;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;

    public Server() {
        userHandler = new UserHandler();
        gameHandler = new GameHandler();
        javalinObj = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalinObj.delete("/db", context -> clearDatabase(context));
        javalinObj.post("/user", context -> registerUser(context));
        javalinObj.post("/session", context -> loginUser(context));
        javalinObj.delete("/session", context -> logoutUser(context));
        javalinObj.get("/game", context -> listGames(context));
        javalinObj.post("/game", context -> createGame(context));
        javalinObj.put("/game", context -> joinGame(context));
    }

    private void joinGame(@NotNull Context ctx) {
        try {
            gameHandler.joinGame(ctx);
            ctx.status(200);
            ctx.result("{}");
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void createGame(@NotNull Context ctx) {
        try {
            int id = gameHandler.createGame(ctx);
            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("gameID", id)));
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void listGames(Context ctx) {
        try {
            String gameList = gameHandler.listGames(ctx);
            ctx.status(200);
            ctx.result(gameList);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void logoutUser(Context ctx) {
        try {
            userHandler.logoutUser(ctx);
            ctx.status(200);
            ctx.result("{}");
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void loginUser(Context ctx) {
        try {
            String newAuth = userHandler.loginUser(ctx);
            ctx.status(200);
            ctx.result(newAuth);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void registerUser(Context ctx) {
        try {
            String newAuth = userHandler.registerUser(ctx);
            ctx.status(200);
            ctx.result(newAuth);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void clearDatabase(Context ctx) {//throws ResponseException {
        try {
            userHandler.clear();
            gameHandler.clear();
            ctx.status(200);
            ctx.result("{}");
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void handleException(Context ctx, Exception e) {
        if (e.getClass() == BadRequestException.class) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", "Error: bad request")));
        } else if (e.getClass() == UnauthorizedException.class) {
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
        } else if (e.getClass() == AlreadyTakenException.class) {
            ctx.status(403);
            ctx.result(new Gson().toJson(Map.of("message", "Error: already taken")));
        } else {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", String.format("Error: %s", e))));
        }
    }

    public int run(int desiredPort) {
        javalinObj.start(desiredPort);
        return javalinObj.port();
    }

    public void stop() {
        javalinObj.stop();
    }
}