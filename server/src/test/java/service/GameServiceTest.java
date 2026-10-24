package service;

import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    GameService gameService;
    UserService userService;
    UserData testUser;
    GameData testGame;
    AuthData newAuth;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        gameService = new GameService();
        userService.clear();
        gameService.clear();
        testUser = new UserData("bob", "1234", "cosmo@byu.edu");
        testGame = new GameData(1, "", "", "GameName", new ChessGame());
        newAuth = assertDoesNotThrow(() -> userService.register(testUser));
    }

    @Test
    void createGame() {
        CreateGameRequest newGameRequest = new CreateGameRequest(newAuth.authToken(), testGame.gameName());
        int gameID = assertDoesNotThrow(() -> gameService.createGame(newGameRequest));
        CreateGameRequest fakeGameRequest = new CreateGameRequest("Fake-Token-1234", testGame.gameName());
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(fakeGameRequest));
    }

    @Test
    void listGames() {
        Collection<String> nameList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CreateGameRequest newGameRequest = new CreateGameRequest(newAuth.authToken(), testGame.gameName() + Integer.valueOf(i).toString());
            gameService.createGame(newGameRequest);
            nameList.add(newGameRequest.gameName());
        }

        Collection<GameInfo> gameInfoList = gameService.listGames(newAuth.authToken());
        for (GameInfo game : gameInfoList) {
            assertTrue(nameList.contains(game.gameName()));
        }
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("Fake-Token-1234"));
    }

    @Test
    void joinGame() {
        CreateGameRequest newGameRequest = new CreateGameRequest(newAuth.authToken(), testGame.gameName());
        int gameID = gameService.createGame(newGameRequest);

        JoinGameRequest newRequest = new JoinGameRequest(newAuth.authToken(), ChessGame.TeamColor.WHITE, Integer.valueOf(gameID).toString());
        assertDoesNotThrow(() -> gameService.joinGame(newRequest));
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(newRequest));
    }
}