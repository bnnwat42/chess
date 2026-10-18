package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.*;

import java.util.UUID;

public class UserService {

    private final UserDAO userAccess;
    private final AuthDAO authAccess;

    public UserService(UserDAO userAccess, AuthDAO authAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
    }

    public void register(UserData newUser) throws DataAccessException {
        if (userAccess.getUser(newUser.username()) != null) {
            throw new DataAccessException("User already exists");
        }
        userAccess.createUser(newUser);

        AuthData newUserAuth = new AuthData(generateToken(), newUser.username());

        authAccess.createAuth(newUserAuth);
    }

//    public LoginResult login(LoginRequest loginRequest) {
//    }
//
//    public void logout(LogoutRequest logoutRequest) {
//    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}