package Board;

public interface Visualizer extends Observer{
    /**
     * Creates the base map based on current board state
     *
     */
    public void setup();
}
