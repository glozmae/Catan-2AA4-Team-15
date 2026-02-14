package GameResources;

import Player.Player;

/**
 * Concrete implementation of Bank interface to validate purchases and deduct approprite
 * resources from players when they purchase
 * 
 * @author Elizabeth Glozman, 400559660, McMaster University
 */
public class GameBank implements Bank {

    /**
     * Determine if a player can afford item
     * compare resources and cost for item
     * 
     * @param item - item to purchase
     * @param player - player wanting to buy item
     * @return true if player has enough resources
     */
    @Override
    public boolean canBuy(Purchasable item, Player player) {
        if (item == null || player == null) return false;

        Cost c = item.getCost();

        return player.getResourceAmount(ResourceType.BRICK) >= c.getBrick()
            && player.getResourceAmount(ResourceType.LUMBER) >= c.getLumber()
            && player.getResourceAmount(ResourceType.GRAIN) >= c.getGrain()
            && player.getResourceAmount(ResourceType.WOOL) >= c.getWool()
            && player.getResourceAmount(ResourceType.ORE) >= c.getOre();
    }

    /**
     * Executes purchase by deducting required resources from player
     * 
     * @param item - item being purchased
     * @param player - player making the purchase
     * @throws IllegalStateException if player doesn't have enough resources
     */
    @Override
    public void buy(Purchasable item, Player player) {
        if (!canBuy(item, player)) {
            throw new IllegalStateException("Player cannot afford: " + item.getClass().getSimpleName());
        }

        Cost c = item.getCost();

        for (int i = 0; i < c.getBrick(); i++) player.removeResource(ResourceType.BRICK);
        for (int i = 0; i < c.getLumber(); i++) player.removeResource(ResourceType.LUMBER);
        for (int i = 0; i < c.getGrain(); i++) player.removeResource(ResourceType.GRAIN);
        for (int i = 0; i < c.getWool(); i++) player.removeResource(ResourceType.WOOL);
        for (int i = 0; i < c.getOre(); i++) player.removeResource(ResourceType.ORE);
    }
}
