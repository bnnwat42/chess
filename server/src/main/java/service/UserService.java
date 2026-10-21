package service;

import dataaccess.*;
import model.*;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    private final UserDAO userAccess;
    private final AuthDAO authAccess;

    public UserService() {
        userAccess = new MemoryUserDAO();
        authAccess = new MemoryAuthDAO();
    }

    public AuthData register(UserData newUser) throws AlreadyTakenException {
        userAccess.createUser(newUser);
        return createNewAuth(newUser);
    }

    public AuthData login(UserData loginRequest) throws DataAccessException {
        UserData user = userAccess.getUser(loginRequest.username());
        if (user == null) {
            throw new DataAccessException("User does not exist");
        } else if (!Objects.equals(user.password(), loginRequest.password())) {
            throw new DataAccessException("Invalid password");
        }

        return createNewAuth(loginRequest);
    }

    private AuthData createNewAuth(UserData userData) {
        AuthData newUserAuth = new AuthData(generateToken(), userData.username());
        authAccess.createAuth(newUserAuth);
        return newUserAuth;
    }

    public void logout(String authToken) throws DataAccessException {
        if (authAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Auth token does not exist");
        }

        authAccess.deleteAuth(authToken);
    }

    public void clear() {
        userAccess.clear();
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}