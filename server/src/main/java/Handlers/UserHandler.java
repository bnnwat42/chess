package Handlers;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.UserService;

import java.util.Map;
import java.util.Set;

public class UserHandler {

    private final UserService userService;

    public UserHandler() {
        userService = new UserService();
    }

    public String registerUser(Context ctx) throws AlreadyTakenException, BadRequestException {
        Map<String, String> body = getBodyObject(ctx, Map.class);

        Set<String> keys = body.keySet();
        if (isMalformed(keys, new String[]{"username", "password", "email"})) {
            throw new BadRequestException();
        }

        UserData newUser = new UserData(body.get("username"), body.get("password"), body.get("email"));

        AuthData newAuth = userService.register(newUser);
        return new Gson().toJson(Map.of("username", newAuth.username(), "authToken", newAuth.authToken()));
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

    public String loginUser(Context ctx) throws DataAccessException {
        Map<String, String> body = getBodyObject(ctx, Map.class);

        Set<String> keys = body.keySet();
        if (isMalformed(keys, new String[]{"username", "password"})) {
            throw new BadRequestException();
        }

        UserData loginUser = new UserData(body.get("username"), body.get("password"), null);
        AuthData newAuth = userService.login(loginUser);
        return new Gson().toJson(Map.of("username", newAuth.username(), "authToken", newAuth.authToken()));
    }

    public void logoutUser(Context ctx) throws DataAccessException {
        userService.logout(ctx.headerMap().get("Authorization"));
    }

    private boolean isMalformed(Set<String> jsonKeys, String[] keys) {
        if (jsonKeys.size() != keys.length) {
            return true;
        }
        for (String key : keys) {
            if (!jsonKeys.contains(key)) {
                return true;
            }
        }
        return false;
    }
}
