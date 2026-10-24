package Handlers;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import io.javalin.http.Context;
import model.GameData;
import service.CreateGameRequest;
import service.GameInfo;
import service.GameService;
import service.JoinGameRequest;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GameHandler {

    private final GameService gameService;

    public GameHandler() {
        gameService = new GameService();
    }

    public String listGames(Context ctx) {
        Collection<GameInfo> gameList = gameService.listGames(ctx.headerMap().get("Authorization"));

        String jsonGames = new Gson().toJson(Map.of("games", gameList));
        return jsonGames;
    }

    public int createGame(Context ctx) {
        String authToken = ctx.headerMap().get("Authorization");
        Map<String, String> body = getBodyObject(ctx, Map.class);
        if (isMalformed(body.keySet(), new String[]{"gameName"})) {
            throw new BadRequestException();
        }
        return gameService.createGame(new CreateGameRequest(authToken, body.get("gameName")));
    }

    private static <T> T getBodyObject(Context context, Class<T> clazz) {
        var bodyObject = new Gson().fromJson(context.body(), clazz);

        if (bodyObject == null) {
            throw new RuntimeException("missing required body");
        }

        return bodyObject;
    }

    public void joinGame(Context ctx) throws AlreadyTakenException {
        String authToken = ctx.headerMap().get("Authorization");
        Map<String, Object> body = getBodyObject(ctx, Map.class);

        if (isMalformed(body.keySet(), new String[]{"playerColor", "gameID"})) {
            throw new BadRequestException();
        }

        Object color = body.get("playerColor");
        ChessGame.TeamColor chessColor;
        chessColor = convertToColor(color);
        //Forgive me for this
        String id = String.valueOf(Integer.parseInt(body.get("gameID").toString().split("\\.")[0]));

        gameService.joinGame(new JoinGameRequest(authToken, chessColor, id));

    }

    private ChessGame.TeamColor convertToColor(Object color) {
        if (color == null) {
            throw new BadRequestException();
        }
        if (Objects.equals(color, "WHITE")) {
            return ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(color, "BLACK")) {
            return ChessGame.TeamColor.BLACK;
        } else {
            throw new BadRequestException();
        }
    }

    public void clear() {
        gameService.clear();
    }


    private boolean isMalformed(Set<String> jsonKeys, String[] keys) {
        return UserHandler.isMalformed(jsonKeys, keys);
    }
}
