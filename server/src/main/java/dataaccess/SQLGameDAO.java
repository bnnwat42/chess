package dataaccess;

import exceptions.DataAccessException;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() {
        try {
            DatabaseManager.updateDatabase("CREATE TABLE IF NOT EXISTS games (" +
                    "gameID INT NOT NULL," +
                    "gameData TEXT NOT NULL,");
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createGame(GameData gameData) {
        String updateString = String.format("INSERT INTO games (gameID, gameData) VALUE (\"%s\", \"%s\");",
                gameData.gameID(), gameData.game().toString()
        );
        System.out.println(gameData.game().toString());
        try {
            DatabaseManager.updateDatabase(updateString);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData getGame(int gameIdentifier) {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(GameData gameData) {

    }

    @Override
    public void clear() {

    }
}
