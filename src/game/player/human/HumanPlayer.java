package game.player.human;

import game.elements.GameField;
import game.elements.Point;
import game.elements.Ship;
import game.player.Player;

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

        field.placeShips(Ship.getRandomlyPlacedShips());

        return field;
    }


}
