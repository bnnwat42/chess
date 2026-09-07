package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return col;
    }

    //This will work as long as the board remains 8 wide and 8 tall
    @Override
    public int hashCode() {
        return row * (col + 8);
    }

    @Override
    public boolean equals(Object obj) {
        //The obj is equivalent to itself
        if (obj == this){
            return true;
        }
        //Cannot compare if obj is not a ChessPosition or null
        if (obj == null || obj.getClass() != getClass()){
            return false;
        }
        ChessPosition objPosition = (ChessPosition) obj;
        return (row == objPosition.getRow() && col == objPosition.getColumn());
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "rank=" + row +
                ", file=" + col +
                '}';
    }
}
