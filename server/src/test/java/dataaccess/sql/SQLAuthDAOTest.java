package dataaccess.sql;

import exceptions.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {

    SQLAuthDAO DAO;

    @BeforeEach
    void clearTable() {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.updateDatabase("DROP TABLE IF EXISTS authTokens");
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        DAO = assertDoesNotThrow(SQLAuthDAO::new);
    }

    @Test
    void constructor() {
        assertDoesNotThrow(() -> DAO.clear());
        DAO = assertDoesNotThrow(SQLAuthDAO::new);
        assertDoesNotThrow(() -> DAO.clear());
    }

    void dropDatabase() {
        assertDoesNotThrow(() -> DatabaseManager.updateDatabase("DROP DATABASE CHESS;"));
    }

    @Test
    void constructorNoDatabase() {
        dropDatabase();
        assertThrows(DataAccessException.class, SQLAuthDAO::new);
    }

    @Test
    void createAuth() {
        AuthData newAuth = new AuthData("abc123", "Bob");
        assertDoesNotThrow(() -> DAO.createAuth(newAuth));
    }

    @Test
    void authNoDatabase() {
        dropDatabase();
        AuthData newAuth = new AuthData("abc123", "Bob");
        assertThrows(DataAccessException.class, () -> DAO.createAuth(newAuth));
    }

    @Test
    void getAuth() {
        AuthData newAuth = new AuthData("abc123", "Bob");
        assertDoesNotThrow(() -> DAO.createAuth(newAuth));
        var retrievedAuth = assertDoesNotThrow(() -> DAO.getAuth(newAuth.authToken()));
        assertEquals(newAuth, retrievedAuth);
    }

    @Test
    void getNoDatabase() {
        dropDatabase();
        assertThrows(DataAccessException.class, () -> DAO.getAuth("abc"));
    }

    @Test
    void deleteAuth() {
        AuthData newAuth = new AuthData("abc123", "Bob");
        assertDoesNotThrow(() -> DAO.createAuth(newAuth));
        assertDoesNotThrow(() -> DAO.deleteAuth(newAuth.authToken()));
        assertThrows(RuntimeException.class, () -> DAO.getAuth(newAuth.authToken()));
    }

    @Test
    void deleteNoDatabase() {
        dropDatabase();
        assertThrows(DataAccessException.class, () -> DAO.deleteAuth("abc"));
    }

    @Test
    void clear() {
        for (int i = 0; i < 5; i++) {
            AuthData newAuth = new AuthData(String.format("abc123%d", i), String.format("Bob%d", i));
            assertDoesNotThrow(() -> DAO.createAuth(newAuth));
            AuthData retrievedAuth = assertDoesNotThrow(() -> DAO.getAuth(newAuth.authToken()));
            assertEquals(newAuth, retrievedAuth);
        }
        assertDoesNotThrow(() -> DAO.clear());
        for (int i = 0; i < 5; i++) {
            AuthData newAuth = new AuthData(String.format("abc123%d", i), String.format("Bob%d", i));
            assertThrows(RuntimeException.class, () -> DAO.getAuth(newAuth.authToken()));
        }
    }

    @Test
    void clearNoDatabase() {
        dropDatabase();
        assertThrows(DataAccessException.class, () -> DAO.clear());
    }
}