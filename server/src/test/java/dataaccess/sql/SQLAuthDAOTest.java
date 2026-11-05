package dataaccess.sql;

import exceptions.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {

    SQLAuthDAO dao;

    @BeforeEach
    void clearTable() {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.updateDatabase("DROP TABLE IF EXISTS authTokens");
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        dao = assertDoesNotThrow(SQLAuthDAO::new);
    }

    @Test
    void constructor() {
        assertDoesNotThrow(() -> dao.clear());
        dao = assertDoesNotThrow(SQLAuthDAO::new);
        assertDoesNotThrow(() -> dao.clear());
    }

    @Test
    void noDatabase() {
        assertDoesNotThrow(() -> DatabaseManager.updateDatabase("DROP TABLE authTokens;"));
        AuthData newAuth = new AuthData("abc123", "Bob");
        assertThrows(DataAccessException.class, () -> dao.createAuth(newAuth));
        assertThrows(DataAccessException.class, () -> dao.getAuth("abc"));
        assertThrows(DataAccessException.class, () -> dao.deleteAuth("abc"));
        assertThrows(DataAccessException.class, () -> dao.clear());
    }

    @Test
    void createAuth() {
        AuthData newAuth = new AuthData("abc123", "Bob");
        assertDoesNotThrow(() -> dao.createAuth(newAuth));
    }

    @Test
    void getAuth() {
        AuthData newAuth = new AuthData("abc123", "Bob");
        assertDoesNotThrow(() -> dao.createAuth(newAuth));
        var retrievedAuth = assertDoesNotThrow(() -> dao.getAuth(newAuth.authToken()));
        assertEquals(newAuth, retrievedAuth);
    }

    @Test
    void deleteAuth() {
        AuthData newAuth = new AuthData("abc123", "Bob");
        assertDoesNotThrow(() -> dao.createAuth(newAuth));
        assertDoesNotThrow(() -> dao.deleteAuth(newAuth.authToken()));
        assertThrows(RuntimeException.class, () -> dao.getAuth(newAuth.authToken()));
    }

    @Test
    void clear() {
        for (int i = 0; i < 5; i++) {
            AuthData newAuth = new AuthData(String.format("abc123%d", i), String.format("Bob%d", i));
            assertDoesNotThrow(() -> dao.createAuth(newAuth));
            AuthData retrievedAuth = assertDoesNotThrow(() -> dao.getAuth(newAuth.authToken()));
            assertEquals(newAuth, retrievedAuth);
        }
        assertDoesNotThrow(() -> dao.clear());
        for (int i = 0; i < 5; i++) {
            AuthData newAuth = new AuthData(String.format("abc123%d", i), String.format("Bob%d", i));
            assertThrows(RuntimeException.class, () -> dao.getAuth(newAuth.authToken()));
        }
    }
}