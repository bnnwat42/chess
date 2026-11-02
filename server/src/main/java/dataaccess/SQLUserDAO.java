package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {
    @Override
    public void createUser(UserData userInfo) throws AlreadyTakenException {
        //Try and create table
        sqlRunner("CREATE TABLE users (username VARCHAR(255) NOT NULL , password VARCHAR(255) NOT NULL , email VARCHAR(255))");

        var hashedPassword = passwordHasher(userInfo.password());
        var userInputString = String.format("INSERT INTO users VALUES (%s, %s, %s)", userInfo.username(), hashedPassword, userInfo.email());
        sqlRunner(userInputString);
    }

    @Override
    public UserData getUser(String userName) {
        var getUserInput = String.format("SELECT * FROM users where username=\"%s\"", userName);
        sqlRunner(userInputString);
        return
    }

    @Override
    public void clear() {

    }

    private void sqlRunner(String sqlStatement) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sqlStatement)) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println(rs.getInt(1));
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    String passwordHasher(String clearTextPassword) {
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
        return hashedPassword;
    }
}
