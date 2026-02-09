package Board;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections; // Added for the shuffle method reference
import Resources.ResourceType;

/**
 * Represents the entire game map.
 * <p>
 * This class is responsible for initializing the graph topology of the board.
 * It creates all 54 intersection Nodes first, then creates the 19 Hexagonal Tiles
 * and "stitches" them together by assigning the appropriate Nodes to each Tile.
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
	 * Constructor: Builds the standard Catan board layout.
	 */
	public Board() {
		// 1. Initialize Nodes
		// Standard Catan board has roughly 54 intersections (vertices).
		nodes = new ArrayList<>();
		for (int i = 0; i <= 53; i++) {
			nodes.add(new Node(i));
		}

		// 2. Setup Resources (The Deck of Terrain Hexes)
		// This creates the standard distribution of resources found in the base game.
		ArrayList<ResourceType> resourceTypes = new ArrayList<>();

		// The Desert (produces nothing)
		resourceTypes.add(null);

		// Add 4 of each: Grain (Wheat), Wool (Sheep), Lumber (Wood)
		for(int i = 0; i < 4; i++) {
			resourceTypes.add(ResourceType.GRAIN);
			resourceTypes.add(ResourceType.WOOL);
			resourceTypes.add(ResourceType.LUMBER);
		}

		// Add 3 of each: Ore, Brick
		for(int i = 0; i < 3; i++) {
			resourceTypes.add(ResourceType.ORE);
			resourceTypes.add(ResourceType.BRICK);
		}

		// RANDOMIZATION NOTE:
		// Uncommenting the line below would randomize the board layout every game.
		// Currently, the board is generated with a fixed resource layout.
		// Collections.shuffle(resourceTypes);

		// 3. Create Tiles and Map Topology
		tiles = new ArrayList<>();

		// --- TILE MAPPING ---
		// The numbers passed to n(...) represent indices in the 'nodes' list.
		// When two tiles list the same number, they are physically connected at that corner.

		// CENTER: The single tile in the very middle of the board
		// Uses resource index 0 (Desert)
		tiles.add(new Tile(0, n(0, 1, 2, 3, 4, 5), resourceTypes.get(0)));

		// INNER RING: The 6 tiles surrounding the center
		// Note how they share nodes with the center tile (e.g., Tile 1 shares nodes 1 and 2 with Tile 0)
		tiles.add(new Tile(1, n(6, 7, 8, 9, 2, 1), resourceTypes.get(1)));
		tiles.add(new Tile(2, n(2, 9, 10, 11, 12, 3), resourceTypes.get(2)));
		tiles.add(new Tile(3, n(4, 3, 12, 13, 14, 15), resourceTypes.get(3)));
		tiles.add(new Tile(4, n(16, 5, 4, 15, 17, 18), resourceTypes.get(4)));
		tiles.add(new Tile(5, n(19, 20, 0, 5, 16, 21), resourceTypes.get(5)));
		tiles.add(new Tile(6, n(22, 23, 6, 1, 0, 20), resourceTypes.get(6)));

		// OUTER RING: The 12 tiles forming the coast/exterior
		tiles.add(new Tile(7, n(24, 25, 26, 27, 8, 7), resourceTypes.get(7)));
		tiles.add(new Tile(8, n(8, 27, 28, 29, 10, 9), resourceTypes.get(8)));
		tiles.add(new Tile(9, n(10, 29, 30, 31, 32, 11), resourceTypes.get(9)));
		tiles.add(new Tile(10, n(12, 11, 32, 33, 34, 13), resourceTypes.get(10)));
		tiles.add(new Tile(11, n(14, 13, 34, 35, 36, 37), resourceTypes.get(11)));
		tiles.add(new Tile(12, n(17, 15, 14, 37, 38, 39), resourceTypes.get(12)));
		tiles.add(new Tile(13, n(40, 18, 17, 39, 41, 42), resourceTypes.get(13)));
		tiles.add(new Tile(14, n(43, 21, 16, 18, 40, 44), resourceTypes.get(14)));
		tiles.add(new Tile(15, n(47, 46, 19, 21, 43, 45), resourceTypes.get(15)));
		tiles.add(new Tile(16, n(48, 49, 22, 20, 19, 46), resourceTypes.get(16)));
		tiles.add(new Tile(17, n(50, 51, 52, 23, 22, 49), resourceTypes.get(17)));
		tiles.add(new Tile(18, n(52, 53, 24, 7, 6, 23), resourceTypes.get(18)));
	}

	/**
	 * Helper method to retrieve references to Node objects by their ID.
	 * <p>
	 * This ensures that when different tiles ask for "Node 5", they both receive
	 * a reference to the SAME object in memory, not two copies.
	 * * @param indices A variable list of integer IDs representing the node corners.
	 * @return An array of Node objects corresponding to those IDs.
	 */
	private Node[] n(int... indices) {
		Node[] nodeArray = new Node[indices.length];
		for (int i = 0; i < indices.length; i++) {
			// Retrieve the existing node instance from the master list
			nodeArray[i] = nodes.get(indices[i]);
		}
		return nodeArray;
	}
}