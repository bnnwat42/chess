package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.memoryImplementations.MemoryAuthDAO;
import dataaccess.memoryImplementations.MemoryUserDAO;
import dataaccess.sqlImpementations.SQLAuthDAO;
import dataaccess.sqlImpementations.SQLUserDAO;
import exceptions.AlreadyTakenException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.util.Objects;
import java.util.UUID;

public class UserService {

    private final UserDAO userAccess;
    private final AuthDAO authAccess;

    public UserService() throws DataAccessException {
        //userAccess = new MemoryUserDAO();
        userAccess = new SQLUserDAO();
        //authAccess = new MemoryAuthDAO();
        authAccess = new SQLAuthDAO();
    }

    public AuthData register(UserData newUser) throws AlreadyTakenException, DataAccessException {
        userAccess.createUser(newUser);
        return createNewAuth(newUser);
    }

    public AuthData login(UserData loginRequest) throws DataAccessException {
        UserData user = userAccess.getUser(loginRequest.username());
        if (user == null) {
            throw new UnauthorizedException();
        } else if (!BCrypt.checkpw(loginRequest.password(), user.password())) {
            throw new UnauthorizedException();
        }

        return createNewAuth(loginRequest);
    }

    private AuthData createNewAuth(UserData userData) throws DataAccessException {
        AuthData newUserAuth = new AuthData(generateToken(), userData.username());
        authAccess.createAuth(newUserAuth);
        return newUserAuth;
    }

    public void logout(String authToken) throws UnauthorizedException, DataAccessException {
        if (authAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }

        authAccess.deleteAuth(authToken);
    }

    public void clear() throws DataAccessException {
        userAccess.clear();
        authAccess.clear();
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}