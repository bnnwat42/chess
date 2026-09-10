package chess;

import java.util.Arrays;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public boolean equals(Object obj) {
        //The obj is equivalent to itself
        if (obj == this){
            return true;
        }
        //Cannot compare if obj is not a ChessPiece or null
        if (obj == null || obj.getClass() != getClass()){
            return false;
        }
        ChessBoard objBoard = (ChessBoard) obj;
        for (int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                if(board[i][j] == null){
                    if (objBoard.getPiece(new ChessPosition(i+1,j+1)) == null){
                        continue;
                    } else {
                        return false;
                    }
                }
                if(!board[i][j].equals(objBoard.getPiece(new ChessPosition(i+1,j+1)))){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = (piece == null) ? null : new ChessPiece(piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        ChessPiece thisPiece = board[position.getRow()-1][position.getColumn()-1];
        if (thisPiece == null){
            return null;
        }
        return new ChessPiece(thisPiece);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //First Pawns
        for(int i=0; i<8; i++){
            board[1][i] = new ChessPiece(WHITE, PAWN);;
            board[6][i] = new ChessPiece(BLACK, PAWN);
        }
        int[] rows = {0, 7};
        ChessGame.TeamColor[] colors = {WHITE, BLACK};
        //Loops through the Whites, then the blacks
        for (int i=0; i<2; i++) {
            //Rooks
            board[rows[i]][0] = new ChessPiece(colors[i], ROOK);
            board[rows[i]][7] = new ChessPiece(colors[i], ROOK);
            //Knights
            board[rows[i]][1] = new ChessPiece(colors[i], KNIGHT);
            board[rows[i]][6] = new ChessPiece(colors[i], KNIGHT);
            //Bishops
            board[rows[i]][2] = new ChessPiece(colors[i], BISHOP);
            board[rows[i]][5] = new ChessPiece(colors[i], BISHOP);
            //Queen
            board[rows[i]][3] = new ChessPiece(colors[i], QUEEN);
            //King
            board[rows[i]][4] = new ChessPiece(colors[i], KING);
        }
    }

    @Override
    public String toString() {
        String str = "";
        for(ChessPiece[] i : board){
            for(ChessPiece j : i){
                if(j == null){
                    str += "null, ";
                    continue;
                }
                str += j.toString() + ", ";
            }
            str += "\n";
        }
        return str;
    }
}
