package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {

    SQLGameDAO DAO;

    @BeforeEach
    void setup() {
        DAO = new SQLGameDAO();
    }

    @Test
    void createGame() {
    }

    @Test
    void getGame() {
    }

    @Test
    void listGames() {
    }

    @Test
    void updateGame() {
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