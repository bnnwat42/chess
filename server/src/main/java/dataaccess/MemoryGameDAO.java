package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    static final private HashMap<Integer, GameData> gameStorage = new HashMap<>();

    @Override
    public void createGame(GameData gameData) {
        gameStorage.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameId) {
        return gameStorage.get(gameId);
    }

    @Override
    public Collection<GameData> listGames() {
        return gameStorage.values();
    }

    @Override
    public void updateGame(GameData gameData) {
        gameStorage.put(gameData.gameID(), gameData);
    }

    public static void clear() {
        gameStorage.clear();
    }
}
