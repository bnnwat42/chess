package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {

    private final GameDAO gameStorage;
    private final AuthDAO authStorage;
    private int gameId = 1;

    public GameService() {
        gameStorage = new MemoryGameDAO();
        authStorage = new MemoryAuthDAO();
    }

    public Collection<GameInfo> listGames(String authToken) throws UnauthorizedException {
        if (authStorage.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        Collection<GameInfo> games = new ArrayList<>();
        for (GameData game : gameStorage.listGames()) {
            games.add(new GameInfo(game));
        }
        return games;
    }

    public int createGame(CreateGameRequest newGameRequest) throws UnauthorizedException {
        if (authStorage.getAuth(newGameRequest.authToken()) == null) {
            throw new UnauthorizedException();
        }
        int newId = gameId++;
        GameData newGame = new GameData(newId, "", "", newGameRequest.gameName(), new ChessGame());

        gameStorage.createGame(newGame);
        return newGame.gameID();
    }

    public void joinGame(JoinGameRequest joinGameRequest) {
        if (authStorage.getAuth(joinGameRequest.authToken()) == null) {
            throw new UnauthorizedException();
        }
        GameData game = gameStorage.getGame(Integer.parseInt(joinGameRequest.gameID()));
        if (joinGameRequest.teamColor() == ChessGame.TeamColor.WHITE) {
            if (!game.whiteUsername().isEmpty()) {
                //throw new Exception();
            }
        }
    }
}
