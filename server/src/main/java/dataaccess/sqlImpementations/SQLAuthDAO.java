package dataaccess.sqlImpementations;

import dataaccess.interfaces.AuthDAO;
import exceptions.DataAccessException;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() {
        var createPetTable = "CREATE TABLE IF NOT EXISTS authTokens (" +
                "authToken VARCHAR(255) NOT NULL PRIMARY KEY," +
                "username VARCHAR(255) NOT NULL);";
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
        String updateString = "INSERT INTO authTokens (authToken, username) VALUE (?, ?);";
        try {
            DatabaseManager.updateDatabase(updateString, authData.authToken(), authData.username());
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authIdentifier) {
        String retrivalString = "SELECT * FROM authTokens where authToken = ?;";
        ArrayList<Map<String, String>> response;

        try {
            response = DatabaseManager.retrieveFromDatabase(retrivalString, authIdentifier);
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> userMap = response.getFirst();
        return new AuthData(userMap.get("authToken"), userMap.get("username"));
    }

    @Override
    public void deleteAuth(String authIdentifier) {
        String updateString = "DELETE FROM authTokens WHERE authToken = ?";
        try {
            DatabaseManager.updateDatabase(updateString, authIdentifier);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try {
            DatabaseManager.updateDatabase("TRUNCATE TABLE authTokens");
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
