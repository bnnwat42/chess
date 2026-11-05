package service;

import exceptions.AlreadyTakenException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService myLogin;
    private UserData testUser;

    @BeforeEach
    void setUp() {
        myLogin = assertDoesNotThrow(() -> new UserService());
        assertDoesNotThrow(() -> myLogin.clear());
        testUser = new UserData("Bob", "1234", "cosmo@byu.edu");
    }

    @Test
    void registerRealPerson() {
        assertDoesNotThrow(() -> myLogin.register(testUser));
        assertThrows(AlreadyTakenException.class, () -> myLogin.register(testUser));
    }

    @Test
    void login() {
        AuthData firstAuth = assertDoesNotThrow(() -> myLogin.register(testUser));

        assertThrows(UnauthorizedException.class, () -> myLogin.login(new UserData("Tom", "4321", "devil@asu.edu")));
        AuthData secondAuth = assertDoesNotThrow(() -> myLogin.login(testUser));
        assertNotEquals(firstAuth, secondAuth);
    }

    @Test
    void logout() {
        AuthData authData = assertDoesNotThrow(() -> myLogin.register(testUser));

        assertDoesNotThrow(() -> myLogin.logout(authData.authToken()));
        assertThrows(UnauthorizedException.class, () -> myLogin.logout("invalid-token-xyz"));
    }
}