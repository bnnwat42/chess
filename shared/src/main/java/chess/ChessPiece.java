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

            if(myPiece.getPieceType() == PieceType.ROOK ||
                    myPiece.getPieceType() == PieceType.BISHOP ||
                    myPiece.getPieceType() == PieceType.QUEEN ||
                    myPiece.getPieceType() == PieceType.KING){
                //Queen, rook, king and bishop use functionally the same code
                int[][] directions = {};
                if(myPiece.getPieceType() == PieceType.ROOK) {
                    directions = new int[][] {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
                } else if(myPiece.getPieceType() == PieceType.BISHOP) {
                    directions = new int[][] {{1,1},{-1,-1},{1,-1},{-1,1}};
                }
                //The king is just a mini queen
                else if(myPiece.getPieceType() == PieceType.QUEEN || myPiece.getPieceType() == PieceType.KING) {
                    directions = new int[][] {{1,0},{-1,0},{0,-1},{0,1},{1,1},{-1,-1},{1,-1},{-1,1}};
                }
                ChessPosition tempPosition;
                ChessPiece targetPiece;
                for (int[] d : directions){
                    int counter = 0;
                    //If the piece is king it only needs to search one deep
                    while (myPiece.getPieceType() != PieceType.KING || counter < 1){
                        counter++;
                        tempPosition = new ChessPosition(myPosition, d[0]*counter, d[1]*counter);
                        if(OutofBounds(tempPosition)){
                            break;
                        }
                        targetPiece = board.getPiece(tempPosition);
                        if (targetPiece == null){
                            moves.add(new ChessMove(myPosition, tempPosition, null));
                        } else if (targetPiece.getTeamColor() != myPiece.getTeamColor()){
                            moves.add(new ChessMove(myPosition, tempPosition, null));
                            break;
                        } else {
                            break;
                        }
                    }
                }
            }
            if(myPiece.getPieceType() == PieceType.KNIGHT) {
                //Its really dumb, but it works
                int[] shifts = {1, 2, -1, -2, 1, -2, -1, 2, 1};
                ChessPosition tempPosition;
                ChessPiece targetPiece;
                for (int i = 0; i < shifts.length-1; i++) {
                    tempPosition = new ChessPosition(myPosition, shifts[i], shifts[i + 1]);
                    if (OutofBounds(tempPosition)){
                        continue;
                    }
                    targetPiece = board.getPiece(tempPosition);
                    if(targetPiece == null || targetPiece.getTeamColor() != myPiece.getTeamColor()){
                        moves.add(new ChessMove(myPosition, tempPosition, null));
                    }
                }
            }
            /*if(myPiece.getPieceType() == PieceType.KING){
                ChessPosition tempPosition;
                ChessPiece targetPiece;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i == 0 && j == 0) {
                            continue;
                        }
                        tempPosition = new ChessPosition(myPosition, i, j);
                        if (OutofBounds(tempPosition)) {
                            continue;
                        }
                        targetPiece = board.getPiece(tempPosition);
                        if (targetPiece != null && targetPiece.getTeamColor() == myPiece.getTeamColor()) {
                            continue;
                        }
                        moves.add(new ChessMove(myPosition, tempPosition, null));
                    }
                }
            }*/
            if(myPiece.getPieceType() == PieceType.PAWN){
                int direction = (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
                ChessPosition tempPosition = new ChessPosition(myPosition, direction, 0);
                ChessPiece targetPiece = board.getPiece(tempPosition);
                //Checks if square directly in front of pawn is empty
                if (targetPiece == null) {
                    //Check if pawn can promote
                    if(tempPosition.getRow() == 1 || tempPosition.getRow() == 8){
                        moves.addAll(makePromotionMoves(myPosition,tempPosition));
                    } else {
                        moves.add(new ChessMove(myPosition, tempPosition, null));
                    }
                //If on starting square, checks if 2 in front is empty as well
                    if (myPosition.getRow() == ((direction == 1) ? 2 : 7)){
                        tempPosition = new ChessPosition(myPosition, direction*2, 0);
                        targetPiece = board.getPiece(tempPosition);
                        if (targetPiece == null) {
                            moves.add(new ChessMove(myPosition, tempPosition, null));
                        }
                    }
                }
                //Checks if there is an enemy piece to take diagonally
                int[] sides = {-1,1};
                for(int i : sides){
                    tempPosition = new ChessPosition(myPosition, direction, i);
                    if(OutofBounds(tempPosition)){
                        continue;
                    }
                    targetPiece = board.getPiece(tempPosition);
                    if (targetPiece != null && board.getPiece(tempPosition).getTeamColor() != myPiece.getTeamColor()){
                        //Check if pawn can promote
                        if(tempPosition.getRow() == 1 || tempPosition.getRow() == 8){
                            moves.addAll(makePromotionMoves(myPosition,tempPosition));
                        } else {
                            moves.add(new ChessMove(myPosition, tempPosition, null));
                        }
                    }
                }
        }
        return moves;
    }

    /**@return All promotion pieces of a pawn*/
    private Collection<ChessMove> makePromotionMoves(ChessPosition startPosition, ChessPosition endPosition){
        ChessPiece.PieceType[] pieces = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        for(ChessPiece.PieceType p : pieces){
            moves.add(new ChessMove(startPosition, endPosition, p));
        }
        return moves;
    }

    //Checks if position is out of bounds
    private boolean OutofBounds(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return 9 <= row || row <= 0 || col <= 0 || 9 <= col;
    }
}
