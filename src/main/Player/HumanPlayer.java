package Player;

import java.util.Scanner;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Board.Node;
import Board.Tile;
import Game.Game;
import GameResources.City;
import GameResources.Cost;
import GameResources.ResourceType;
import GameResources.Road;
import GameResources.Settlement;

/**
 * This class implements the human player.
 * Reads input from the command line and executes actions based on assignments specs.
 *
 * @author Taihan Mobasshir, 400578506, McMaster University
 * @autor Parnia Yazdinia, 400567795, McMaster University
 */
public class HumanPlayer extends Player {

    // Regex Patterns for parsing Human Commands
    private static final Pattern ROLL_PATTERN = Pattern.compile("^roll$", Pattern.CASE_INSENSITIVE);
    private static final Pattern GO_PATTERN = Pattern.compile("^go$", Pattern.CASE_INSENSITIVE);
    private static final Pattern LIST_PATTERN = Pattern.compile("^list$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BUILD_SETTLEMENT = Pattern.compile("^build\\s+settlement\\s+(\\d+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BUILD_CITY = Pattern.compile("^build\\s+city\\s+(\\d+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BUILD_ROAD = Pattern.compile("^build\\s+road\\s+\\[?\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]?$", Pattern.CASE_INSENSITIVE);
    private static final Pattern UNDO_PATTERN = Pattern.compile("^undo$", Pattern.CASE_INSENSITIVE);
    private static final Pattern REDO_PATTERN = Pattern.compile("^redo$", Pattern.CASE_INSENSITIVE);

    /** Reads commands from the console for this human player. */
    private final Scanner commandReader;
    private final Random randomizer;
    private final Deque<PlayerCommand> undoStack;
    private final Deque<PlayerCommand> redoStack;

    public HumanPlayer() {
        super();
        this.commandReader = new Scanner(System.in);
        this.randomizer = new Random();
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
    }

    /**
     * Requirement R2.1 & R2.4: Reads human input and waits for "go" to proceed.
     */
    @Override
    public void takeTurn(Game game) {
        boolean turnFinished = false;

        undoStack.clear();
        redoStack.clear();

        System.out.println("\n========================================");
        System.out.println("Your Turn: " + this.toString());
        System.out.println("Commands: roll | list | build settlement <id> | build city <id> | build road <id,id> | undo | redo | go");
        System.out.println("========================================");

        while (!turnFinished) {
            System.out.print("> ");
            String command = commandReader.nextLine().trim();

            if (command.isEmpty()) continue;

            // ROLL COMMAND
            if (ROLL_PATTERN.matcher(command).matches()) {
                System.out.println("The Game engine automatically rolls at the start of the turn. You rolled a " + game.getLastRoll() + ".");
                continue;
            }

            // LIST COMMAND
            if (LIST_PATTERN.matcher(command).matches()) {
                printHand();
                continue;
            }

            // UNDO COMMAND
            if (UNDO_PATTERN.matcher(command).matches()) {
                undoLastCommand(game);   //need to come back and fix this eror
                continue;
            }

            // REDO COMMAND
            if (REDO_PATTERN.matcher(command).matches()) {
                redoLastCommand(game);
                continue;
            }

            // GO COMMAND
            if (GO_PATTERN.matcher(command).matches()) {
                turnFinished = true;
                logAction(game.getRound(), "Ended turn (Go)");
                continue;
            }

            // BUILD COMMANDS
            if (tryBuildSettlement(game, command)) continue;
            if (tryBuildCity(game, command)) continue;
            if (tryBuildRoad(game, command)) continue;

            System.out.println("Unknown command or invalid syntax.");
        }
    }

    /**
     * Interactive Setup Phase for the human player.
     */
    @Override
    public void setup(Game game) {
        System.out.println("\n--- Setup Phase: " + this.toString() + " ---");

        for (int i = 0; i < 2; i++) {
            boolean validSettlement = false;
            int placedNodeId = -1;

            while (!validSettlement) {
                System.out.print("Enter Node ID for Setup Settlement " + (i + 1) + " > ");
                try {
                    int nodeId = Integer.parseInt(commandReader.nextLine().trim());
                    Node node = findNode(game, nodeId);

                    if (node != null && node.getPlayer() == null && !isNeighborOccupied(node)) {
                        Settlement s = new Settlement();
                        node.setStructure(s);
                        addStructure(s);
                        placedNodeId = nodeId;
                        validSettlement = true;
                        logAction(game.getRound(), "Setup Settlement at Node " + nodeId);
                    } else {
                        System.out.println("Invalid or occupied node. Check the Distance Rule!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer.");
                }
            }

            boolean validRoad = false;
            while (!validRoad) {
                System.out.print("Enter target Node ID to build a Road from node " + placedNodeId + " > ");
                try {
                    int targetNodeId = Integer.parseInt(commandReader.nextLine().trim());
                    Node startNode = findNode(game, placedNodeId);
                    Node endNode = findNode(game, targetNodeId);

                    if (endNode != null && isAdjacent(startNode, endNode)) {
                        Road r = new Road();
                        placeRoadEdge(startNode, endNode, r);
                        addRoad(r);
                        validRoad = true;
                        logAction(game.getRound(), "Setup Road to Node " + targetNodeId);
                    } else {
                        System.out.println("Invalid target. Must be adjacent to node " + placedNodeId + ".");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer.");
                }
            }
        }
    }

    /**
     * Requirement R2.5: Players lose cards if > 7.
     * Human interactively picks which cards to throw away.
     */
    @Override
    public void robberDiscard() {
        int cardsToDiscard = getHand().getCount() - 7;
        if (cardsToDiscard <= 0) return;

        System.out.println("\n!!! ROBBER STRIKES !!! You must discard " + cardsToDiscard + " cards.");

        while (getHand().getCount() > 7) {
            printHand();
            System.out.print("Type resource to discard (BRICK, LUMBER, WOOL, GRAIN, ORE) > ");
            String resStr = commandReader.nextLine().trim().toUpperCase();

            try {
                ResourceType type = ResourceType.valueOf(resStr);
                if (getResourceAmount(type) > 0) {
                    removeResource(type);
                    System.out.println("Discarded 1 " + type + ".");
                } else {
                    System.out.println("You don't have any " + type + " to discard!");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid resource type.");
            }
        }
    }

    /**
     * Requirement R2.5 Simplification: Robber placed on a RANDOM tile.
     */
    @Override
    public Tile setRobber(List<Tile> tiles) {
        int randomIndex = randomizer.nextInt(tiles.size());
        Tile randomTile = tiles.get(randomIndex);
        System.out.println(this.toString() + " randomly placed the Robber on Tile " + randomTile.getId());
        return randomTile;
    }

    // ==========================================
    // PARSING & ACTION HELPERS
    // ==========================================

    private boolean tryBuildSettlement(Game game, String command) {
        Matcher matcher = BUILD_SETTLEMENT.matcher(command);
        if (!matcher.matches()) return false;

        int nodeId = Integer.parseInt(matcher.group(1));
        Node node = findNode(game, nodeId);

        if (node == null || getSettlements().size() >= Settlement.getMax() || !node.canBuildSettlement(this)) {
            System.out.println("Cannot build settlement there (Invalid node, max reached, or distance rule).");
            return true;
        }

        Settlement s = new Settlement();
        if (!affordable(s.getCost())) {
            System.out.println("Not enough resources!");
            return true;
        }

        PlayerCommand buildSettlement = new BuildSettlementCommand(this, node);
        executeCommand(game, buildSettlement);
        return true;
    }

    private boolean tryBuildCity(Game game, String command) {
        Matcher matcher = BUILD_CITY.matcher(command);
        if (!matcher.matches()) return false;

        int nodeId = Integer.parseInt(matcher.group(1));
        Node node = findNode(game, nodeId);

        if (node == null || getCities().size() >= City.getMax() || node.getPlayer() != this || !(node.getStructure() instanceof Settlement)) {
            System.out.println("Cannot build city there (Must own a settlement there first).");
            return true;
        }

        //Need to ad city
        PlayerCommand buildCity = new BuildCityCommand(this, node);
        executeCommand(game, buildCity);
        return true;
    }

    private boolean tryBuildRoad(Game game, String command) {
        Matcher matcher = BUILD_ROAD.matcher(command);
        if (!matcher.matches()) return false;

        int fromId = Integer.parseInt(matcher.group(1));
        int toId = Integer.parseInt(matcher.group(2));
        Node beginNode = findNode(game, fromId);
        Node endNode = findNode(game, toId);

        if (beginNode == null || endNode == null || getRoads().size() >= Road.getMax() ||
                (!beginNode.getBuildableRoadNeighbors(this).contains(endNode) &&
                        !endNode.getBuildableRoadNeighbors(this).contains(beginNode))) {
            System.out.println("Cannot build road there (No connection or edge already taken).");
            return true;
        }

        Road r = new Road();
        if (!affordable(r.getCost())) {
            System.out.println("Not enough resources!");
            return true;
        }

        PlayerCommand buildRoad = new BuildRoadCommand(this, beginNode, endNode);
        executeCommand(game, buildRoad);
        return true;
    }

    // ==========================================
    // UTILITY HELPERS
    // ==========================================

    /** Required Output format: [TurnID] / [PlayerID]: [Action] */
    private void logAction(int round, String action) {
        System.out.println("[" + round + "] / [" + (getId() + 1) + "]: " + action);
    }

    private void printHand() {
        System.out.println("Hand: BRICK=" + getResourceAmount(ResourceType.BRICK)
                + " | LUMBER=" + getResourceAmount(ResourceType.LUMBER)
                + " | WOOL=" + getResourceAmount(ResourceType.WOOL)
                + " | GRAIN=" + getResourceAmount(ResourceType.GRAIN)
                + " | ORE=" + getResourceAmount(ResourceType.ORE));
    }

    private boolean affordable(Cost cost) {
        return getResourceAmount(ResourceType.BRICK) >= cost.getBrick()
                && getResourceAmount(ResourceType.LUMBER) >= cost.getLumber()
                && getResourceAmount(ResourceType.WOOL) >= cost.getWool()
                && getResourceAmount(ResourceType.GRAIN) >= cost.getGrain()
                && getResourceAmount(ResourceType.ORE) >= cost.getOre();
    }

    private Node findNode(Game game, int nodeId) {
        List<Node> nodes = game.getBoard().getNodes();
        if (nodeId < 0 || nodeId >= nodes.size()) return null;
        return nodes.get(nodeId);
    }

    private boolean isNeighborOccupied(Node n) {
        return (n.getLeft() != null && n.getLeft().getPlayer() != null) ||
                (n.getRight() != null && n.getRight().getPlayer() != null) ||
                (n.getVert() != null && n.getVert().getPlayer() != null);
    }

    private boolean isAdjacent(Node start, Node end) {
        return start.getLeft() == end || start.getRight() == end || start.getVert() == end;
    }

    private void placeRoadEdge(Node start, Node end, Road r) {
        if (start.getLeft() == end) { start.setLeftRoad(r); end.setRightRoad(r); }
        else if (start.getRight() == end) { start.setRightRoad(r); end.setLeftRoad(r); }
        else if (start.getVert() == end) { start.setVertRoad(r); end.setVertRoad(r); }
    }

    private void executeCommand(Game game, PlayerCommand command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
        logAction(game.getRound(), command.getExecuteMessage());
    }

    private void undoLastCommand(Game game) {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo.");
            return;
        }
        PlayerCommand command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        logAction(game.getRound(), command.getUndoMessage());
    }

    private void redoLastCommand(Game game) {
        if (redoStack.isEmpty()) {
            System.out.println("Nothing to redo.");
            return;
        }
        PlayerCommand command = redoStack.pop();
        command.execute();
        undoStack.push(command);
        logAction(game.getRound(), "Redid: " + command.getExecuteMessage());
    }

    //Parser helpers
    public static boolean undoCommand(String command) {
        return UNDO_PATTERN.matcher(command).matches();
    }

    public static boolean redoCommand(String command) {
        return REDO_PATTERN.matcher(command).matches();
    }

    // ==========================================
    // TASK 3.3: ISOLATED REGEX PARSERS (For Testing)
    // ==========================================

    public static boolean rollCommand(String command) {
        return ROLL_PATTERN.matcher(command).matches();
    }

    public static boolean goCommand(String command) {
        return GO_PATTERN.matcher(command).matches();
    }

    public static boolean listCommand(String command) {
        return LIST_PATTERN.matcher(command).matches();
    }

    public static Integer parseSettlementNodeId(String command) {
        Matcher matcher = BUILD_SETTLEMENT.matcher(command);
        if (matcher.matches()) return Integer.parseInt(matcher.group(1));
        return null;
    }

    public static Integer parseCityNodeId(String command) {
        Matcher matcher = BUILD_CITY.matcher(command);
        if (matcher.matches()) return Integer.parseInt(matcher.group(1));
        return null;
    }

    public static int[] parseRoadNodeIds(String command) {
        Matcher matcher = BUILD_ROAD.matcher(command);
        if (matcher.matches()) {
            return new int[]{ Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)) };
        }
        return null;
    }


}