package dataaccess;

import dataaccess.sqlImpementations.DatabaseManager;
import dataaccess.sqlImpementations.SQLUserDAO;
import exceptions.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

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
            DatabaseManager.updateDatabase("DROP TABLE IF EXISTS users");
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        DAO = new SQLUserDAO();
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
        var retrivedUse = DAO.getUser("Bob");
        assertEquals(newUser.email(), retrivedUse.email());
        assertEquals(newUser.username(), retrivedUse.username());
        assertTrue(SQLUserDAO.checkPassword(newUser.password(), retrivedUse.password()));
    }

    @Test
    void clear() {
        UserData newUser = new UserData("Bob", "1234", "Bob@byu.edu");
        assertDoesNotThrow(() -> DAO.createUser(newUser));
        DAO.clear();
        assertThrows(NoSuchElementException.class, () -> DAO.getUser("Bob"));
    }
}