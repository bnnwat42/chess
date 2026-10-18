package dataaccess;

import java.util.Collection;

public interface GameDAO {
    void createGame(Object gameData);

    Object getGame(Object gameIdentifier);

    Collection<Object> listGames();

    void updateGame(Object gameData);
}
