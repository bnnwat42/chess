package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

    final private HashMap<String, AuthData> authStorage = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) {
        authStorage.put(authData.authToken(), authData);
    }

    @Override
    public Object getAuth(Object authIdentifier) {
        return null;
    }

    @Override
    public void deleteAuth(Object authIdentifier) {

    }
}
