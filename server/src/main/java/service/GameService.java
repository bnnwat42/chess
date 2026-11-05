package service;

import chess.ChessGame;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.memoryImplementations.MemoryAuthDAO;
import dataaccess.memoryImplementations.MemoryGameDAO;
import dataaccess.sqlImpementations.SQLAuthDAO;
import dataaccess.sqlImpementations.SQLGameDAO;
import exceptions.AlreadyTakenException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {

    private final GameDAO gameStorage;
    private final AuthDAO authStorage;

    public GameService() throws DataAccessException {
        //gameStorage = new MemoryGameDAO();
        gameStorage = new SQLGameDAO();
        //authStorage = new MemoryAuthDAO();
        authStorage = new SQLAuthDAO();
    }

    public Collection<GameInfo> listGames(String authToken) throws UnauthorizedException, DataAccessException {
        if (authStorage.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        Collection<GameInfo> games = new ArrayList<>();
        for (GameData game : gameStorage.listGames()) {
            games.add(new GameInfo(game));
        }
        return games;
    }

    public int createGame(CreateGameRequest newGameRequest) throws UnauthorizedException, DataAccessException {
        if (authStorage.getAuth(newGameRequest.authToken()) == null) {
            throw new UnauthorizedException();
        }
        GameData newGame = new GameData(0, null, null, newGameRequest.gameName(), new ChessGame());

        return gameStorage.createGame(newGame);
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws AlreadyTakenException, DataAccessException {
        if (authStorage.getAuth(joinGameRequest.authToken()) == null) {
            throw new UnauthorizedException();
        }
        GameData game = gameStorage.getGame(Integer.parseInt(joinGameRequest.gameID()));
        String whiteName = game.whiteUsername();
        String blackName = game.blackUsername();
        if (joinGameRequest.teamColor() == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException();
            } else {
                AuthData authData = authStorage.getAuth(joinGameRequest.authToken());
                whiteName = authData.username();
            }
        } else if (joinGameRequest.teamColor() == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException();
            } else {
                AuthData authData = authStorage.getAuth(joinGameRequest.authToken());
                blackName = authData.username();
            }
        }
        GameData newGame = new GameData(game.gameID(), whiteName, blackName, game.gameName(), game.game());
        gameStorage.updateGame(newGame);
    }

    public void clear() {
        gameStorage.clear();
    }
}
