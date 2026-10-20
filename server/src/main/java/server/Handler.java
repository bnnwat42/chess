package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.UserData;
import service.UserService;

import java.util.Map;

public class Handler {

    UserService userService;

    public Handler() {
//        userService = new UserService()
    }

    public String registerUser(Context ctx) {
        Map body = this.getBodyObject(ctx, Map.class);

        //TODO
        UserData newUserData = new UserData((String) body.get("username"), (String) body.get("password"), (String) body.get("email"));
        return null;
    }

    private static <T> T getBodyObject(Context context, Class<T> clazz) {
        var bodyObject = new Gson().fromJson(context.body(), clazz);

        if (bodyObject == null) {
            throw new RuntimeException("missing required body");
        }

        return bodyObject;
    }
}
