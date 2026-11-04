package dataaccess.sqlImpementations;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import exceptions.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {

    SQLGameDAO DAO;

    @BeforeEach
    void setup() {
        try {
            DatabaseManager.updateDatabase("DROP TABLE IF EXISTS games");
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        DAO = assertDoesNotThrow(() -> new SQLGameDAO());
    }

    @BeforeAll
    static void createDatabase() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createandGetGame() {
        GameData newGame = new GameData(0, null, null, "myName", new ChessGame());
        int realID = assertDoesNotThrow(() -> DAO.createGame(newGame));
        GameData realGame = new GameData(realID, null, null, "myName", new ChessGame());
        GameData retrievedGame = assertDoesNotThrow(() -> DAO.getGame(realID));
        assertEquals(realGame, retrievedGame);
    }

    @Test
    void listGames() {
        int[] ids = new int[10];
        for (int i = 0; i < ids.length; i++) {
            GameData newGame = new GameData(0, null, null, "myName", new ChessGame());
            int realID = assertDoesNotThrow(() -> DAO.createGame(newGame));
            ids[i] = realID;
        }
        Collection<GameData> games = assertDoesNotThrow(() -> DAO.listGames());
        for (int id : ids) {
            GameData realGame = new GameData(id, null, null, "myName", new ChessGame());
            assertTrue(games.contains(realGame));
        }
    }

    @Test
    void updateGame() {
        GameData newGame = new GameData(0, null, null, "myName", new ChessGame());
        int realID = assertDoesNotThrow(() -> DAO.createGame(newGame));
        GameData realGame = new GameData(realID, null, null, "myName", new ChessGame());
        ChessMove fakeMove = new ChessMove(new ChessPosition(2, 2), new ChessPosition(3, 2), null);
        ChessGame fakeGame = new ChessGame();
        assertDoesNotThrow(() -> fakeGame.makeMove(fakeMove));
        GameData updatedGame = new GameData(realID, "whiteGuy", "blackGuy", "gameName", fakeGame);
        assertDoesNotThrow(() -> DAO.updateGame(updatedGame));
        GameData retrievedGame = assertDoesNotThrow(() -> DAO.getGame(realID));
        assertEquals(updatedGame, retrievedGame);
        assertNotEquals(realGame, retrievedGame);
    }

    @Test
    void clear() {
        GameData newGame = new GameData(1, "white", "black", "gameName", new ChessGame());
        int gameId;
        assertDoesNotThrow(() -> DAO.createGame(newGame));
        DAO.clear();
        assertThrows(NoSuchElementException.class, () -> DAO.getGame(1));
    }
}