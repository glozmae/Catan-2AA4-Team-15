package Player;

import java.util.List;
import java.util.Scanner;

import Board.Node;
import Game.Game;
import GameResources.City;
import GameResources.Cost;
import GameResources.ResourceType;
import GameResources.Road;
import GameResources.Settlement;
import GameResources.Structure;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements the human player.
 *
 * @author Parnia Yazdinia, 400567795, McMaster University
 */
public class HumanPlayer extends Player {

    private static final Pattern ROLL_PATTERN = Pattern.compile("^roll$", Pattern.CASE_INSENSITIVE);

    private static final Pattern GO_PATTERN = Pattern.compile("^go$", Pattern.CASE_INSENSITIVE);

    private static final Pattern  LIST_PATTERN = Pattern.compile("^list$", Pattern.CASE_INSENSITIVE);

    private static final Pattern  BUILD_SETTLEMENT = Pattern.compile("^build\\s+settlement\\s+(\\d+)$", Pattern.CASE_INSENSITIVE);

    private static final Pattern  BUILD_CITY = Pattern.compile("^build\\s+city\\s+(\\d+)$", Pattern.CASE_INSENSITIVE);

    private static final Pattern  BUILD_ROAD = Pattern.compile("^build\\s+road\\s+\\[?(\\d+)\\s*,\\s*(\\d+)\\]?$", Pattern.CASE_INSENSITIVE);

    /** Reads commands from the console for this human player. */
    private final Scanner commandReader;

    /**
     * Constructor for a human player.
     */
    public HumanPlayer() {
        super();
        this.commandReader = new Scanner(System.in);
    }

    /**
     * Runs the human player's turn.
     *
     * @param game current game
     */
    @Override
    public void takeTurn(Game game) {
        boolean hasRolledThisTurn = false;
        boolean turnFinished = false;

        System.out.println();
        System.out.println("Human Player " + (getId() + 1) + " turn.");
        System.out.println(
                "Commands: roll | list | build settlement <nodeId> | build city <nodeId> | build road <fromNodeId,toNodeId> | go");

        while (!turnFinished) {
            System.out.print("> ");
            String command = commandReader.nextLine().trim();

            if (command.isEmpty()) {
                System.out.println("Enter a command.");
                continue;
            }
            if (rollCommand(command)) {
                if (hasRolledThisTurn) {
                    System.out.println("You already rolled this turn.");
                } else {
                    boolean rolled = game.executeHumanRoll();
                    if (rolled) {
                        hasRolledThisTurn = true;
                    }
                }
                continue;
            }

            if (listCommand(command)) {
                printHand();
                continue;
            }

            if (goCommand(command)) {
                if (!hasRolledThisTurn) {
                    System.out.println("You must enter 'roll' before ending your turn.");
                } else {
                    turnFinished = true;
                    System.out.println("Turn ended.");
                }
                continue;
            }

            if (!hasRolledThisTurn) {
                System.out.println("You must enter 'roll' before building.");
                continue;
            }

            Integer settlementNodeId = parseSettlementNodeId(command);
            if (settlementNodeId != null) {
                buildSettlement(game, settlementNodeId);
                continue;
            }

            Integer cityNodeId = parseCityNodeId(command);
            if (cityNodeId != null) {
                buildCity(game, cityNodeId);
                continue;
            }

            int[] roadNodeIds = parseRoadNodeIds(command);
            if (roadNodeIds != null) {
                buildRoad(game, roadNodeIds[0], roadNodeIds[1]);
                continue;
            }

            System.out.println("Unknown command.");
        }
    }



    @Override
    public void setup(Game game) {
        // Setup is handled elsewhere for now.
    }

    /**
     * Prints the current resource counts in the player's hand.
     */
    private void printHand() {
        System.out.println("Hand: BRICK=" + getResourceAmount(ResourceType.BRICK)
                + " | LUMBER=" + getResourceAmount(ResourceType.LUMBER)
                + " | WOOL=" + getResourceAmount(ResourceType.WOOL)
                + " | GRAIN=" + getResourceAmount(ResourceType.GRAIN)
                + " | ORE=" + getResourceAmount(ResourceType.ORE));
    }

    /**
     * Builds a settlement on the given node.
     *
     * @param game game being played
     * @param nodeId target node id
     */
    private void buildSettlement(Game game, int nodeId) {
        Node node = findNode(game, nodeId);

        if (node == null) {
            System.out.println("Invalid node id entered.");
            return;
        }

        if (getSettlements().size() >= Settlement.getMax()) {
            System.out.println("You have already built the maximum number of settlements.");
            return;
        }

        if (!node.canBuildSettlement(this)) {
            System.out.println("You cannot build a settlement there.");
            return;
        }

        Settlement settlement = new Settlement();
        Cost cost = settlement.getCost();

        if (!affordable(cost)) {
            System.out.println("Not enough resources to build a settlement.");
            return;
        }

        payCost(cost);
        node.setPlayer(this);
        node.setStructure(settlement);
        addStructure(settlement);

        System.out.println("Built settlement at node " + nodeId + ".");
    }

