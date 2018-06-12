package game.player.computer;

import game.elements.GameField;
import game.elements.Point;
import game.elements.Ship;

import java.util.*;

class AIModule {
    private GameField playersField = new GameField();

    private Point lastShot;
    private Point saveOfSuccessfulLastShot = null;
    private Ship currentShip = null;

    private boolean onXLine = false; //what direction we going
    private boolean onYLine = false;

    private Point start = null;
    private Point end = null;

    private boolean weGoingTop = true;

    int maxSizedShip = 4;

    void setInformationAboutLastShot(Point p, GameField.CellStatus status, Ship ship, int maxSize) {
        lastShot = p;
        playersField.setCell(p, status);
        maxSizedShip = maxSize;


        if (currentShip == null) {
            currentShip = ship;
        }
        if (ship != null && ship.isSank()) {
            for (int i = 0; i < ship.getPointsAround().length; i++) {
                playersField.setCell(ship.getPointsAround()[i], GameField.CellStatus.EMPTYSHOT);
            }
        }
    }


    Point calculateCorrectShot() {
        if (currentShip == null || currentShip.isSank()) { //if ship is sank, or we dont have one
            currentShip = null;
            saveOfSuccessfulLastShot = null;
            start = null;
            end = null;
            onXLine = false;
            onYLine = false;
            weGoingTop = true;
//            System.out.println(maxSizedShip);

            Point[] possibleRet = getExpectablePoints();
            int e = new Random().nextInt(possibleRet.length);
            return possibleRet[e];

        }

        if (onYLine) {
            return verticalWay();
        } else if (onXLine) {
            return horizontalWay();
        }

        if (saveOfSuccessfulLastShot != null && //determine the orientation of the ship (horizontal or vertical)
                playersField.getCell(saveOfSuccessfulLastShot) == GameField.CellStatus.SHIPSHOT &&
                playersField.getCell(lastShot) == GameField.CellStatus.SHIPSHOT
                && !lastShot.equals(saveOfSuccessfulLastShot)) {

            int dx = lastShot.getX() - saveOfSuccessfulLastShot.getX();
            if (dx > 0) {
                end = lastShot;
                start = saveOfSuccessfulLastShot;
                onXLine = true;
                return horizontalWay();
            } else if (dx < 0) {
                end = saveOfSuccessfulLastShot;
                start = lastShot;
                onXLine = true;
                return horizontalWay();
            }

            int dy = lastShot.getY() - saveOfSuccessfulLastShot.getY();
            if (dy > 0) {
                end = lastShot;
                start = saveOfSuccessfulLastShot;
                onYLine = true;
                return verticalWay();
            } else if (dy < 0) {
                end = saveOfSuccessfulLastShot;
                start = lastShot;
                onYLine = true;
                return verticalWay();
            }
        }

        if (playersField.getCell(lastShot) == GameField.CellStatus.SHIPSHOT) {//ship is shot
            saveOfSuccessfulLastShot = lastShot;
        }

        if (saveOfSuccessfulLastShot != null &&
                playersField.getCell(saveOfSuccessfulLastShot) == GameField.CellStatus.SHIPSHOT &&
                playersField.getCell(lastShot) == GameField.CellStatus.EMPTYSHOT) {
            //our guess was wrong
            lastShot = saveOfSuccessfulLastShot;
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

    //for this methods saveOfSuccessfulLastShot is point for search to X or Y Line
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

    private boolean isCellEmpty(Point p) {
        return p != null && p.getX() >= 0 && p.getX() <= 9
                && p.getY() >= 0 && p.getY() <= 9
                && playersField.getCell(p) == GameField.CellStatus.EMPTY;
    }


    private Point[] getExpectablePoints() {

        Point[][] tempField = new Point[10 ][10];
        for (int i = 0; i < tempField.length; i++) {
            for (int j = 0; j < tempField.length; j++) {
                tempField[i][j] = new Point(j,i);

            }
        }

        HashSet<Point> ret = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (playersField.getCell(new Point(j, i)) == GameField.CellStatus.EMPTY) {
                    ret.addAll(pointsForGivenPoint(new Point(j, i),tempField));
                }
            }
        }

        Point[] retTrue = new Point[ret.size()];

        retTrue = ret.toArray(retTrue);



        return retTrue;

    }

    private HashSet<Point> pointsForGivenPoint(Point p, Point[][] tempField) {




        HashSet<Point> ret = new HashSet<>();


        boolean isRight = true;
        for (int i = 0; i < maxSizedShip; i++) {
            if(!isCellEmpty(new Point(p.getX() + i, p.getY()))){
                isRight = false;
            }
        }

        if(isRight){
            for (int i = 0; i < maxSizedShip; i++) {
                ret.add(tempField[p.getY()][p.getX()+i]);
            }
        }

        isRight = true;
        for (int i = 0; i < maxSizedShip; i++) {
            if(!isCellEmpty(new Point(p.getX(), p.getY()+i))){
                isRight = false;
            }
        }

        if(isRight){
            for (int i = 0; i < maxSizedShip; i++) {
                ret.add(tempField[p.getY()+i][p.getX()]);
            }
        }

        return ret;
    }


}
