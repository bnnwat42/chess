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

public class GameHandler {

    private final GameService gameService;

    public GameHandler() {
        gameService = new GameService();
    }

    public String listGames(Context ctx) {
        Collection<GameInfo> gameList = gameService.listGames(ctx.headerMap().get("Authorization"));

        //TODO: Convert list to JSON
        String jsonGames = new Gson().toJson(Map.of("games", gameList));

        System.out.println(jsonGames);
        return jsonGames;
    }

    public int createGame(Context ctx) {
        String authToken = ctx.headerMap().get("Authorization");
        Map<String, String> body = getBodyObject(ctx, Map.class);
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
        String color = body.get("playerColor").toString();
        ChessGame.TeamColor chessColor;
        if (Objects.equals(color, "WHITE")) {
            chessColor = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(color, "BLACK")) {
            chessColor = ChessGame.TeamColor.BLACK;
        } else {
            throw new BadRequestException();
        }
        //Forgive me for this
        String id = String.valueOf(Integer.parseInt(body.get("gameID").toString().split("\\.")[0]));

        gameService.joinGame(new JoinGameRequest(authToken, chessColor, id));

    }

    public void clear() {
        gameService.clear();
    }
}
