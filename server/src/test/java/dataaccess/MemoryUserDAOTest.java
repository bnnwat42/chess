package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryUserDAOTest {

    @Test
    void createUserAndGetUser() {
        MemoryUserDAO memoryStorage = new MemoryUserDAO();
        UserData newUser = new UserData("Bob", "1234", "bobby@tables.xkcd");
        memoryStorage.createUser(newUser);

        UserData returnedUser = memoryStorage.getUser("Bob");

        assertEquals("Bob", returnedUser.username());
        assertEquals("1234", returnedUser.password());
        assertEquals("bobby@tables.xkcd", returnedUser.email());
    }
}