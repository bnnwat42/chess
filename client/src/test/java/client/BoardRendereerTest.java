import chess.ChessBoard;
import chess.ChessGame;
import org.junit.jupiter.api.*;
import ui.BoardRenderer;

public class BoardRendereerTest {
    @Test
    void RenderBoard() throws InterruptedException {
        ChessGame chessGame = new ChessGame();
        BoardRenderer.renderBoard(chessGame, ChessGame.TeamColor.BLACK);
//        BoardRenderer.renderBoard(chessGame, ChessGame.TeamColor.WHITE);
    }
}
