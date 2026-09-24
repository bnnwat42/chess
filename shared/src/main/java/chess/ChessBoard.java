package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static chess.ChessPiece.PieceType.*;
import static chess.ChessGame.TeamColor.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board;
    private ChessPosition whiteKingPos;
    private ChessPosition blackKingPos;
    private Collection<ChessPosition> whitePieces;
    private Collection<ChessPosition> blackPieces;


    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        } else if(obj instanceof ChessBoard objBoard){
            for(int i=0; i<8; i++){
                for(int j=0; j<8; j++){
                    ChessPiece toCompare = objBoard.getPiece(new ChessPosition(i+1,j+1));
                    if(board[i][j] == null || toCompare == null){
                        if(board[i][j] != toCompare){
                            return false;
                        }
                    } else if(!(board[i][j].equals(toCompare))){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        var name = "|";
        for(ChessPiece[] c: board){
            for(ChessPiece p : c){
                if(p == null){
                    name += " |";
                } else {
                    name += p.toString() + "|";
                }
            }
            name += "\n|";
        }
        return name;
    }

    public ChessBoard() {
        board = new ChessPiece[8][8];
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
    }

    public ChessBoard(ChessBoard toCopy){
        board = new ChessPiece[8][8];
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();

        for (int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                board[i][j] = toCopy.getPiece(new ChessPosition(i+1, j+1));
                //Makes a list of all positions of each piece of each color
                if(board[i][j] != null){
                    if(board[i][j].getTeamColor() == WHITE){
                        whitePieces.add(new ChessPosition(i+1, j+1));
                        if(board[i][j].getPieceType() == KING){
                            whiteKingPos = new ChessPosition(i+1, j+1);
                        }
                    } else if(board[i][j].getTeamColor() == BLACK){
                        blackPieces.add(new ChessPosition(i+1, j+1));
                        if(board[i][j].getPieceType() == KING){
                            blackKingPos = new ChessPosition(i+1, j+1);
                        }
                    }
                }
            }
        }
    }

    private ChessPosition getKingPosition(ChessGame.TeamColor color){
        if(color == WHITE){
            return whiteKingPos;
        } if(color == BLACK){
            return blackKingPos;
        } else {
            return null;
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if (piece != null && piece.getPieceType() == KING){
            if(piece.getTeamColor() == WHITE){
                whiteKingPos = position;
            } else if(piece.getTeamColor() == BLACK){
                blackKingPos = position;
            }
        }
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        whiteKingPos = new ChessPosition(1,5);
        blackKingPos = new ChessPosition(8,5);
        for(ChessGame.TeamColor color : new ChessGame.TeamColor[] {WHITE, BLACK}){
            //Pawns
            int side = (color == WHITE) ? 1 : 6;
            for(int i=0; i<8; i++){
                board[side][i] = new ChessPiece(color, PAWN);
            }
            side = (color == WHITE) ? 0 : 7;
            //Rooks
            board[side][0] = new ChessPiece(color, ROOK);
            board[side][7] = new ChessPiece(color, ROOK);
            //Knights
            board[side][1] = new ChessPiece(color, KNIGHT);
            board[side][6] = new ChessPiece(color, KNIGHT);
            //Bishops
            board[side][2] = new ChessPiece(color, BISHOP);
            board[side][5] = new ChessPiece(color, BISHOP);
            //Queen and King
            board[side][3] = new ChessPiece(color, QUEEN);
            board[side][4] = new ChessPiece(color, KING);
        }
    }

    public boolean isInCheck(ChessGame.TeamColor teamColor) {
        //We go through each piece and pretend the king is that piece. If it can take an enemy piece
        //of the corresponding type, we know the corresponding piece can also take it
        ChessBoard testBoard = new ChessBoard(this);
        ChessPosition kingPosition = this.getKingPosition(teamColor);

        for(ChessPiece.PieceType p : ChessPiece.PieceType.values()) {
            if(p == KING){
                continue;
            }
            ChessPiece holderPiece = new ChessPiece(teamColor, p);
            testBoard.addPiece(kingPosition, holderPiece);
            Collection<ChessMove> moves = holderPiece.pieceMoves(testBoard, kingPosition);
            for (ChessMove m : moves) {
                //If the pieceType of one of the moves is the same as the check typed, it can take king
                if (testBoard.getPiece(m.getEndPosition()) != null && testBoard.getPiece(m.getEndPosition()).getPieceType() == p) {
                    return true;
                }
            }
        }
        return false;
    }

    //Used by the checkmate and stalemate methods
    private boolean isSurrounded(ChessGame.TeamColor teamColor) {
        //All positions of pieces of select color
        Collection<ChessPosition> piecePositions = (teamColor == WHITE) ? whitePieces : blackPieces;

        //For each position that has a piece of that color
        for(ChessPosition testPosition : piecePositions) {
            //Get the piece
            ChessPiece testPiece = this.getPiece(testPosition);
            //For each possible move that piece could make
            for (ChessMove m : testPiece.pieceMoves(this, testPosition)) {
                //Create a testBoard where that move is done
                ChessBoard testBoard = new ChessBoard(this);
                testBoard.addPiece(m.getEndPosition(), testPiece);
                testBoard.addPiece(m.getStartPosition(), null);
                //Check if in check. If it isn't, there is a valid move out of stalemate
                if (!testBoard.isInCheck(teamColor)) {
                    return false;
                }
            }
        }
        //If there weren't any valid moves out, it is in stalemate
        return true;
    }

    public boolean isInCheckmate(ChessGame.TeamColor teamColor) {
        //If it has no valid escapes and is in check, it is checkmated
        return this.isInCheck(teamColor) && this.isSurrounded(teamColor);
    }

    public boolean isInStalemate(ChessGame.TeamColor teamColor) {
        //If it has no valid escapes and isn't in check, it is stalemated
        return !this.isInCheck(teamColor) && this.isSurrounded(teamColor);
    }
}
