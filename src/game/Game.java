package game;

import game.elements.GameField;
import game.elements.Point;
import game.elements.Ship;
import game.player.Player;

public class Game {

    private Player player; // It's you

    private Player opponent; // your opponent (comp or real human)

    public Game(Player player, Player opponent) {
        this.player = player;
        this.opponent = opponent;

    }

    public Player getPlayer() {
        return player;
    }

    public Player getOpponent() {
        return opponent;
    }

    public void sendShotToOpponent(Point point) {

        if (opponent.getCellStatus(point) == GameField.CellStatus.EMPTY) {
            opponent.setCellStatus(point, GameField.CellStatus.EMPTYSHOT);
        } else {
            opponent.setCellStatus(point, GameField.CellStatus.SHIPSHOT);
        }
        player.setInfoAboutLastShot(point,opponent.getCellStatus(point),null,opponent.maxSizeOfAlivesShips());

        opponent.printField();

    }

    public Player getWinner() {
        if (player.allShipsDead())
            return opponent;
        if (opponent.allShipsDead())
            return player;
        return null;
    }

    // TODO: 07.06.2018 maybe we need hierarchy of game classes: human game and computer
    public Point receiveShotFromOpponent() {

        Point point = opponent.makeShot();
        Ship tempShip;
        if (player.getCellStatus(point) == GameField.CellStatus.EMPTY) {
            tempShip = player.setCellStatus(point, GameField.CellStatus.EMPTYSHOT);
        } else {
            tempShip = player.setCellStatus(point, GameField.CellStatus.SHIPSHOT);
        }
        opponent.setInfoAboutLastShot(point, player.getCellStatus(point), tempShip,player.maxSizeOfAlivesShips());

        player.printField();
        return point;
    }

}
