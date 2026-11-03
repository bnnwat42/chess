package dataaccess.sqlImpementations;

import dataaccess.interfaces.UserDAO;
import exceptions.AlreadyTakenException;
import exceptions.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() {

        var createPetTable = "CREATE TABLE IF NOT EXISTS users (" +
                "username VARCHAR(255) NOT NULL PRIMARY KEY," +
                "password VARCHAR(255) NOT NULL," +
                "email VARCHAR(255) NOT NULL);";

        try {
            DatabaseManager.updateDatabase(createPetTable);
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void createUser(UserData userInfo) throws AlreadyTakenException {
        String updateString = "INSERT INTO users (username, password, email) VALUE (?, ?, ?);";
        String hashedPassword = hashPassword(userInfo.password());
        try {
            DatabaseManager.updateDatabase(updateString, userInfo.username(), hashedPassword, userInfo.email());
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String userName) {
        ArrayList<Map<String, String>> response;
        String retrivalString = String.format("SELECT * FROM users WHERE username=\"%s\";", userName);
        try {
            response = DatabaseManager.retrieveFromDatabase(retrivalString);
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> userMap = response.getFirst();
        return new UserData(userMap.get("username"), userMap.get("password"), userMap.get("email"));
    }

    @Override
    public void clear() {
        try {
            DatabaseManager.updateDatabase("TRUNCATE TABLE users");
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }


}
