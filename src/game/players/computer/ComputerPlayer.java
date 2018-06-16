package game.players.computer;

import game.elements.fields.GameField;
import game.elements.Point;
import game.elements.ships.Ship;
import game.elements.ships.ShipFactory;
import game.players.Player;

public class ComputerPlayer extends Player {

    private AIModule ai = new AIModule();


    public ComputerPlayer(String name) {
        super(name);
        getField().placeShips(ShipFactory.getRandomlyPlacedShips());
    }

    @Override
    public Point makeShot() {
        return ai.calculateCorrectShot();
    }

    @Override
    public void setInfoAboutLastShot(Point point, GameField.CellStatus shot, Ship ship, int maxSize) {

        ai.setInformationAboutLastShot(point, shot, ship, maxSize);
    }

}

