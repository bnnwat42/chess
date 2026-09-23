package chess;

import java.util.Arrays;
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
        return super.toString();
    }

    public ChessBoard() {
        board = new ChessPiece[8][8];
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

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
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
}
