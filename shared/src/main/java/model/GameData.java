package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData(GameData oldGame, int newID) {
        this(newID, oldGame.whiteUsername, oldGame.blackUsername, oldGame.gameName, oldGame.game);
    }
}