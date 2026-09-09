package chess;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    public ChessPiece(ChessPiece toCopy){
        this.color = toCopy.getTeamColor();
        this.type = toCopy.getPieceType();
    }

    @Override
    public int hashCode() {
        return color.hashCode() * type.hashCode();
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
        ChessPiece objPiece = (ChessPiece) obj;
        return (color == objPiece.getTeamColor()
                && type == objPiece.getPieceType());
    }

    @Override
    public String toString() {
        return type.name().substring(0,1) + color.name().substring(0,1);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece myPiece = board.getPiece(myPosition);
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();

        switch (myPiece.getPieceType()){
            case ROOK:
                break;
            case BISHOP:
                break;
            case QUEEN:
                break;
            case KNIGHT:
                break;
            case KING:
                for (int i=-1;i<=1;i++){
                    for (int j=-1;j<=1;j++){
                        if(i==0 && j==0){
                            continue;
                        }
                        ChessPosition tempPositon = new ChessPosition(myPosition, i, j);
                        if(tempPositon.OutofBounds()){
                            continue;
                        }
                        ChessPiece tempPiece = board.getPiece(tempPositon);
                        if (tempPiece != null && tempPiece.getTeamColor() == myPiece.getTeamColor()){
                            continue;
                        }
                        moves.add(new ChessMove(myPosition, tempPositon, null));
                    }
                }
                break;
            case PAWN:
                break;
        }
        return moves;
    }
}
