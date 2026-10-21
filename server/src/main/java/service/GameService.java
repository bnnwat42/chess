package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;

import java.util.Collection;

public class GameService {

    private final GameDAO gameStorage;
    private final AuthDAO authStorage;

    public GameService() {
        gameStorage = new MemoryGameDAO();
        authStorage = new MemoryAuthDAO();
    }

    public Collection<GameData> listGames(String authToken) throws UnauthorizedException {
        if (authStorage.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        return gameStorage.listGames();
    }

    public int createGame(CreateGameRequest newGameRequest) throws UnauthorizedException {
        if (authStorage.getAuth(newGameRequest.authToken()) == null) {
            throw new UnauthorizedException();
        }
        //TODO:Generate unique Id
        int newId = 1;
        GameData newGame = new GameData(newId, null, null, newGameRequest.gameName(), new ChessGame());

        gameStorage.createGame(newGame);
        return newGame.gameID();
    }

    public void joinGame(JoinGameRequest joinGameRequest) {
        if (authStorage.getAuth(joinGameRequest.authToken()) == null) {
            throw new UnauthorizedException();
        }
    }
}
