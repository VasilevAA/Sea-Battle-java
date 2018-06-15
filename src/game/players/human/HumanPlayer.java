package game.players.human;

import game.elements.fields.GameField;
import game.elements.Point;
import game.players.Player;

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
