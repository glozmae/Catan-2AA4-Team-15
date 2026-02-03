package player;

import java.util.*;
public class RandomAgent implements Agent{

    @Override
    public String takeTurn(Player player, Random random){

        int total = totalCards(player);
        boolean mustSpend = total > 7;

        List<String> actions = new ArrayList<>();

        boolean canMakeRoad = player.numResource(ResourceType.BRICK) >= 1 && player.numResource(ResourceType.LUMBER) >= 1;

        boolean canMakeSettlement =
                player.numResource(ResourceType.BRICK) >= 1 &&
                player.numResource(ResourceType.LUMBER) >= 1 &&
                player.numResource(ResourceType.WOOL) >= 1 &&
                player.numResource(ResourceType.GRAIN) >= 1;

        boolean canMakeCity = player.numResource(ResourceType.GRAIN) >= 2 &&
                player.numResource(ResourceType.ORE) >= 3;

        if (canMakeRoad){
            actions.add("Build a ROAD by spending 1 BRICK and 1 LUMBER");
        }

        if (canMakeCity){
            actions.add("Build a CITY by spending 2 GRAIN and 3 ORE");
        }

        if (canMakeSettlement){
            actions.add("Build a SETTLEMENT by spending 1 BRICK, 1 LUMBER, 1 WOOL and 1 GRAIN");
        }

        //If not forced to spend, agent can choose to do nothing
        if (!mustSpend && actions.size() == 0){
            return "does nothing";
        }

        //if not forced to spend, do nothing
        if (!mustSpend){
            actions.add("does nothing");
        }

        String actionToPreform = actions.get(random.nextInt(actions.size()));

        if (actionToPreform.startsWith("Build a ROAD")){
            player.removeResource(ResourceType.BRICK);
            player.removeResource(ResourceType.LUMBER);
            player.addStructure(new Road(player, -1, -1));
        }else if (actionToPreform.startsWith("Build a SETTLEMENT")){
            player.removeResource(ResourceType.BRICK);
            player.removeResource(ResourceType.LUMBER);
            player.removeResource(ResourceType.WOOL);
            player.removeResource(ResourceType.GRAIN);
            player.addStructure(new Settlement(player, -1));

        }else if (actionToPreform.startsWith("Build a CITY")){
            player.removeResource(ResourceType.GRAIN);
            player.removeResource(ResourceType.GRAIN);
            player.removeResource(ResourceType.ORE);
            player.removeResource(ResourceType.ORE);
            player.removeResource(ResourceType.ORE);
            player.addStructure(new City(player, -1));

        }

        return  actionToPreform;
    }

    private int totalCards(Player player){
        int sum = 0;
        for (ResourceType t : ResourceType.values()){
            sum += player.numResource(t);
        }
        return sum;
    }

}