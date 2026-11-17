package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import static java.lang.Math.abs;


public class BoardRenderer {
    public static void renderBoard(ChessGame game, ChessGame.TeamColor color) {
        ChessBoard board = game.getBoard();

        int start = 0;
        int end = 0;
        if (color == ChessGame.TeamColor.BLACK) {
            start = 0;
            end = 7;
        } else if (color == ChessGame.TeamColor.WHITE) {
            start = -7;
            end = 0;
        }

        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.ERASE_SCREEN);
        System.out.print("   ");
        for (int i = start; i <= end; i++) {
            System.out.print(" " + (char) (97 + abs(i)) + " ");
        }

        System.out.println();

        for (int i = start; i <= end; i++) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + " " + (abs(i) + 1) + " ");
            for (int j = start; j <= end; j++) {
                if ((abs(i) + abs(j)) % 2 == 1) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                }
                ChessPiece piece = board.getPiece(new ChessPosition(abs(i) + 1, abs(j) + 1));
                if (piece == null) {
                    System.out.print("   ");
                } else {
                    System.out.print(convertPiece(piece));
                }
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_WHITE);
            System.out.println(" " + (abs(i) + 1) + " ");
        }
        System.out.print("   ");
        for (int i = start; i <= end; i++) {
            System.out.print(" " + (char) (97 + abs(i)) + " ");
        }
    }

    private static String convertPiece(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            return EscapeSequences.SET_TEXT_COLOR_BLACK +
                    switch (piece.toString()) {
                        case "p" -> EscapeSequences.BLACK_PAWN;
                        case "r" -> EscapeSequences.BLACK_ROOK;
                        case "n" -> EscapeSequences.BLACK_KNIGHT;
                        case "b" -> EscapeSequences.BLACK_BISHOP;
                        case "q" -> EscapeSequences.BLACK_QUEEN;
                        case "k" -> EscapeSequences.BLACK_KING;
                        default -> throw new IllegalStateException("Unexpected value: " + piece.toString());
                    };
        } else {
            return EscapeSequences.SET_TEXT_COLOR_WHITE +
                    switch (piece.toString()) {
                        case "P" -> EscapeSequences.WHITE_PAWN;
                        case "R" -> EscapeSequences.WHITE_ROOK;
                        case "N" -> EscapeSequences.WHITE_KNIGHT;
                        case "B" -> EscapeSequences.WHITE_BISHOP;
                        case "Q" -> EscapeSequences.WHITE_QUEEN;
                        case "K" -> EscapeSequences.WHITE_KING;
                        default -> throw new IllegalStateException("Unexpected value: " + piece.toString());
                    };
        }
    }
}
