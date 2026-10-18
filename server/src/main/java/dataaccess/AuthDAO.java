package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData);

    Object getAuth(Object authIdentifier);

    void deleteAuth(Object authIdentifier);
}
