package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.memoryImplementations.MemoryAuthDAO;
import dataaccess.memoryImplementations.MemoryUserDAO;
import exceptions.AlreadyTakenException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
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
            throw new UnauthorizedException();
        } else if (!Objects.equals(user.password(), loginRequest.password())) {
            throw new UnauthorizedException();
        }

        return createNewAuth(loginRequest);
    }

    private AuthData createNewAuth(UserData userData) {
        AuthData newUserAuth = new AuthData(generateToken(), userData.username());
        authAccess.createAuth(newUserAuth);
        return newUserAuth;
    }

    public void logout(String authToken) throws UnauthorizedException {
        if (authAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }

        authAccess.deleteAuth(authToken);
    }

    public void clear() {
        userAccess.clear();
        authAccess.clear();
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}