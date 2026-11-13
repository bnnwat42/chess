import client.ServerFacade;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private ServerFacade facade;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void createFacade() {
        facade = new ServerFacade("localhost", port);
        facade.clearDatabase();
    }

    @Test
    public void clearDatabase() {
        facade.clearDatabase();
    }

    @Test
    public void registerUser() {
        UserData newUser = new UserData("Bob", "1234", "gocougs@byu.edu");
        facade.registerUser(newUser);
    }

    @Test
    public void loginUser() {
        UserData newUser = new UserData("Bob", "1234", "gocougs@byu.edu");
        facade.registerUser(newUser);
        var authToken = facade.loginUser(newUser);
    }

}
