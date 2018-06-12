package game.player.computer;

import game.elements.GameField;
import game.elements.Point;
import game.elements.Ship;
import game.player.Player;

public class ComputerPlayer extends Player {

    private AIModule ai = new AIModule();


    public ComputerPlayer(String name) {
        super(name);
    }

    @Override
    public Point makeShot() {

        return ai.calculateCorrectShot();
    }

    @Override
    public GameField generateField() {

        GameField field = new GameField();
        field.placeShips(Ship.getRandomlyPlacedShips());

        return field;
    }


    public void setInfoAboutLastShot(Point point, GameField.CellStatus shot, Ship ship,int maxSize) {

        ai.setInformationAboutLastShot(point, shot, ship,maxSize);
    }

}

