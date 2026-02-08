package player;

import java.util.List;
import java.util.ArrayList;


/**
 * Represents an abstract player. Can be a human or a computer.
 *
 * @author Yojith Sai Biradavolu, McMaster University
 * @version Winter, 2026
 */
public abstract class Player {
    /** Represents the total number of players that have been created **/
    private static int num_players = 0;

    /** The maximum number of players allowed in a game **/
    private static final int max_players = 4;

    /** The color of the player's structures **/
    private final PlayerColor color;

    /** Represents the numerical id of the player **/
    private int id;

    /** The player's resource cards **/
    private List<Card> resources;

    /** The player's development cards **/
    private List<SpecialCard> development_cards;

    /** The player's roads, settlements, and cities **/
    private List<Structure> structures;

    /**
     * Constructor for a player, whether it be a human or a computer.
     */
    public Player() {
        if (num_players >= max_players) {
            throw new IllegalStateException("Maximum number of players reached");
        }

        this.id = num_players;
        num_players += 1;
        this.color = PlayerColor.values()[this.id];
        this.resources = new ArrayList<>();
        this.development_cards = new ArrayList<>();
        this.structures = new ArrayList<>();
    }

    /**
     * Initiate the current player's turn
     *
     * @param game The current game
     */
    public abstract void takeTurn(Game game);

    /**
     * Setup of the player's structures at the beginning of the game
     *
     * @param game The current game
     */
    public abstract void setup(Game game);

    /**
     * Resets the number of players to zero. Used when restarting the game.
     */
    public static void resetNumPlayers() {
        num_players = 0;
    }

    /**
     * Adds a new card to the player's hand
     *
     * @param type New card type to be added
     */
    public void addResource(ResourceType type) {
        Card new_card;
        switch (type) {
            case BRICK:
                new_card = new BrickCard();
                break;
            case LUMBER:
                new_card = new LumberCard();
                break;
            case WOOL:
                new_card = new WoolCard();
                break;
            case GRAIN:
                new_card = new GrainCard();
                break;
            case ORE:
                new_card = new OreCard();
                break;
            default:
                throw new IllegalArgumentException("Invalid resource type");
        }
        resources.add(new_card);
    }

    /**
     * Returns the number of resource cards of a specific type the player has
     *
     * @param type Type of resource card
     * @return Number of resource cards of the specified type
     */
    public int numResource(ResourceType type) {
        int count = 0;
        for (Card card : resources) {
            if (card.getType() == type) {
                count++;
            }
        }
        return count;
    }

    /**
     * Removes a resource card from the player's hand
     *
     * @param type Type of resource card to be removed
     */
    public void removeResource(ResourceType type) {
        for (int i = 0; i < resources.size(); i++) {
            Card card = resources.get(i);
            if (card.getType() == type) {
                resources.remove(i);
                return;
            }
        }
        throw new IllegalArgumentException("Player does not have the specified resource card");
    }

    /**
     * Adds a structure to the player's list of structures
     *
     * @param structure Structure to be added
     */
    public void addStructure(Structure structure) {
        structures.add(structure);
    }

    /**
     * Adds a development card to the player's hand
     *
     * @param card Development card to be added
     */
    public void addDevelopmentCard(SpecialCard card) {
        development_cards.add(card);
    }

    /**
     * Returns the player's resource cards
     *
     * @return Player's resource cards
     */
    // public ArrayList<Card> getResources() {
    // return resources;
    // }

    /**
     * Returns the player's development cards
     *
     * @return List of Player's development cards
     */
    public List<SpecialCard> getDevelopmentCards() {
        return development_cards;
    }

    /**
     * Calculates and returns the player's total victory points
     *
     * @return Player's total victory points
     */
    public int calculateVictoryPoints() {
        int points = 0;
        points += getSettlements().size();
        points += getCities().size() * 2;
        return points;
    }

    /**
     * Returns the player's id
     *
     * @return Player id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the player's color based on their player id.
     *
     * @return Player's color
     */
    public PlayerColor getColor() {
        return this.color;
    }

    /**
     * Returns a list of the player's cities
     *
     * @return List of player's cities
     */
    public List<City> getCities() {
        List<City> cities = new ArrayList<>();
        for (Structure structure : structures) {
            if (structure instanceof City) {
                cities.add((City) structure);
            }
        }
        return cities;
    }

    /**
     * Returns a list of the player's settlements
     *
     * @return List of player's settlements
     */
    public List<Settlement> getSettlements() {
        List<Settlement> settlements = new ArrayList<>();
        for (Structure structure : structures) {
            if (structure instanceof Settlement) {
                settlements.add((Settlement) structure);
            }
        }
        return settlements;
    }

    /**
     * Returns a list of the player's roads
     *
     * @return List of player's roads
     */
    public List<Road> getRoads() {
        List<Road> roads = new ArrayList<>();
        for (Structure structure : structures) {
            if (structure instanceof Road) {
                roads.add((Road) structure);
            }
        }
        return roads;
    }

    /**
     * Returns a string representation of the player
     *
     * @return String representation of the player with their id and color
     */
    @Override
    public String toString() {
        return "Player " + this.id + ", color: " + getColor();
    }
}