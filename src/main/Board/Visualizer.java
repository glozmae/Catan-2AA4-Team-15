package Board;

public interface Visualizer {
    /**
     * Creates the base map JSON based on current board state
     *
     */
    public void setupJSON();

    /**
     * Updates state JSON based on current board state
     *
     */
    public void updateJSON();

}
