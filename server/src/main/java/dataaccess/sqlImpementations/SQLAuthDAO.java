package dataaccess.sqlImpementations;

import dataaccess.interfaces.AuthDAO;
import exceptions.DataAccessException;
import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() {
        var createPetTable = "CREATE TABLE IF NOT EXISTS auth (" +
                "authToken VARCHAR(255) NOT NULL PRIMARY KEY," +
                "userName VARCHAR(255) NOT NULL);";
        try {
            DatabaseManager.updateDatabase(createPetTable);
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void createAuth(AuthData authData) {
        String updateString = "INSERT INTO users (authToken, userName) VALUE (?, ?);";
        try {
            DatabaseManager.updateDatabase(updateString, authData.authToken(), authData.username());
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authIdentifier) {
        return null;
    }

    @Override
    public void deleteAuth(String authIdentifier) {

    }

    @Override
    public void clear() {

    }
}
