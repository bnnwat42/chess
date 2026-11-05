package dataaccess.sql;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import exceptions.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {

    SQLGameDAO dao;

    @BeforeEach
    void setup() {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.updateDatabase("DROP TABLE IF EXISTS games");
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        dao = assertDoesNotThrow(() -> new SQLGameDAO());
    }

    @Test
    void createandGetGame() {
        GameData newGame = new GameData(0, null, null, "myName", new ChessGame());
        int realID = assertDoesNotThrow(() -> dao.createGame(newGame));
        GameData realGame = new GameData(realID, null, null, "myName", new ChessGame());
        GameData retrievedGame = assertDoesNotThrow(() -> dao.getGame(realID));
        assertEquals(realGame, retrievedGame);
    }

    @Test
    void noDatabase() {
        assertDoesNotThrow(() -> DatabaseManager.updateDatabase("DROP TABLE games;"));
        assertThrows(DataAccessException.class, () -> dao.createGame(new GameData(1, "2", "3", "4", new ChessGame())));
        assertThrows(DataAccessException.class, () -> dao.clear());
        assertThrows(DataAccessException.class, () -> dao.getGame(1));
        assertThrows(DataAccessException.class, () -> dao.listGames());
        assertThrows(DataAccessException.class, () -> dao.updateGame(new GameData(1, "2", "3", "4", new ChessGame())));
    }

    @Test
    void listGames() {
        int[] ids = new int[10];
        for (int i = 0; i < ids.length; i++) {
            GameData newGame = new GameData(0, null, null, "myName", new ChessGame());
            int realID = assertDoesNotThrow(() -> dao.createGame(newGame));
            ids[i] = realID;
        }
        Collection<GameData> games = assertDoesNotThrow(() -> dao.listGames());
        for (int id : ids) {
            GameData realGame = new GameData(id, null, null, "myName", new ChessGame());
            assertTrue(games.contains(realGame));
        }
    }

    @Test
    void updateGame() {
        GameData newGame = new GameData(0, null, null, "myName", new ChessGame());
        int realID = assertDoesNotThrow(() -> dao.createGame(newGame));
        GameData realGame = new GameData(realID, null, null, "myName", new ChessGame());
        ChessMove fakeMove = new ChessMove(new ChessPosition(2, 2), new ChessPosition(3, 2), null);
        ChessGame fakeGame = new ChessGame();
        assertDoesNotThrow(() -> fakeGame.makeMove(fakeMove));
        GameData updatedGame = new GameData(realID, "whiteGuy", "blackGuy", "gameName", fakeGame);
        assertDoesNotThrow(() -> dao.updateGame(updatedGame));
        GameData retrievedGame = assertDoesNotThrow(() -> dao.getGame(realID));
        assertEquals(updatedGame, retrievedGame);
        assertNotEquals(realGame, retrievedGame);
    }

    @Test
    void clear() {
        GameData newGame = new GameData(1, "white", "black", "gameName", new ChessGame());
        int gameId;
        assertDoesNotThrow(() -> dao.createGame(newGame));
        assertDoesNotThrow(() -> dao.clear());
        assertThrows(NoSuchElementException.class, () -> dao.getGame(1));
    }
}