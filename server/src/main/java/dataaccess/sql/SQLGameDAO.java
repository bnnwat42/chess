package dataaccess.sql;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.interfaces.GameDAO;
import exceptions.DataAccessException;
import model.GameData;

import java.sql.SQLException;
import java.util.*;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        var createGameTable = "CREATE TABLE IF NOT EXISTS games (" +
                "gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY ," +
                "whiteUsername VARCHAR(255)," +
                "blackUsername VARCHAR(255)," +
                "gameName VARCHAR(255)," +
                "gameData TEXT NOT NULL);";
        try {
            DatabaseManager.updateDatabase(createGameTable);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        String updateString = "INSERT INTO games (gameName, gameData) VALUE (?, ?);";
        String gameJson = jsonBuilder(gameData.game());

        try {
            return Integer.parseInt(DatabaseManager.updateDatabase(updateString, gameData.gameName(), gameJson));
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameIdentifier) throws DataAccessException {
        String retrivalString = "SELECT * FROM games WHERE gameID = ?;";
        ArrayList<Map<String, String>> response;
        try {
            response = DatabaseManager.getFromDatabase(retrivalString, gameIdentifier);
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
        Map<String, String> gameMap = response.getFirst();
        ChessGame retrievedGame = deserializeGame(gameMap.get("gameData"));

        return new GameData(Integer.parseInt(gameMap.get("gameID")), gameMap.get("whiteUsername"),
                gameMap.get("blackUsername"), gameMap.get("gameName"), retrievedGame);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String retrivalString = "SELECT gameID FROM games;";
        ArrayList<Map<String, String>> response;
        try {
            response = DatabaseManager.getFromDatabase(retrivalString);
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
        ArrayList<GameData> games = new ArrayList<>();
        for (Map<String, String> row : response) {
            games.add(getGame(Integer.parseInt(row.get("gameID"))));
        }
        return games;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        String updateString = "UPDATE games SET whiteUsername = ?," +
                "blackUsername = ?," +
                "gameName = ?," +
                "gameData = ? WHERE gameID = ?;";
        String gameJson = jsonBuilder(gameData.game());

        try {
            DatabaseManager.updateDatabase(updateString, gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), gameJson, gameData.gameID());
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            DatabaseManager.updateDatabase("TRUNCATE TABLE games");
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private String jsonBuilder(ChessGame game) {
        var jsonMap = Map.of(
                "activePlayer", game.getTeamTurn().toString(),
                "boardState", game.getBoard().serilizeBoard());

        var serializer = new Gson();
        return serializer.toJson(jsonMap);
    }

    private ChessGame deserializeGame(String gameJson) {
        Gson gson = new Gson();
        var gameMap = gson.fromJson(gameJson, Map.class);

        ChessGame retrievedGame = new ChessGame();
        String[][] boardString = gson.fromJson(gameMap.get("boardState").toString(), String[][].class);
        ChessBoard gameBoard = deserializeBoard(boardString);
        retrievedGame.setBoard(gameBoard);
        retrievedGame.setTeamTurn(ChessGame.TeamColor.valueOf(gameMap.get("activePlayer").toString()));
        return retrievedGame;
    }

    private ChessBoard deserializeBoard(String[][] boardState) {
        ChessBoard constructedBoard = new ChessBoard();
        constructedBoard.clearBoard();

        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState[i].length; j++) {
                ChessPosition position = new ChessPosition(i + 1, j + 1);
                String piece = boardState[i][j];

                if (piece == null) {
                    continue;
                }

                ChessGame.TeamColor color = Character.isLowerCase(piece.charAt(0))
                        ? ChessGame.TeamColor.WHITE
                        : ChessGame.TeamColor.BLACK;

                ChessPiece.PieceType type = switch (piece.toUpperCase()) {
                    case "N" -> ChessPiece.PieceType.KNIGHT;
                    case "B" -> ChessPiece.PieceType.BISHOP;
                    case "R" -> ChessPiece.PieceType.ROOK;
                    case "Q" -> ChessPiece.PieceType.QUEEN;
                    case "K" -> ChessPiece.PieceType.KING;
                    case "P" -> ChessPiece.PieceType.PAWN;
                    default -> throw new IllegalArgumentException("Invalid piece: " + piece);
                };

                constructedBoard.addPiece(position, new ChessPiece(color, type));
            }
        }
        return constructedBoard;
    }

}