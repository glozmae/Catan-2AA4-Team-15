package Game;

import Board.Board;
import Board.Observer;
import Board.Subject;

/**
 * ConsoleLogger is an observer that logs game events to the console.
 * Note: Since Task 3 does not require full implementation, this class is not fully implemented.
 * It purely exists for demonstration purposes.
 */
public class ConsoleLogger implements Observer {
    private Board board;

    @Override
    public void update() {
        // Logs board state
    }

    /**
     * Sets the subject that the observer is observing
     *
     * @param subject the subject to observe
     */
    @Override
    public void setSubject(Subject subject) {
        if (!(subject instanceof Board)) {
            throw new IllegalArgumentException("Subject must be of type Board");
        }
        this.board = (Board) subject;
    }
}
