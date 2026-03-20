package Game;

import Board.Board;
import Board.Observer;

/**
 * ConsoleLogger is an observer that logs game events to the console.
 * Note: Since Task 3 does not require full implementation, this class is not fully implemented.
 * It purely exists for demonstration purposes.
 *
 * @author Yojith Sai Biradavolu, McMaster University
 */
public class ConsoleLogger extends Observer {
    /**
     * The board that this observer is observing
     */
    private Board board;

    /**
     * Updates the console logger to show information about the current board state.
     *
     */
    @Override
    public void update() {
        // Logs board state
    }
}
