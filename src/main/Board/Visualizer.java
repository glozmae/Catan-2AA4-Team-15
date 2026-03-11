package Board;

import GameResources.ResourceType;
import GameResources.Road;
import GameResources.Structure;
import Player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that creates the base map JSON and updates the state JSON for the python visualizer
 *
 * @author Yojith Sai Biradavolu, McMaster University
 */
public class Visualizer {
    private static final String BASE_MAP_JSON = "visualize/base_map.json";
    private static final String STATE_JSON = "visualize/state.json";

    /**
     * Maps each tile from 0-18 to a qsr coordinate
     **/
    private static final int[][] TILE_COORDS = {
            {0, 0, 0},
            {0, -1, 1},
            {-1, 0, 1},
            {-1, 1, 0},
            {0, 1, -1},
            {1, 0, -1},
            {1, -1, 0},
            {0, -2, 2},
            {-1, -1, 2},
            {-2, 0, 2},
            {-2, 1, 1},
            {-2, 2, 0},
            {-1, 2, -1},
            {0, 2, -2},
            {1, 1, -2},
            {2, 0, -2},
            {2, -1, -1},
            {2, -2, 0},
            {1, -2, 1}
    };

    /**
     * Creates the base map JSON based on provided tiles
     *
     * @param board Board from which to generate JSON
     */
    public static void setupJSON(Board board) {
        List<Tile> tiles = board.getTiles();
        List<DiceNum> diceNumbers = board.getDiceNumbers();
        ObjectMapper objectMapper = new ObjectMapper();
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

            for (DiceNum dn : diceNumbers) {
                for (Tile t : dn.getTiles()) {
                    if (t.getId() == tile.getId()) {
                        tileNode.put("number", dn.getNumber());
                        break;
                    }
                }
            }
            tileNodes.add(tileNode);
        }
        baseMap.set("tiles", objectMapper.valueToTree(tileNodes));
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(BASE_MAP_JSON), baseMap);
        } catch (IOException e) {
            System.err.println("Error: Could not create base map JSON");
            e.printStackTrace();
        }
    }

    public static void updateJSON(Board board) {
        List<Node> nodes = board.getNodes();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode state = objectMapper.createObjectNode();
        List<ObjectNode> roadNodes = new ArrayList<>();
        List<ObjectNode> buildingNodes = new ArrayList<>();

        for (Node node : nodes) {
            Structure structure = node.getStructure();
            if (structure != null) {
                String owner = structure.getOwner().getColor().toString();
                String type = structure.getClass().getSimpleName().toUpperCase();
                ObjectNode buildingNode = objectMapper.createObjectNode();
                buildingNode.put("node", fixNodeIndex(node.getId()));
                buildingNode.put("owner", owner);
                buildingNode.put("type", type);
                buildingNodes.add(buildingNode);
            }
            Road[] roads = {node.getLeftRoad(), node.getRightRoad(), node.getVertRoad()};
            Node[] destinations = {node.getLeft(), node.getRight(), node.getVert()};
            for (int i = 0; i < roads.length; i++) {
                if (roads[i] != null) {
                    int destination = destinations[i].getId();
                    String owner = roads[i].getOwner().getColor().toString();
                    ObjectNode roadNode = objectMapper.createObjectNode();
                    roadNode.put("a", fixNodeIndex(node.getId()));
                    roadNode.put("b", fixNodeIndex(destination));
                    roadNode.put("owner", owner);
                    roadNodes.add(roadNode);
                }
            }
        }
        state.set("roads", objectMapper.valueToTree(roadNodes));
        state.set("buildings", objectMapper.valueToTree(buildingNodes));
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(STATE_JSON), state);
        } catch (IOException e) {
            System.err.println("Error: Could not create state JSON");
            e.printStackTrace();
        }
    }

    /**
     * The visualizer has node ID mapping differently from the Board class.
     * Using a simple array, the mapping is corrected.
     *
     * @param nodeId Initial id of the node
     * @return Corrected id of the node
     */
    private static int fixNodeIndex(int nodeId) {
        int[] MAP = {
                5, 0, 1, 2, 3, 4, 6, 7, 8, 9, 10, 11, 12, 15, 14, 14, 18, 16, 17, 21, 19, 20, 22, 23, 24, 25, 26,
                27, 28, 29, 30, 31, 32, 33, 34, 37, 35, 36, 38, 39, 40, 42, 41, 44, 43, 45, 47, 46, 48, 49, 50, 51, 52, 53
        };

        return MAP[nodeId];
    }
}
