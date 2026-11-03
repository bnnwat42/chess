package dataaccess;

import exceptions.AlreadyTakenException;
import exceptions.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() {

        try (var conn = DatabaseManager.getConnection()) {
            conn.setCatalog("chess");

            var createPetTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(255) NOT NULL PRIMARY KEY," +
                    "password VARCHAR(255) NOT NULL," +
                    "email VARCHAR(255) NOT NULL);";

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
    public void createUser(UserData userInfo) throws AlreadyTakenException {
        String updateString = String.format("INSERT INTO users (username, password, email) VALUE (\"%s\", \"%s\", \"%s\");",
                userInfo.username(), userInfo.password(), userInfo.email()
        );
        try {
            DatabaseManager.updateDatabase(updateString);
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


}
