package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class MemoryGameDAOTest {

    MemoryGameDAO memoryStorage;

    @BeforeEach
    void setUp() {
        memoryStorage = new MemoryGameDAO();
        memoryStorage.clear();
    }

    @Test
    void createGameAndGetGame() {
        GameData newGame = new GameData(123, "White", "Black", "The Battle of the Centuries", new ChessGame());

        memoryStorage.createGame(newGame);

        GameData returnedGame = memoryStorage.getGame(123);

        assertEquals(123, returnedGame.gameID());
        assertEquals("White", returnedGame.whiteUsername());
        assertEquals("Black", returnedGame.blackUsername());
        assertEquals("The Battle of the Centuries", returnedGame.gameName());
        assertEquals(new ChessGame(), returnedGame.game());
    }

    @Test
    void listGames() {
        HashSet<GameData> chessGames = new HashSet<>();
        int iD = 100;

        for (int i = 0; i < 10; i++) {
            GameData newGame = new GameData(iD++, Integer.toString(iD++), Integer.toString(iD++), Integer.toString(iD++), new ChessGame());
            chessGames.add(newGame);
            memoryStorage.createGame(newGame);
        }

        HashSet<GameData> returnedGames = new HashSet<>();
        returnedGames.addAll(memoryStorage.listGames());

        assertEquals(chessGames, returnedGames);
    }

    @Test
    void updateGame() {
        GameData newGame = new GameData(123, "White", "Black", "The Battle of the Centuries", new ChessGame());

        memoryStorage.createGame(newGame);

        GameData updatedGame = new GameData(123, "wHITE", "bLACK", "IDK", new ChessGame());  // ← New variable

        assertNotEquals(updatedGame, memoryStorage.getGame(123));

        memoryStorage.updateGame(updatedGame);

        assertNotEquals(newGame, memoryStorage.getGame(123));
        assertEquals(updatedGame, memoryStorage.getGame(123));
    }
}