package dataaccess.interfaces;

import exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authIdentifier) throws DataAccessException;

    void deleteAuth(String authIdentifier) throws DataAccessException;

    void clear() throws DataAccessException;
}
