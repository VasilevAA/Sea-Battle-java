package game;

import game.elements.fields.GameField;
import game.elements.Point;
import game.elements.ships.Ship;
import game.players.Player;

/**
 * Controller between gui and game elements
 */

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
            opponent.setCellStatus(point, GameField.CellStatus.EMPTY_SHOT);
        } else {
            opponent.setCellStatus(point, GameField.CellStatus.SHIP_SHOT);
        }
        player.setInfoAboutLastShot(point,opponent.getCellStatus(point),null,opponent.maxSizeOfAliveShips());
    }

    public Player getWinner() {
        if (player.allShipsDead())
            return opponent;
        if (opponent.allShipsDead())
            return player;
        return null;
    }

    public Point receiveShotFromOpponent() {

        Point point = opponent.makeShot();
        Ship tempShip;
        if (player.getCellStatus(point) == GameField.CellStatus.EMPTY) {
            tempShip = player.setCellStatus(point, GameField.CellStatus.EMPTY_SHOT);
        } else {
            tempShip = player.setCellStatus(point, GameField.CellStatus.SHIP_SHOT);
        }
        opponent.setInfoAboutLastShot(point, player.getCellStatus(point), tempShip,player.maxSizeOfAliveShips());

        return point;
    }

}
