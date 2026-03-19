package Board;

import GameResources.Road;
import GameResources.Structure;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that creates the base map JSON and updates the state JSON for the
 * python visualizer
 *
 * @author Yojith Sai Biradavolu, McMaster University
 */
public class JSONVisualizer implements Visualizer {
    private final String BASE_MAP_JSON = "visualize/base_map.json";
    private final String STATE_JSON = "visualize/state.json";


    /**
     * Maps each tile from 0-18 to a qsr coordinate
     **/
    private static final int[][] TILE_COORDS = {
            {0, 0, 0}, {1, -1, 0}, {0, -1, 1}, {-1, 0, 1}, {-1, 1, 0},
            {0, 1, -1}, {1, 0, -1}, {2, -2, 0}, {1, -2, 1}, {0, -2, 2},
            {-1, -1, 2}, {-2, 0, 2}, {-2, 1, 1}, {-2, 2, 0}, {-1, 2, -1},
            {0, 2, -2}, {1, 1, -2}, {2, 0, -2}, {2, -1, -1},};

    private final ObjectMapper objectMapper;
    private Board board;

    public JSONVisualizer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    /**
     * Creates the base map JSON based on the board state
     */
    @Override
    public void setup() {
        List<Tile> tiles = board.getTiles();
        ObjectNode baseMap = objectMapper.createObjectNode();
        List<ObjectNode> tileNodes = new ArrayList<>();

        for (Tile tile : tiles) {
            ObjectNode tileNode = objectMapper.createObjectNode();
            int[] coordinates = TILE_COORDS[tile.getId()];
            String tileType = tile.getType().toString();
            tileType = tileType.equals("GRAIN") ? "WHEAT" : tileType;
            tileType = tileType.equals("WOOL") ? "SHEEP" : tileType;
            tileType = tileType.equals("LUMBER") ? "WOOD" : tileType;
            tileType = tileType.equals("DESERT") ? null : tileType;

            tileNode.put("q", coordinates[0]);
            tileNode.put("s", coordinates[1]);
            tileNode.put("r", coordinates[2]);
            tileNode.put("resource", tileType);

            tileNode.put("number", tile.getProductionNumber());
            tileNodes.add(tileNode);
        }
        baseMap.set("tiles", objectMapper.valueToTree(tileNodes));
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(BASE_MAP_JSON), baseMap);
        } catch (IOException e) {
            System.err.println("Error: Could not create base map JSON");
        }
    }

    /**
     * Updates state JSON based on current board state
     */
    @Override
    public void update() {
        List<Node> nodes = board.getNodes();
        ObjectNode state = objectMapper.createObjectNode();
        List<ObjectNode> roadNodes = new ArrayList<>();
        List<ObjectNode> buildingNodes = new ArrayList<>();

        for (Node node : nodes) {
            Structure structure = node.getStructure();
            if (structure != null) {
                String owner = structure.getOwner().getColor().toString();
                String type = structure.getClass().getSimpleName().toUpperCase();
                ObjectNode buildingNode = objectMapper.createObjectNode();
                buildingNode.put("node", node.getId());
                buildingNode.put("owner", owner);
                buildingNode.put("type", type);
                buildingNodes.add(buildingNode);
            }
            Road[] roads = {node.getLeftRoad(), node.getRightRoad(), node.getVertRoad()};
            Node[] destinations = {node.getLeft(), node.getRight(), node.getVert()};
            for (int i = 0; i < roads.length; i++) {
                if (roads[i] != null && destinations[i] != null) {
                    int destination = destinations[i].getId();
                    if (node.getId() < destination) {
                        String owner = roads[i].getOwner().getColor().toString();
                        ObjectNode roadNode = objectMapper.createObjectNode();
                        roadNode.put("a", (node.getId()));
                        roadNode.put("b", (destination));
                        roadNode.put("owner", owner);
                        roadNodes.add(roadNode);
                    }
                }
            }
        }
        state.set("roads", objectMapper.valueToTree(roadNodes));
        state.set("buildings", objectMapper.valueToTree(buildingNodes));
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(STATE_JSON), state);
        } catch (IOException e) {
            System.err.println("Error: Could not create state JSON");
        }
    }

    /**
     * Sets the subject that the observer is observing
     *
     * @param subject the subject to observe
     */
    @Override
    public void setSubject(Subject subject) {
        if (!(subject instanceof Board)) {
            throw new IllegalArgumentException("Subject must be of type Board");
        }
        this.board = (Board) subject;
    }
}
