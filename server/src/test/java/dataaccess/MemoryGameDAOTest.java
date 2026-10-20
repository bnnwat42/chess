package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class MemoryGameDAOTest {

    @Test
    void createGameAndGetGame() {
        MemoryGameDAO memoryStorage = new MemoryGameDAO();
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
        MemoryGameDAO memoryStorage = new MemoryGameDAO();
        int dummyId = 100;

        for (int i = 0; i < 10; i++) {
            GameData newGame = new GameData(dummyId++, Integer.toString(dummyId++), Integer.toString(dummyId++), Integer.toString(dummyId++), new ChessGame());
            chessGames.add(newGame);
            memoryStorage.createGame(newGame);
        }

        HashSet<GameData> returnedGames = new HashSet<>();
        returnedGames.addAll(memoryStorage.listGames());

        assertEquals(chessGames, returnedGames);
    }

    @Test
    void updateGame() {
        MemoryGameDAO memoryStorage = new MemoryGameDAO();
        GameData newGame = new GameData(123, "White", "Black", "The Battle of the Centuries", new ChessGame());

        memoryStorage.createGame(newGame);

        GameData updatedGame = new GameData(123, "wHITE", "bLACK", "IDK", new ChessGame());  // ← New variable

        assertNotEquals(updatedGame, memoryStorage.getGame(123));

        assertDoesNotThrow(() -> memoryStorage.updateGame(updatedGame));

        assertNotEquals(newGame, memoryStorage.getGame(123));
        assertEquals(updatedGame, memoryStorage.getGame(123));
    }
}