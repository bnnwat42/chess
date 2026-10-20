package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData gameData);

    Object getGame(int gameIdentifier);

    Collection<GameData> listGames();

    void updateGame(GameData gameData) throws DataAccessException;
}
