package Handlers;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.UserService;

import java.nio.charset.MalformedInputException;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Set;

public class UserHandler {

    private final UserService userService;

    public UserHandler() {
        userService = new UserService();
    }

    public AuthData registerUser(Context ctx) throws AlreadyTakenException, InputMismatchException {
        Map<String, String> body = getBodyObject(ctx, Map.class);

        Set<String> keys = body.keySet();
        if (!keys.contains("username") ||
                !keys.contains("password") ||
                !keys.contains("email") ||
                keys.size() != 3) {
            throw new InputMismatchException();
        }

        UserData newUser = new UserData(body.get("username"), body.get("password"), body.get("email"));

        return userService.register(newUser);
    }

    public void clear() throws Exception {
        userService.clear();
    }

    private static <T> T getBodyObject(Context context, Class<T> clazz) {
        var bodyObject = new Gson().fromJson(context.body(), clazz);

        if (bodyObject == null) {
            throw new RuntimeException("missing required body");
        }

        return bodyObject;
    }

    public AuthData loginUser(Context ctx) throws DataAccessException {
        Map<String, String> body = getBodyObject(ctx, Map.class);

        Set<String> keys = body.keySet();
        if (!keys.contains("username") ||
                !keys.contains("password") ||
                keys.size() != 2) {
            throw new InputMismatchException();
        }

        UserData loginUser = new UserData(body.get("username"), body.get("password"), null);
        return userService.login(loginUser);
    }
}
