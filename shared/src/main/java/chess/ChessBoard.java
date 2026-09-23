package chess;

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
                if(board[i][j] != null && board[i][j].getPieceType() == KING){
                    if(board[i][j].getTeamColor() == WHITE){
                        whiteKingPos = new ChessPosition(i+1, j+1);
                    } else if(board[i][j].getTeamColor() == BLACK){
                        blackKingPos = new ChessPosition(i+1, j+1);
                    }
                }
            }
        }
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor color){
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
        //We go through each piece and pretend the king is one of them. If it can take an enemy piece
        //of the corresponding piece, we know the corresponding piece can also take it
        ChessPosition kingPosition = this.getKingPosition(teamColor);

        for(ChessPiece.PieceType p : ChessPiece.PieceType.values()) {
            if(p == KING){
                continue;
            }
            ChessPiece holderPiece = new ChessPiece(teamColor, p);
            this.addPiece(kingPosition, holderPiece);
            Collection<ChessMove> moves = holderPiece.pieceMoves(this, kingPosition);
            for (ChessMove m : moves) {
                //If the pieceType of one of the moves is the same as the check typed, it can take king
                if (this.getPiece(m.getEndPosition()) != null && this.getPiece(m.getEndPosition()).getPieceType() == p) {
                    return true;
                }
            }
        }
        return false;
    }
}
