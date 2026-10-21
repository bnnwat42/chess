package server;

import Handlers.UserHandler;
import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import io.javalin.*;
import io.javalin.http.*;
import model.AuthData;
import org.jetbrains.annotations.NotNull;

import java.util.InputMismatchException;
import java.util.Map;


public class Server {

    private final Javalin javalinObj;
    private final UserHandler userHandler;

    public Server() {
        userHandler = new UserHandler();
        javalinObj = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalinObj.delete("/db", context -> clearDatabase(context));
        javalinObj.post("/user", context -> registerUser(context));
        javalinObj.post("/session", context -> loginUser(context));
    }

    private void loginUser(Context ctx) {
        try {
            AuthData newAuth = userHandler.loginUser(ctx);
            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("username", newAuth.username(), "authToken", newAuth.authToken())));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(new Gson().toJson(Map.of("message", "Error: already taken")));
        } catch (InputMismatchException e) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", "Error: bad request")));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", String.format("Error: %s", e))));
        }
    }

    private void registerUser(Context ctx) {
        try {
            AuthData newAuth = userHandler.registerUser(ctx);
            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("username", newAuth.username(), "authToken", newAuth.authToken())));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(new Gson().toJson(Map.of("message", "Error: already taken")));
        } catch (InputMismatchException e) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", "Error: bad request")));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", String.format("Error: %s", e))));
        }
    }

    private void clearDatabase(Context ctx) {//throws ResponseException {
        try {
            userHandler.clear();
            ctx.status(200);
            ctx.result("{}");
        } catch (Exception e) {
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

    /*private void listNames(Context context) {
        String jsonNames = new Gson().toJson(Map.of("name", names));

        context.contentType("application/json");
        context.result(jsonNames);
    }*/
}
/*
import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", ctx -> ctx.result("{}"));
        javalin.post("/user", Server::register);
    }

    private static void register(Context ctx) {
        var result = Map.of("username", "", "authToken", "");
        ctx.result(new Gson().toGson(result));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}*/