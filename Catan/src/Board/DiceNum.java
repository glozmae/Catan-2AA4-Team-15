package Board;

import java.util.List;

public class DiceNum {

    private int number;
    private List<Tile> tiles;

    public DiceNum(int number, List<Tile> tiles) {
        this.number = number;
        this.tiles = tiles;
    }

    public List<Tile> getTiles() {
        return tiles;
    }
}
