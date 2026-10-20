package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData);

    Object getAuth(String authIdentifier);

    void deleteAuth(String authIdentifier);
}
