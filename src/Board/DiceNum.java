package Board;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Number Token (Chit) on the board (2-12).
 * <p>
 * This class maps a specific dice roll number to the Tiles that 
 * produce resources when that number is rolled.
 */
public class DiceNum {

	/**
	 * The number on the token (e.g., 6, 8, 12).
	 */
	private int number;

	/**
	 * The list of Hexagonal Tiles associated with this number.
	 * (Usually 1 or 2 tiles per number in a standard game).
	 */
	private List<Tile> tiles;

	/**
	 * Constructor.
	 * @param number The dice number (2-12).
	 */
	public DiceNum(int number) {
		this.number = number;
		this.tiles = new ArrayList<>();
	}

	/**
	 * Associates a board Tile with this dice number.
	 * @param tile The tile to add.
	 */
	public void addTile(Tile tile) {
		if (tile != null) {
			this.tiles.add(tile);
		}
	}

	/**
	 * Gets the number associated with this token.
	 * @return The integer dice value.
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * Retrieves all tiles that produce resources for this number.
	 * @return A list of Tile objects.
	 */
	public List<Tile> getTiles() {
		return this.tiles;
	}
}