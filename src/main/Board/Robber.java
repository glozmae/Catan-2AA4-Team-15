package Board;

/**
 * Robber class.
 * Represents the robber entity that occupies a specific tile on the board.
 * Allows tracking and updating the robber's current location during the game.
 *
 * @author [Your Name], [Your Student Number], McMaster University
 */
public class Robber {

    /**
     * The tile currently occupied by the robber.
     */
    private Tile tile;

    /**
     * Constructor for the Robber.
     * Initializes the robber's starting location on the board.
     *
     * @param tile the initial tile the robber is placed on (usually the Desert)
     */
    public Robber (Tile tile) {
        this.tile = tile;
    }

    /**
     * Checks if the robber is currently on the specified tile.
     *
     * @param tile the tile to check against the robber's current location
     * @return true if the robber is on the given tile, false otherwise
     */
    public boolean hasRobber (Tile tile) {
        return this.tile == tile;
    }

    /**
     * Updates the robber's location to a new tile.
     *
     * @param tile the new tile the robber will occupy
     */
    public void newRobber (Tile tile) {
        this.tile = tile;
    }

}