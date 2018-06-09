package player;

import gameelements.GameField;
import gameelements.Point;
import gameelements.Ship;

public class HumanPlayer extends Player {


    public HumanPlayer(String text) {
        super(text);
    }

    @Override
    public Point makeShot() {
        return null;
    }

    @Override
    public GameField generateField() {

        GameField field = new GameField();

        field.placeShips(Ship.getGoodRandom());

        return field;
    }

}
