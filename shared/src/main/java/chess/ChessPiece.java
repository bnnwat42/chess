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

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public ChessPiece(ChessPiece toCopy){
        this.pieceColor = toCopy.getTeamColor();
        this.type = toCopy.getPieceType();
    }

        @Override
    public int hashCode() {
        return pieceColor.hashCode() * type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        } else if(obj instanceof chess.ChessPiece objPiece){
            return objPiece.getPieceType() == type && objPiece.getTeamColor() == pieceColor;
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString();
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
        return pieceColor;
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
        ChessGame.TeamColor myColor = myPiece.getTeamColor();
        PieceType myType = myPiece.getPieceType();

        ArrayList<ChessMove> myMoves = new ArrayList<>();
        int[][] directions = {};

        int depth = -1;
        switch (myType){
            case PAWN:
                return pawnsAreSpecial(board, myPosition, myColor);
            case ROOK:
                directions = new int[][] {{1,0},{0,1},{-1,0},{0,-1}};
                break;
            case BISHOP:
                directions = new int[][] {{1,1},{1,-1},{-1,1},{-1,-1}};
                break;
            case QUEEN:
                directions = new int[][] {{1,0},{0,1},{-1,0},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
                break;
            case KING:
                directions = new int[][] {{1,0},{0,1},{-1,0},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
                depth = 1;
                break;
            case KNIGHT:
                directions = new int[][] {{1,2},{2,1},{-1,2},{2,-1},{-2,1},{1,-2},{-1,-2},{-2,-1}};
                depth = 1;
                break;
        }

        for(int[] dir : directions){
            int counter = 0;
            while (depth != counter) {
                counter++;
                ChessPosition positionToConsider = new ChessPosition(myPosition, counter * dir[0], counter * dir[1]);
                if(positionToConsider.outOfBounds()){
                    break;
                }
                chess.ChessPiece pieceToConsider = board.getPiece(positionToConsider);
                if (pieceToConsider == null) {
                    myMoves.add(new ChessMove(myPosition, positionToConsider, null));
                } else if (pieceToConsider.pieceColor != myColor) {
                    myMoves.add(new ChessMove(myPosition, positionToConsider, null));
                    break;
                } else {
                    break;
                }
            }
        }
        return myMoves;
    }

    private Collection<ChessMove> pawnsAreSpecial(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        ArrayList<ChessMove> pawnList = new ArrayList<>();
        int dir = (myColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

        ChessPosition testPos;
        //Check in front
        testPos = new ChessPosition(myPosition, dir, 0);
        if(board.getPiece(testPos) == null){
            pawnList.addAll(toPromoteOrNotToPromote(myPosition, testPos));
            if(myPosition.getRow() == ((dir == 1) ? 2 : 7)) {
                testPos = new ChessPosition(myPosition, dir * 2, 0);
                if (board.getPiece(testPos) == null) {
                    pawnList.addAll(toPromoteOrNotToPromote(myPosition, testPos));
                }
            }
        }
        //Check sides
        for(int i : new int[] {-1,1}) {
            testPos = new ChessPosition(myPosition, dir, i);
            if(testPos.outOfBounds()){
                break;
            }
            if (board.getPiece(testPos) != null) {
                if (board.getPiece(testPos).getTeamColor() != myColor) {
                    pawnList.addAll(toPromoteOrNotToPromote(myPosition, testPos));
                }
            }
        }
        return pawnList;
    }

    private Collection<ChessMove> toPromoteOrNotToPromote(ChessPosition startPosition, ChessPosition endPosition){
        ArrayList<ChessMove> promotionList = new ArrayList<>();

        if(endPosition.getRow() == 1 || endPosition.getRow() == 8){
            promotionList.add(new ChessMove(startPosition, endPosition, PieceType.QUEEN));
            promotionList.add(new ChessMove(startPosition, endPosition, PieceType.ROOK));
            promotionList.add(new ChessMove(startPosition, endPosition, PieceType.BISHOP));
            promotionList.add(new ChessMove(startPosition, endPosition, PieceType.KNIGHT));
        } else {
            promotionList.add(new ChessMove(startPosition, endPosition, null));
        }
        return promotionList;
    }
}

