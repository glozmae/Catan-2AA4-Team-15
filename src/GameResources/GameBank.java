package GameResources;

import Player.Player;

public class GameBank implements Bank {

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
