package Board;

/**
 * Interface for all visualizers of the board.
 * Can be of any form, such as a dynamic GUI or a JSON file
 *
 * @author Yojith Sai Biradavolu, McMaster University
 */
public interface Visualizer {
    /**
     * Creates the base map based on the current board state
     *
     */
    public void setup();

    /**
     * Updates the visualizer based on the current board state
     */
    public void update();
}
