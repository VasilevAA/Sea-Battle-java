package game.player;

import game.elements.GameField;
import game.elements.Point;
import game.elements.Ship;

import java.util.Random;

public class ComputerPlayer extends Player {

    public ComputerPlayer(String name) {
        super(name);
    }


    @Override
    public Point makeShot() {// TODO: 07.06.2018 make it just a bit smarter

        //lastShot = new Point(new Random().nextInt(10), new Random().nextInt(10));
        return analyzeLastTurn();
    }

    @Override
    public GameField generateField() {

        GameField field = new GameField();

        field.placeShips(Ship.getRandomlyPlacedShips());

        return field;
    }


    private Point lastShot;

    GameField playersField = new GameField();

    private Point lastLastShot = null;

    Ship currentShip = null;

    boolean onXLine = false;
    boolean onYLine = false;

    //for this methods lastLastShot is point for search to X or Y Line
    Point goXLine() {
        Point temp = null;
        if (playersField.getCell(lastShot) == GameField.CellStatus.SHIPSHOT) {
            lastLastShot = lastShot;
        }
        if (playersField.getCell(lastShot) != GameField.CellStatus.EMPTYSHOT) {
            for (int i = 0; i < 10; i++) {
                if (isCellCorrect(new Point(lastLastShot.getX() + i, lastLastShot.getY()))) {
                    temp = new Point(lastLastShot.getX() + i, lastLastShot.getY());
                    break;
                }
            }
        }
        if (temp != null) {
            return temp;
        }

        for (int i = 0; i < 10; i++) {
            if (isCellCorrect(new Point(lastLastShot.getX() - i, lastLastShot.getY()))) {
                temp = new Point(lastLastShot.getX() - i, lastLastShot.getY());
                break;
            }
        }
        if (temp != null) {
            return temp;
        }
        return null;
    }

    Point goYLine() {
        Point temp = null;
        if (playersField.getCell(lastShot) == GameField.CellStatus.SHIPSHOT) {
            lastLastShot = lastShot;
        }

        if (playersField.getCell(lastShot) != GameField.CellStatus.EMPTYSHOT) {
            for (int i = 0; i < 10; i++) {
                if (isCellCorrect(new Point(lastLastShot.getX(), lastLastShot.getY() + i))) {
                    temp = new Point(lastLastShot.getX(), lastLastShot.getY() + i);
                    break;
                }
            }
        }
        if (temp != null) {
            return temp;
        }
        for (int i = 0; i < 10; i++) {
            if (isCellCorrect(new Point(lastLastShot.getX(), lastLastShot.getY() - i))) {
                temp = new Point(lastLastShot.getX(), lastLastShot.getY() - i);
                break;
            }
        }
        if (temp != null) {
            return temp;
        }
        return null;
    }

    public Point analyzeLastTurn() {
        if (currentShip == null || currentShip.isSank()) { //if ship is sank, or we dont have one
            currentShip = null;
            onXLine = false;
            onYLine = false;
            lastLastShot = null;
            return new Point(new Random().nextInt(10), new Random().nextInt(10));
        }
        if (onYLine) {
            return goYLine();
        }
        if (onXLine) {
            return goXLine();
        }
        //stage one - find the right direction of ship
        if (lastLastShot != null &&
                playersField.getCell(lastLastShot) == GameField.CellStatus.SHIPSHOT &&
                playersField.getCell(lastShot) == GameField.CellStatus.SHIPSHOT
                && !lastShot.equals(lastLastShot)) {
            //значит dеверно угадали направление
            int dx = lastLastShot.getX() - lastShot.getX();
            int dy = lastLastShot.getY() - lastShot.getY();
            if (dx != 0) {
                onXLine = true;
                return goXLine();
            }
            if (dy != 0) {
                onYLine = true;
                return goYLine();

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

        //тут надо стрельнуть в четыре направления
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
        } while (!isCellCorrect(p));
        return p;

    }

    @Override
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

    private boolean isCellCorrect(Point p) {
        return p.getX() >= 0 && p.getX() <= 9
                && p.getY() >= 0 && p.getY() <= 9
                && playersField.getCell(p) == GameField.CellStatus.EMPTY;
    }


}
