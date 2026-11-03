package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(GameData gameData);

    GameData getGame(int gameIdentifier);

    Collection<GameData> listGames();

    void updateGame(GameData gameData);

    void clear();
}
