package Game;

import Player.Player;
import Resources.ResourceType;

import java.util.Random;

/**
 * Handles resource distribution.
 *
 * In a full Catan implementation, this would use the Board state and the dice roll.
 * For this runnable milestone build, we grant small resources so purchasing logic
 * and victory point progression can be tested without Board dependencies.
 */
public class ResourceDistributor {
    /** Grant a single random resource card to a player. */
    public void grantRandomResource(Player player, Random rng) {
        if (player == null) throw new IllegalArgumentException("player cannot be null");
        if (rng == null) throw new IllegalArgumentException("rng cannot be null");

        ResourceType[] types = ResourceType.values();
        ResourceType chosen = types[rng.nextInt(types.length)];
        player.addResource(chosen);
    }

    /** Deterministic mapping from a dice roll to a resource type. */
    public void grantFromRoll(Player player, int roll) {
        if (player == null) throw new IllegalArgumentException("player cannot be null");
        int idx = Math.floorMod(roll, ResourceType.values().length);
        player.addResource(ResourceType.values()[idx]);
    }
}