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
        GameData newGame = new GameData(0, "White", "Black", "The Battle of the Centuries", new ChessGame());

        int realId = memoryStorage.createGame(newGame);
        GameData realGame = new GameData(realId, "White", "Black", "The Battle of the Centuries", new ChessGame());

        GameData returnedGame = memoryStorage.getGame(realId);

        assertEquals(realId, returnedGame.gameID());
        assertEquals("White", returnedGame.whiteUsername());
        assertEquals("Black", returnedGame.blackUsername());
        assertEquals("The Battle of the Centuries", returnedGame.gameName());
        assertEquals(new ChessGame(), returnedGame.game());
    }

    @Test
    void listGames() {
        HashSet<GameData> chessGames = new HashSet<>();
        int dummyId = 100;

        int[] ids = new int[10];
        for (int i = 0; i < 10; i++) {
            GameData newGame = new GameData(dummyId++, Integer.toString(dummyId++), Integer.toString(dummyId++), Integer.toString(dummyId++), new ChessGame());
            ids[i] = memoryStorage.createGame(newGame);
            chessGames.add(new GameData(newGame, ids[i]));
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