package Game;

import Board.Board;
import Board.Observer;
import Board.Subject;

/**
 * ConsoleLogger is an observer that logs game events to the console.
 * Note: Since Task 3 does not require full implementation, this class is not fully implemented.
 * It purely exists for demonstration purposes.
 *
 * @author Yojith Sai Biradavolu, McMaster University
 */
public class ConsoleLogger implements Observer {
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

    /**
     * Sets the board that the logger is observing. Must be of type Board.
     *
     * @param subject the subject to observe
     */
    @Override
    public void setSubject(Subject subject) {
    }

    /**
     * Removes the board that the logger is observing.
     */
    @Override
    public void removeSubject() {
    }
}
