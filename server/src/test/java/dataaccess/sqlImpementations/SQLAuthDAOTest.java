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
            DatabaseManager.updateDatabase("DROP TABLE IF EXISTS auth");
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
    }

    @Test
    void deleteAuth() {
    }

    @Test
    void clear() {
    }
}