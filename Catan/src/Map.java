import java.util.ArrayList;
import java.util.Collections;

public class Map {

    private ArrayList<Node> vertexes;
    private ArrayList<Tile> hexagons;

    public Map() {

        vertexes = new ArrayList<>();
        for (int i = 0; i <= 53; i++) {
            vertexes.add(new Node(i));
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
        Collections.shuffle(resourceTypes);


        hexagons = new ArrayList<>();

        // 0 -- CENTRAL TILE
        hexagons.add(new Tile(0, vertexes.get(0), vertexes.get(1), vertexes.get(2), vertexes.get(3), vertexes.get(4), vertexes.get(5), resourceTypes.get(0)));

        // 1-6 -- INNER RING TILES
        hexagons.add(new Tile(1, vertexes.get(6), vertexes.get(7), vertexes.get(8), vertexes.get(9), vertexes.get(2), vertexes.get(1), resourceTypes.get(1)));
        hexagons.add(new Tile(2, vertexes.get(2), vertexes.get(9), vertexes.get(10), vertexes.get(11), vertexes.get(12), vertexes.get(3), resourceTypes.get(2)));
        hexagons.add(new Tile(3, vertexes.get(4), vertexes.get(3), vertexes.get(12), vertexes.get(13), vertexes.get(14), vertexes.get(15), resourceTypes.get(3)));
        hexagons.add(new Tile(4, vertexes.get(16), vertexes.get(5), vertexes.get(4), vertexes.get(15), vertexes.get(17), vertexes.get(18), resourceTypes.get(4)));
        hexagons.add(new Tile(5, vertexes.get(19), vertexes.get(20), vertexes.get(0), vertexes.get(5), vertexes.get(16), vertexes.get(21), resourceTypes.get(5)));
        hexagons.add(new Tile(6, vertexes.get(22), vertexes.get(23), vertexes.get(6), vertexes.get(1), vertexes.get(0), vertexes.get(20), resourceTypes.get(6)));

        // 7-18 -- OUTER EDGE TILES
        hexagons.add(new Tile(7, vertexes.get(24), vertexes.get(25), vertexes.get(26), vertexes.get(27), vertexes.get(8), vertexes.get(7), resourceTypes.get(7)));
        hexagons.add(new Tile(8, vertexes.get(8), vertexes.get(27), vertexes.get(28), vertexes.get(29), vertexes.get(10), vertexes.get(9), resourceTypes.get(8)));
        hexagons.add(new Tile(9, vertexes.get(10), vertexes.get(29), vertexes.get(30), vertexes.get(31), vertexes.get(32), vertexes.get(11), resourceTypes.get(9)));
        hexagons.add(new Tile(10, vertexes.get(12), vertexes.get(11), vertexes.get(32), vertexes.get(33), vertexes.get(34), vertexes.get(13), resourceTypes.get(10)));
        hexagons.add(new Tile(11, vertexes.get(14), vertexes.get(13), vertexes.get(34), vertexes.get(35), vertexes.get(36), vertexes.get(37), resourceTypes.get(11)));
        hexagons.add(new Tile(12, vertexes.get(17), vertexes.get(15), vertexes.get(14), vertexes.get(37), vertexes.get(38), vertexes.get(39), resourceTypes.get(12)));
        hexagons.add(new Tile(13, vertexes.get(40), vertexes.get(18), vertexes.get(17), vertexes.get(39), vertexes.get(41), vertexes.get(42), resourceTypes.get(13)));
        hexagons.add(new Tile(14, vertexes.get(43), vertexes.get(21), vertexes.get(16), vertexes.get(18), vertexes.get(40), vertexes.get(44), resourceTypes.get(14)));
        hexagons.add(new Tile(15, vertexes.get(47), vertexes.get(46), vertexes.get(19), vertexes.get(21), vertexes.get(43), vertexes.get(45), resourceTypes.get(15)));
        hexagons.add(new Tile(16, vertexes.get(48), vertexes.get(49), vertexes.get(22), vertexes.get(20), vertexes.get(19), vertexes.get(46), resourceTypes.get(16)));
        hexagons.add(new Tile(17, vertexes.get(50), vertexes.get(51), vertexes.get(52), vertexes.get(23), vertexes.get(22), vertexes.get(49), resourceTypes.get(17)));
        hexagons.add(new Tile(18, vertexes.get(52), vertexes.get(53), vertexes.get(24), vertexes.get(7), vertexes.get(6), vertexes.get(23), resourceTypes.get(18)));
    }
}
