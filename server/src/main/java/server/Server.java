package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.*;
import org.jetbrains.annotations.NotNull;
//import exception.ResponseException;


public class Server {

    private final Javalin javalinObj;
    private final Handler handler;

    public Server() {
        handler = new Handler();
        javalinObj = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalinObj.delete("/db", context -> clearDatabase(context));
        javalinObj.post("/user", context -> registerUser(context));
    }

    private void registerUser(Context context) {
        //Handler.registerUser(context.body());
    }

    private void clearDatabase(Context ctx) {//throws ResponseException {
        //TODO: Implement clear
        System.out.println(ctx.body().toString());
        ctx.status(500);
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