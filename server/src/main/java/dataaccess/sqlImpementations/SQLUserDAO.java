package dataaccess.sqlImpementations;

import dataaccess.interfaces.UserDAO;
import exceptions.AlreadyTakenException;
import exceptions.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {

        var createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                "username VARCHAR(255) NOT NULL PRIMARY KEY," +
                "password VARCHAR(255) NOT NULL," +
                "email VARCHAR(255) NOT NULL);";

        try {
            DatabaseManager.updateDatabase(createUserTable);
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createUser(UserData userInfo) throws AlreadyTakenException, DataAccessException {
        String updateString = "INSERT INTO users (username, password, email) VALUE (?, ?, ?);";
        String hashedPassword = hashPassword(userInfo.password());
        try {
            DatabaseManager.updateDatabase(updateString, userInfo.username(), hashedPassword, userInfo.email());
        } catch (SQLException | DataAccessException e) {
            throw new AlreadyTakenException();
        }
    }

    @Override
    public UserData getUser(String userName) throws DataAccessException {
        ArrayList<Map<String, String>> response;
        String retrivalString = "SELECT * FROM users WHERE username=?;";
        try {
            response = DatabaseManager.retrieveFromDatabase(retrivalString, userName);
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
        try {
            Map<String, String> userMap = response.getFirst();
            return new UserData(userMap.get("username"), userMap.get("password"), userMap.get("email"));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            DatabaseManager.updateDatabase("TRUNCATE TABLE users");
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
