package Board;

import java.util.List;
import java.util.Random;
import Resources.ResourceType;

public class Main {

    public static void main(String[] args) {
        System.out.println("------------------------------------------");
        System.out.println("      CATAN BOARD PROTOTYPE SIMULATION    ");
        System.out.println("------------------------------------------");

        // 1. Initialize the Board
        // This triggers the node creation, resource shuffling, and dice assignment
        Board board = new Board();

        // 2. Print the Board Layout
        // This confirms that the Desert is random and numbers are skipped correctly
        board.printBoard();

        // 3. Start the Simulation Loop
        runSimulation(board, 20); // Simulate 20 turns
    }

    /**
     * Simulates a series of dice rolls to test resource generation logic.
     */
    public static void runSimulation(Board board, int turns) {
        System.out.println("\n\n>>> STARTING DICE ROLL SIMULATION (" + turns + " Turns) <<<");

        Random rand = new Random();

        for (int i = 1; i <= turns; i++) {
            // Simulate 2 dice
            int d1 = rand.nextInt(6) + 1;
            int d2 = rand.nextInt(6) + 1;
            int totalRoll = d1 + d2;

            System.out.print("Turn " + i + ": Rolled [" + d1 + " + " + d2 + "] = " + totalRoll);

            // Handle Logic
            if (totalRoll == 7) {
                System.out.println(" -> ROBBER! (No resources produced)");
            } else {
                // Look up which tiles match this number
                DiceNum diceToken = board.getTilesForRoll(totalRoll);

                if (diceToken != null) {
                    List<Tile> activeTiles = diceToken.getTiles();
                    System.out.print(" -> Producing: ");

                    for (Tile t : activeTiles) {
                        // Print the resource and the Tile ID
                        System.out.print("[" + t.getType() + " from Tile " + t.getId() + "] ");
                    }
                    System.out.println();
                } else {
                    // This happens if a number like 2 or 12 is rolled
                    // but it wasn't assigned to any tile (rare, but possible in some setups)
                    System.out.println(" -> No tiles have this number.");
                }
            }
        }
    }
}