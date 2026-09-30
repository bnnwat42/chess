package chess;
import java.util.ArrayList;
import java.util.Collection;


public class CastlingMove{
    private final ChessPosition[] startPositions;
    private final ChessPosition[] endPositions;

    CastlingMove(ChessPosition[] startPositions, ChessPosition[] endPositions){
        this.startPositions = startPositions;
        this.endPositions = endPositions;
    }

    public ChessPosition[] getStartPositions() {
        return startPositions;
    }

    public ChessPosition[] getEndPositions() {
        return endPositions;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
