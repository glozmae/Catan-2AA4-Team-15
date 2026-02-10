package Board;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import GameResources.ResourceType;

/**
 * Represents the entire game map.
 *
 * This class is responsible for initializing the graph topology of the board.
 * It creates all 54 intersection Nodes first, then creates the 19 Hexagonal Tiles,
 * randomly assigns resources (including the Desert), and distributes the dice number tokens.
 *
 * @author Taihan Mobasshir, 400578506, McMaster University
 */
public class Board {

	/**
	 * Master list of all 54 intersections (vertices) on the board.
	 * These are shared between tiles.
	 */
	private List<Node> nodes;

	/**
	 * List of the 19 hexagonal terrain tiles.
	 */
	private List<Tile> tiles;

	/**
	 * List of DiceNum objects that map dice rolls (2-12) to specific tiles.
	 */
	private List<DiceNum> diceNumbers;

	/**
	 * Constructor: Builds the standard Catan board layout with RANDOMIZED resources.
	 */
	public Board() {
		// 1. Initialize Nodes
		nodes = new ArrayList<>();
		for (int i = 0; i <= 53; i++) {
			nodes.add(new Node(i));
		}

		// 2. Setup Resources (The Deck of Terrain Hexes)
		ArrayList<ResourceType> resourceDeck = new ArrayList<>();

		// Add the single Desert
		resourceDeck.add(ResourceType.DESERT);

		// Add 4 of each: Grain, Wool, Lumber
		for(int i = 0; i < 4; i++) {
			resourceDeck.add(ResourceType.GRAIN);
			resourceDeck.add(ResourceType.WOOL);
			resourceDeck.add(ResourceType.LUMBER);
		}

		// Add 3 of each: Ore, Brick
		for(int i = 0; i < 3; i++) {
			resourceDeck.add(ResourceType.ORE);
			resourceDeck.add(ResourceType.BRICK);
		}

		// CRITICAL: Randomize the resource locations!
		// This ensures the Desert and resources can appear anywhere.
		//Collections.shuffle(resourceDeck);

		// 3. Create Tiles and Map Topology
		tiles = new ArrayList<>();

		// --- CENTER TILE (ID: 0) ---
		tiles.add(createTile(0, n(0, 1, 2, 3, 4, 5), resourceDeck.get(0)));

		// --- INNER RING (ID: 1-6) ---
		tiles.add(createTile(1, n(6, 7, 8, 9, 2, 1), resourceDeck.get(1)));
		tiles.add(createTile(2, n(2, 9, 10, 11, 12, 3), resourceDeck.get(2)));
		tiles.add(createTile(3, n(4, 3, 12, 13, 14, 15), resourceDeck.get(3)));
		tiles.add(createTile(4, n(16, 5, 4, 15, 17, 18), resourceDeck.get(4)));
		tiles.add(createTile(5, n(19, 20, 0, 5, 16, 21), resourceDeck.get(5)));
		tiles.add(createTile(6, n(22, 23, 6, 1, 0, 20), resourceDeck.get(6)));

		// --- OUTER RING (ID: 7-18) ---
		tiles.add(createTile(7, n(24, 25, 26, 27, 8, 7), resourceDeck.get(7)));
		tiles.add(createTile(8, n(8, 27, 28, 29, 10, 9), resourceDeck.get(8)));
		tiles.add(createTile(9, n(10, 29, 30, 31, 32, 11), resourceDeck.get(9)));
		tiles.add(createTile(10, n(12, 11, 32, 33, 34, 13), resourceDeck.get(10)));
		tiles.add(createTile(11, n(14, 13, 34, 35, 36, 37), resourceDeck.get(11)));
		tiles.add(createTile(12, n(17, 15, 14, 37, 38, 39), resourceDeck.get(12)));
		tiles.add(createTile(13, n(40, 18, 17, 39, 41, 42), resourceDeck.get(13)));
		tiles.add(createTile(14, n(43, 21, 16, 18, 40, 44), resourceDeck.get(14)));
		tiles.add(createTile(15, n(47, 46, 19, 21, 43, 45), resourceDeck.get(15)));
		tiles.add(createTile(16, n(48, 49, 22, 20, 19, 46), resourceDeck.get(16)));
		tiles.add(createTile(17, n(50, 51, 52, 23, 22, 49), resourceDeck.get(17)));
		tiles.add(createTile(18, n(52, 53, 24, 7, 6, 23), resourceDeck.get(18)));

		// 4. Distribute Dice Numbers
		// This will automatically skip whichever tile is the Desert
		setupDiceNumbers();
	}

	/**
	 * Factory method to create the correct Tile class based on the resource type.
	 */
	private Tile createTile(int id, Node[] nodes, ResourceType type) {
		if (type == ResourceType.DESERT) {
			return new DesertTile(id, nodes);
		} else {
			return new Tile(id, nodes, type);
		}
	}

	/**
	 * Helper method to retrieve references to Node objects by their ID.
	 */
	private Node[] n(int... indices) {
		Node[] nodeArray = new Node[indices.length];
		for (int i = 0; i < indices.length; i++) {
			nodeArray[i] = nodes.get(indices[i]);
		}
		return nodeArray;
	}

	/**
	 * Assigns number tokens (2-12) to the tiles, skipping the Desert.
	 */
	private void setupDiceNumbers() {
		diceNumbers = new ArrayList<>();

		// Standard Catan number token distribution
		int[] standardTokens = {5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11};

		// Create buckets for each number (2-12)
		DiceNum[] lookup = new DiceNum[13];
		for(int i=2; i<=12; i++) {
			if(i!=7) lookup[i] = new DiceNum(i);
		}

		int tokenIndex = 0;

		// Iterate through all tiles (which are now randomized resources)
		for (Tile t : tiles) {
			// Check for Enum value DESERT
			if (t.getType() == ResourceType.DESERT) {
				continue; // Skip the desert (no number token)
			}

			if (tokenIndex < standardTokens.length) {
				int val = standardTokens[tokenIndex];
				if (lookup[val] != null) {
					lookup[val].addTile(t);
				}
				tokenIndex++;
			}
		}

		// Finalize the list
		for(int i=2; i<=12; i++) {
			if(lookup[i] != null) diceNumbers.add(lookup[i]);
		}
	}

	/**
	 * Returns the DiceNum object for a specific roll.
	 */
	public DiceNum getTilesForRoll(int roll) {
		for (DiceNum dn : diceNumbers) {
			if (dn.getNumber() == roll) return dn;
		}
		return null;
	}

	public List<Tile> getTiles() { return tiles; }
	public List<Node> getNodes() { return nodes; }
}