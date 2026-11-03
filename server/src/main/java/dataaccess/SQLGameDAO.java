package dataaccess;

import exceptions.DataAccessException;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

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
            try (var conn = DatabaseManager.getConnection()) {
                try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                    for (int i = 0; i < params.length; i++) {
                        Object param = params[i];
                        if (param instanceof String p) ps.setString(i + 1, p);
                        else if (param instanceof Integer p) ps.setInt(i + 1, p);
                        else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
                        else if (param == null) ps.setNull(i + 1, NULL);
                    }
                    ps.executeUpdate();

                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getInt(1);
                    }

                    return 0;
                }
            } catch (SQLException e) {
                throw new ResponseException(ResponseException.Code.ServerError, String.format("unable to update database: %s, %s", statement, e.getMessage()));
            }
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
        try {
            DatabaseManager.updateDatabase("TRUNCATE TABLE games");
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
