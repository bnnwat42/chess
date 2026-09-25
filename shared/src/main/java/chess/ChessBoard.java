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
    }

    public ChessBoard(ChessBoard toCopy){
        board = new ChessPiece[8][8];

        for (int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                board[i][j] = toCopy.getPiece(new ChessPosition(i+1, j+1));
            }
        }
    }

    public ChessBoard(ChessBoard base, ChessMove move){
        board = new ChessPiece[8][8];
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        for (int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                board[i][j] = base.getPiece(new ChessPosition(i+1, j+1));
            }
        }
        ChessPiece.PieceType newType = (move.getPromotionPiece() == null) ? base.getPiece(start).getPieceType() : move.getPromotionPiece();
        this.addPiece(end, new ChessPiece(base.getPiece(start).getTeamColor(), newType));
        this.addPiece(start, null);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
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

    private ArrayList<ChessPosition> getPiecePositions(ChessGame.TeamColor teamColor){
        ArrayList<ChessPosition> piecePositions = new ArrayList<>();
        for (int i=0; i<8; i++) {
            for (int j=0; j < 8; j++) {
                if(board[i][j] != null && board[i][j].getTeamColor() == teamColor){
                    piecePositions.add(new ChessPosition(i+1, j+1));
                }
            }
        }
        return piecePositions;
    }

    private ChessPosition getKingPosition(ChessGame.TeamColor teamColor) {
        for(ChessPosition c : getPiecePositions(teamColor)){
            if(this.getPiece(c).getPieceType() == KING){
                return c;
            }
        }
        //TODO: Throw exception instead of return null
        return null ;
    }


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        for(ChessGame.TeamColor color : new ChessGame.TeamColor[] {WHITE, BLACK}){
            //Pawns
            int side = (color == WHITE) ? 2 : 7;
            for(int i=1; i<=8; i++){
                this.addPiece(new ChessPosition(side, i), new ChessPiece(color, PAWN));
            }
            side = (color == WHITE) ? 1 : 8;
            //Rooks
            this.addPiece(new ChessPosition(side, 1), new ChessPiece(color, ROOK));
            this.addPiece(new ChessPosition(side, 8), new ChessPiece(color, ROOK));
            //Knights
            this.addPiece(new ChessPosition(side, 2), new ChessPiece(color, KNIGHT));
            this.addPiece(new ChessPosition(side, 7), new ChessPiece(color, KNIGHT));
            //Bishops
            this.addPiece(new ChessPosition(side, 3), new ChessPiece(color, BISHOP));
            this.addPiece(new ChessPosition(side, 6), new ChessPiece(color, BISHOP));
            //Queen and King
            this.addPiece(new ChessPosition(side, 4), new ChessPiece(color, QUEEN));
            this.addPiece(new ChessPosition(side, 5), new ChessPiece(color, KING));
        }
    }

    public boolean isInCheck(ChessGame.TeamColor teamColor) {
        //We go through each piece and pretend the king is that piece. If it can take an enemy piece
        //of the corresponding type, we know the corresponding piece can also take it
        ChessBoard testBoard = new ChessBoard(this);
        ChessPosition kingPosition = this.getKingPosition(teamColor);

        for(ChessPiece.PieceType p : ChessPiece.PieceType.values()) {
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
        Collection<ChessPosition> piecePositions = getPiecePositions(teamColor);

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
        //If there weren't any valid moves out, it's surrounded
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
