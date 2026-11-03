package dataaccess.sqlImpementations;

import dataaccess.interfaces.AuthDAO;
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
            DatabaseManager.updateDatabase("DROP TABLE IF EXISTS authTokens");
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        DAO = new SQLAuthDAO();
    }

    @BeforeAll
    static void createDatabase() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createAuth() {
        AuthData newAuth = new AuthData("abc123", "Bob");
        assertDoesNotThrow(() -> DAO.createAuth(newAuth));
    }

    @Test
    void getAuth() {
        AuthData newAuth = new AuthData("abc123", "Bob");
        DAO.createAuth(newAuth);
        assertEquals(newAuth, DAO.getAuth(newAuth.authToken()));
    }

    @Test
    void deleteAuth() {
        AuthData newAuth = new AuthData("abc123", "Bob");
        DAO.createAuth(newAuth);
        assertDoesNotThrow(() -> DAO.deleteAuth(newAuth.authToken()));
        assertThrows(RuntimeException.class, () -> DAO.getAuth(newAuth.authToken()));
    }

    @Test
    void clear() {
        for (int i = 0; i < 5; i++) {
            AuthData newAuth = new AuthData(String.format("abc123%d", i), String.format("Bob%d", i));
            DAO.createAuth(newAuth);
            assertEquals(newAuth, DAO.getAuth(newAuth.authToken()));
        }
        assertDoesNotThrow(() -> DAO.clear());
        for (int i = 0; i < 5; i++) {
            AuthData newAuth = new AuthData(String.format("abc123%d", i), String.format("Bob%d", i));
            assertThrows(RuntimeException.class, () -> DAO.getAuth(newAuth.authToken()));
        }
    }
}