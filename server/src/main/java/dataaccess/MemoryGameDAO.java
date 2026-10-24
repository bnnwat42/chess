package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    static final private HashMap<Integer, GameData> GAME_STORAGE = new HashMap<>();

    @Override
    public void createGame(GameData gameData) {
        GAME_STORAGE.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameId) {
        return GAME_STORAGE.get(gameId);
    }

    @Override
    public Collection<GameData> listGames() {
        return GAME_STORAGE.values();
    }

    @Override
    public void updateGame(GameData gameData) {
        GAME_STORAGE.put(gameData.gameID(), gameData);
    }

    public void clear() {
        GAME_STORAGE.clear();
    }
}
