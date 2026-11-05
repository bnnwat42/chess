package dataaccess.sql;

import exceptions.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {

    SQLUserDAO dao;
    static String name;

    @BeforeAll
    static void createDatabase() {
        try {
            name = DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void clearTable() {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.updateDatabase("DROP TABLE IF EXISTS users");
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        dao = assertDoesNotThrow(() -> new SQLUserDAO());
    }

    @Test
    void noDatabase() {
        assertDoesNotThrow(() -> DatabaseManager.updateDatabase("DROP TABLE users;"));
        assertThrows(DataAccessException.class, () -> dao.createUser(new UserData("1", "2", "3")));
        assertThrows(DataAccessException.class, () -> dao.clear());
        assertThrows(DataAccessException.class, () -> dao.getUser("Bob"));
    }

    @Test
    void createUser() {
        UserData newUser = new UserData("Bob", "1234", "Bob@byu.edu");
        assertDoesNotThrow(() -> dao.createUser(newUser));
    }

    @Test
    void getUser() {
        UserData newUser = new UserData("Bob", "1234", "Bob@byu.edu");
        assertDoesNotThrow(() -> dao.createUser(newUser));
        var retrievedUse = assertDoesNotThrow(() -> dao.getUser("Bob"));
        assertEquals(newUser.email(), retrievedUse.email());
        assertEquals(newUser.username(), retrievedUse.username());
        assertTrue(BCrypt.checkpw(newUser.password(), retrievedUse.password()));
    }

    @Test
    void clear() {
        UserData newUser = new UserData("Bob", "1234", "Bob@byu.edu");
        assertDoesNotThrow(() -> dao.createUser(newUser));
        assertDoesNotThrow(() -> dao.clear());
        assertNull(assertDoesNotThrow(() -> dao.getUser("Bob")));
    }
}