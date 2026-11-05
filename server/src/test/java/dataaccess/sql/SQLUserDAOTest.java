package dataaccess.sql;

import chess.ChessGame;
import exceptions.DataAccessException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {

    SQLUserDAO DAO;

    @BeforeAll
    static void createDatabase() {
        try {
            DatabaseManager.createDatabase();
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
        DAO = assertDoesNotThrow(() -> new SQLUserDAO());
    }

    @Test
    void noDatabase() {
        assertDoesNotThrow(() -> DatabaseManager.updateDatabase("DROP DATABASE CHESS;"));
        assertThrows(DataAccessException.class, () -> DAO.createUser(new UserData("1", "2", "3")));
        assertThrows(DataAccessException.class, () -> DAO.clear());
        assertThrows(DataAccessException.class, () -> DAO.getUser("Bob"));
    }

    @Test
    void createUser() {
        UserData newUser = new UserData("Bob", "1234", "Bob@byu.edu");
        assertDoesNotThrow(() -> DAO.createUser(newUser));
    }

    @Test
    void getUser() {
        UserData newUser = new UserData("Bob", "1234", "Bob@byu.edu");
        assertDoesNotThrow(() -> DAO.createUser(newUser));
        var retrievedUse = assertDoesNotThrow(() -> DAO.getUser("Bob"));
        assertEquals(newUser.email(), retrievedUse.email());
        assertEquals(newUser.username(), retrievedUse.username());
        assertTrue(BCrypt.checkpw(newUser.password(), retrievedUse.password()));
    }

    @Test
    void clear() {
        UserData newUser = new UserData("Bob", "1234", "Bob@byu.edu");
        assertDoesNotThrow(() -> DAO.createUser(newUser));
        assertDoesNotThrow(() -> DAO.clear());
        assertNull(assertDoesNotThrow(() -> DAO.getUser("Bob")));
    }
}