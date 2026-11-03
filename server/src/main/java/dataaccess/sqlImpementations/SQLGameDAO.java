package dataaccess.sqlImpementations;

import dataaccess.interfaces.GameDAO;
import exceptions.DataAccessException;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() {
        try (var conn = DatabaseManager.getConnection()) {
            conn.setCatalog("chess");

            var createPetTable = "CREATE TABLE IF NOT EXISTS games (" +
                    "gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY ," +
                    "whiteUsername VARCHAR(255)," +
                    "blackUsername VARCHAR(255)," +
                    "gameName VARCHAR(255)," +
                    "gameData TEXT NOT NULL);";

            try (var createTableStatement = conn.prepareStatement(createPetTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int createGame(GameData gameData) {
        String updateString = String.format("INSERT INTO games (gameID, gameData) VALUE (\"%s\", \"%s\");",
                gameData.gameID(), gameData.game().toString()
        );
        System.out.println(gameData.game().toString());
        try {
            DatabaseManager.updateDatabase(updateString);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return gameData.gameID();
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
        try {
            DatabaseManager.updateDatabase("TRUNCATE TABLE games");
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
