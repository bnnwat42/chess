package server;

import dataaccess.sql.DatabaseManager;

public class ServerMain {
    public static void main(String[] args) {
        var port = 8080;
        Server server = new Server();
        try {
            DatabaseManager.createDatabase();
        } catch (Exception e) {

        }
        server.run(port);

        System.out.println("♕ 240 Chess Server running on port " + port);
    }
}
