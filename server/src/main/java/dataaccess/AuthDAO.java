package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData);

    AuthData getAuth(String authIdentifier);

    void deleteAuth(String authIdentifier);
}
