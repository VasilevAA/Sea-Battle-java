package game.player;

import game.elements.GameField;
import game.elements.Point;
import game.elements.Ship;

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
