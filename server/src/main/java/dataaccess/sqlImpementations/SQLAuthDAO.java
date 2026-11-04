package dataaccess.sqlImpementations;

import dataaccess.interfaces.AuthDAO;
import exceptions.DataAccessException;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        var createAuthTable = "CREATE TABLE IF NOT EXISTS authTokens (" +
                "authToken VARCHAR(255) NOT NULL PRIMARY KEY," +
                "username VARCHAR(255) NOT NULL);";
        try {
            DatabaseManager.updateDatabase(createAuthTable);
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        String updateString = "INSERT INTO authTokens (authToken, username) VALUE (?, ?);";
        try {
            DatabaseManager.updateDatabase(updateString, authData.authToken(), authData.username());
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authIdentifier) throws DataAccessException {
        String retrivalString = "SELECT * FROM authTokens where authToken = ?;";
        ArrayList<Map<String, String>> response;

        try {
            response = DatabaseManager.retrieveFromDatabase(retrivalString, authIdentifier);
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
        Map<String, String> userMap = response.getFirst();
        return new AuthData(userMap.get("authToken"), userMap.get("username"));
    }

    @Override
    public void deleteAuth(String authIdentifier) throws DataAccessException {
        String updateString = "DELETE FROM authTokens WHERE authToken = ?";
        try {
            DatabaseManager.updateDatabase(updateString, authIdentifier);
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            DatabaseManager.updateDatabase("TRUNCATE TABLE authTokens");
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
