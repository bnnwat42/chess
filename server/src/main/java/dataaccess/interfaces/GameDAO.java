package dataaccess.interfaces;

import exceptions.DataAccessException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameIdentifier) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    void clear();
}
