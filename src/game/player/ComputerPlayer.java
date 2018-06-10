package game.player;

import game.elements.GameField;
import game.elements.Point;
import game.elements.Ship;

import java.util.Random;

public class ComputerPlayer extends Player {

    private Point lastShot;
    private Point lastLastShot = null;
    private Ship currentShip = null;

    private boolean onXLine = false;
    private boolean onYLine = false;

    private Point start = null;
    private Point end = null;

    private boolean weGoingTop = true;

    public ComputerPlayer(String name) {
        super(name);
    }

    @Override
    public GameField generateField() {

        GameField field = new GameField();

        field.placeShips(Ship.getRandomlyPlacedShips());

        return field;
    }

    @Override
    public Point makeShot() {
        if (currentShip == null || currentShip.isSank()) { //if ship is sank, or we dont have one
            currentShip = null;
            lastLastShot = null;
            start = null;
            end = null;
            onXLine = false;
            onYLine = false;
            weGoingTop = true;
            return new Point(new Random().nextInt(10), new Random().nextInt(10));
        }

        if (onYLine) {
            return verticalWay();
        }
        if (onXLine) {
            return horizontalWay();
        }

        if (lastLastShot != null && //determine the orientation of the ship (horizontal or vertical)
                playersField.getCell(lastLastShot) == GameField.CellStatus.SHIPSHOT &&
                playersField.getCell(lastShot) == GameField.CellStatus.SHIPSHOT
                && !lastShot.equals(lastLastShot)) {

            int dx = lastShot.getX() - lastLastShot.getX();
            if (dx > 0) {
                end = lastShot;
                start = lastLastShot;
                onXLine = true;
                return horizontalWay();
            } else if (dx < 0) {
                end = lastLastShot;
                start = lastShot;
                onXLine = true;
                return horizontalWay();
            }

            int dy = lastShot.getY() - lastLastShot.getY();
            if (dy > 0) {
                end = lastShot;
                start = lastLastShot;
                onYLine = true;
                return verticalWay();
            } else if (dy < 0) {
                end = lastLastShot;
                start = lastShot;
                onYLine = true;
                return verticalWay();
            }
        }

        if (playersField.getCell(lastShot) == GameField.CellStatus.SHIPSHOT) {
            lastLastShot = lastShot;
        }
        if (lastLastShot != null &&
                playersField.getCell(lastLastShot) == GameField.CellStatus.SHIPSHOT &&
                playersField.getCell(lastShot) == GameField.CellStatus.EMPTYSHOT) {
            //значит неверно угадали вернуться назад
            lastShot = lastLastShot;
        }

        return getStartingDirection();
    }

    private Point getStartingDirection() {
        Point p = null;
        do {
            int randomMove = new Random().nextInt(4);
            switch (randomMove) {
                case 0:
                    p = new Point(lastShot.getX() + 1, lastShot.getY());
                    break;
                case 1:
                    p = new Point(lastShot.getX(), lastShot.getY() + 1);
                    break;
                case 2:
                    p = new Point(lastShot.getX() - 1, lastShot.getY());
                    break;
                case 3:
                    p = new Point(lastShot.getX(), lastShot.getY() - 1);
            }
        } while (!isCellEmpty(p));

        return p;
    }

    //for this methods lastLastShot is point for search to X or Y Line
    private Point horizontalWay() {
        if (playersField.getCell(lastShot) == GameField.CellStatus.EMPTYSHOT) {
            weGoingTop = false;
        }

        if (isCellEmpty(new Point(end.getX() + 1, end.getY())) && weGoingTop) {
            end = new Point(end.getX() + 1, end.getY());
            return end;
        } else {
            weGoingTop = false;
        }

        if (isCellEmpty(new Point(start.getX() - 1, start.getY()))) {
            start = new Point(start.getX() - 1, start.getY());
            return start;
        }
        return null;
    }

    private Point verticalWay() {
        if (playersField.getCell(lastShot) == GameField.CellStatus.EMPTYSHOT) {
            weGoingTop = false;
        }

        if (isCellEmpty(new Point(end.getX(), end.getY() + 1)) && weGoingTop) {
            end = new Point(end.getX(), end.getY() + 1);
            return end;
        } else {
            weGoingTop = false;
        }

        if (isCellEmpty(new Point(start.getX(), start.getY() - 1))) {
            start = new Point(start.getX(), start.getY() - 1);
            return start;
        }
        return null;
    }

    public void setInfoAboutLastShot(Point point, GameField.CellStatus shot, Ship ship) {
        lastShot = point;
        if (currentShip == null) {
            this.currentShip = ship;
        }
        playersField.setCell(point, shot);
        if (ship != null && ship.isSank()) {
            for (int i = 0; i < ship.getPointsAround().length; i++) {
                playersField.setCell(ship.getPointsAround()[i], GameField.CellStatus.EMPTYSHOT);
            }
        }
    }

    private boolean isCellEmpty(Point p) {
        return p != null && p.getX() >= 0 && p.getX() <= 9
                && p.getY() >= 0 && p.getY() <= 9
                && playersField.getCell(p) == GameField.CellStatus.EMPTY;
    }
}