    /**
     * Upgrades one of the player's settlements to a city.
     *
     * @param game game being played
     * @param nodeId target node id
     */
    private void buildCity(Game game, int nodeId) {
        Node node = findNode(game, nodeId);

        if (node == null) {
            System.out.println("Invalid node id entered.");
            return;
        }

        if (getCities().size() >= City.getMax()) {
            System.out.println("You have already built the maximum number of cities.");
            return;
        }

        Structure currentStructure = node.getStructure();
        if (node.getPlayer() != this || !(currentStructure instanceof Settlement)) {
            System.out.println("You can only upgrade one of your own settlements.");
            return;
        }

        City city = new City();
        Cost cost = city.getCost();

        if (!affordable(cost)) {
            System.out.println("Not enough resources to build a city.");
            return;
        }

        payCost(cost);
        removeStructure(currentStructure);
        node.setStructure(city);
        addStructure(city);

        System.out.println("Built city at node " + nodeId + ".");
    }

    /**
     * Builds a road between two nodes.
     *
     * @param game game being played
     * @param fromNodeId start node id
     * @param toNodeId end node id
     */
    private void buildRoad(Game game, int fromNodeId, int toNodeId) {
        if (fromNodeId == toNodeId) {
            System.out.println("A road must have two different node ids.");
            return;
        }

        Node beginNode = findNode(game, fromNodeId);
        Node endNode = findNode(game, toNodeId);

        if (beginNode == null || endNode == null) {
            System.out.println("Invalid node id.");
            return;
        }

        if (getRoads().size() >= Road.getMax()) {
            System.out.println("You have already built the maximum number of roads.");
            return;
        }

        boolean canBuildFromStart = beginNode.getBuildableRoadNeighbors(this).contains(endNode);
        boolean canBuildFromEnd = endNode.getBuildableRoadNeighbors(this).contains(beginNode);
        boolean canBuild = canBuildFromStart || canBuildFromEnd;

        if (!canBuild) {
            System.out.println("You cannot build a road on that edge.");
            return;
        }

        Road road = new Road();
        Cost cost = road.getCost();

        if (!affordable(cost)) {
            System.out.println("Not enough resources to build a road.");
            return;
        }

        payCost(cost);

        if (beginNode.getLeft() == endNode) {
            beginNode.setLeftRoad(road);
            endNode.setRightRoad(road);
        } else if (beginNode.getRight() == endNode) {
            beginNode.setRightRoad(road);
            endNode.setLeftRoad(road);
        } else if (beginNode.getVert() == endNode) {
            beginNode.setVertRoad(road);
            endNode.setVertRoad(road);
        }

        addRoad(road);
        System.out.println("Built road from node " + fromNodeId + " to node " + toNodeId + ".");
    }

    /**
     * Checks if the player can afford a cost.
     *
     * @param cost required cost
     * @return true if the player has enough resources, otherwise false
     */
    private boolean affordable(Cost cost) {
        return getResourceAmount(ResourceType.BRICK) >= cost.getBrick()
                && getResourceAmount(ResourceType.LUMBER) >= cost.getLumber()
                && getResourceAmount(ResourceType.WOOL) >= cost.getWool()
                && getResourceAmount(ResourceType.GRAIN) >= cost.getGrain()
                && getResourceAmount(ResourceType.ORE) >= cost.getOre();
    }

    /**
     * Removes the required resources from the player's hand.
     *
     * @param cost cost to pay
     */
    private void payCost(Cost cost) {
        removeResourceAmount(ResourceType.BRICK, cost.getBrick());
        removeResourceAmount(ResourceType.LUMBER, cost.getLumber());
        removeResourceAmount(ResourceType.WOOL, cost.getWool());
        removeResourceAmount(ResourceType.GRAIN, cost.getGrain());
        removeResourceAmount(ResourceType.ORE, cost.getOre());
    }

    /**
     * Removes a resource a specific number of times.
     *
     * @param type resource type
     * @param amount number to remove
     */
    private void removeResourceAmount(ResourceType type, int amount) {
        for (int i = 0; i < amount; i++) {
            removeResource(type);
        }
    }

    /**
     * Returns the node with the given id.
     *
     * @param game current game
     * @param nodeId node id
     * @return the matching node or null
     */
    private Node findNode(Game game, int nodeId) {
        List<Node> nodes = game.getBoard().getNodes();

        if (nodeId < 0 || nodeId >= nodes.size()) {
            return null;
        }

        return nodes.get(nodeId);
    }


    private boolean rollCommand(String command) {
        return ROLL_PATTERN.matcher(command).matches();
    }

    private boolean goCommand(String command) {
        return GO_PATTERN.matcher(command).matches();
    }

    private boolean listCommand(String command) {
        return LIST_PATTERN.matcher(command).matches();
    }

    private Integer parseSettlementNodeId(String command) {
        Matcher matcher = BUILD_SETTLEMENT.matcher(command);
        if (!matcher.matches()) {
            return null;
        }
        return Integer.parseInt(matcher.group(1));
    }

    private Integer parseCityNodeId(String command) {
        Matcher matcher = BUILD_CITY.matcher(command);
        if (!matcher.matches()) {
            return null;
        }
        return Integer.parseInt(matcher.group(1));
    }

    private int[] parseRoadNodeIds(String command) {
        Matcher matcher = BUILD_ROAD.matcher(command);
        if (!matcher.matches()) {
            return null;
        }

        int fromNodeId = Integer.parseInt(matcher.group(1));
        int toNodeId = Integer.parseInt(matcher.group(2));
        return new int[] { fromNodeId, toNodeId };
    }
}