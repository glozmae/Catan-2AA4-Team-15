package Board;

public interface Visualizer {
    /**
     * Creates the base map based on current board state
     *
     */
    public void setup();

    /**
     * Updates state based on current board state
     *
     */
    public void update();

}
