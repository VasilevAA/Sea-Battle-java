package game.player.human;

import game.elements.GameField;
import game.elements.Point;
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

        //field.placeShips(Ship.getRandomlyPlacedShips());

        return new GameField();
    }


}
