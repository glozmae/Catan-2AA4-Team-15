import java.util.*;

public class RandomAgent implements Agent{

    @override
    public String takeTurn(Player player, Random random){
        ArrayList<String> actions = new ArrayList<>();

        int total = player.totalCards();
        boolean mustSpend = total > 7;

        boolean canMakeRoad = player.enoughCards(ResourceType.BRICK, 1) && player.enoughCards(ResourceType.WOOD, 1);
        boolean canMakeSettlement = player.enoughCards(ResourceType.BRICK, 1) &&
                player.enoughCards(ResourceType.WOOD, 1) &&
                player.enoughCards(ResourceType.SHEEP, 1) &&
                player.enoughCards(ResourceType.WHEAT, 1);

        boolean canMakeCity = player.enoughCards(ResourceType.WHEAT, 2)&&
                player.enoughCards(ResourceType.ORE, 3);

        if (canMakeRoad){
            actions.add("Build a ROAD by spending 1 BRICK and 1 WOOD");
        }

        if (canMakeCity){
            actions.add("Build a CITY by spending 2 WHEAT and 3 ORE");
        }

        if (canMakeSettlement){
            actions.add("Build a SETTLEMENT by spending 1 BRICK, 1 WOOD, 1 SHEEP and 1 WHEAT");
        }

        //if it must spend but it cant do anything, return a message
        if (mustSpend && actions.size() == 0){
            return "has more that seven cards but cannot build anything";
        }

        //If not forced to spend, agent can choose to do nothing
        if (!mustSpend && actions.size() == 0){
            return "does nothing";
        }

        String actionToPreform = actions.get(random.nextInt(actions.size()));
        if (actionToPreform.startsWith("Build a ROAD")){
            player.spend(ResourceType.BRICK, 1);
            player.spend(ResourceType.WOOD, 1);
        }else if (actionToPreform.startsWith("Build a SETTLEMENT")){
            player.spend(ResourceType.BRICK, 1);
            player.spend(ResourceType.WOOD, 1);
            player.spend(ResourceType.WHEAT, 1);
            player.spend(ResourceType.SHEEP, 1);
        }else{
            player.spend(ResourceType.WHEAT, 2);
            player.spend(ResourceType.ORE, 3);
            player.addVictoryPoints(1);
        }

        return  actionToPreform;
    }

}