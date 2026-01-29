package Board;

import java.util.List;
import java.util.ArrayList;

public class Board {

    private List<Node> nodes;
    private List<Tile> tiles;

    public Board() {
        nodes = new ArrayList<>();
        for (int i = 0; i <= 53; i++) {
            nodes.add(new Node(i));
        }

        ArrayList<ResourceType> resourceTypes = new ArrayList<>();
        resourceTypes.add(ResourceType.DESERT);
        for(int i = 0; i < 4; i++) {
            resourceTypes.add(ResourceType.WHEAT);
            resourceTypes.add(ResourceType.SHEEP);
            resourceTypes.add(ResourceType.WOOD);
        }
        for(int i = 0; i < 3; i++) {
            resourceTypes.add(ResourceType.ORE);
            resourceTypes.add(ResourceType.BRICK);
        }
        //Collections.shuffle(resourceTypes);

        tiles = new ArrayList<>();

        // 0 -- CENTRAL TILE
        tiles.add(new Tile(0, n(0, 1, 2, 3, 4, 5), resourceTypes.get(0)));

        // 1-6 -- INNER RING TILES
        tiles.add(new Tile(1, n(6, 7, 8, 9, 2, 1), resourceTypes.get(1)));
        tiles.add(new Tile(2, n(2, 9, 10, 11, 12, 3), resourceTypes.get(2)));
        tiles.add(new Tile(3, n(4, 3, 12, 13, 14, 15), resourceTypes.get(3)));
        tiles.add(new Tile(4, n(16, 5, 4, 15, 17, 18), resourceTypes.get(4)));
        tiles.add(new Tile(5, n(19, 20, 0, 5, 16, 21), resourceTypes.get(5)));
        tiles.add(new Tile(6, n(22, 23, 6, 1, 0, 20), resourceTypes.get(6)));

        // 7-18 -- OUTER EDGE TILES
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

    private Node[] n(int... indices) {
        Node[] nodeArray = new Node[indices.length];
        for (int i = 0; i < indices.length; i++) {
            nodeArray[i] = nodes.get(indices[i]);
        }
        return nodeArray;
    }
}