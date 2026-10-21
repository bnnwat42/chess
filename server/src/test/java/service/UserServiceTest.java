package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @BeforeEach
    void setUp() {
        MemoryAuthDAO.clear();
        MemoryUserDAO.clear();
    }

    @Test
    void registerRealPerson() {
        UserService myLogin = new UserService();
        UserData newUser = new UserData("Bob", "1234", "cosmo@byu.edu");

        assertDoesNotThrow(() -> myLogin.register(newUser));
        assertThrows(DataAccessException.class, () -> myLogin.register(newUser));
    }

    @Test
    void login() {
        UserService myLogin = new UserService();
        UserData newUser = new UserData("Bob", "1234", "cosmo@byu.edu");
        AuthData newAuth = assertDoesNotThrow(() -> myLogin.register(newUser));

        assertThrows(DataAccessException.class, () -> myLogin.login(new UserData("Tom", "4321", "devil@asu.edu")));
        //assertEquals(myLogin, myLogin);
    }

    @Test
    void logout() {
    }
}