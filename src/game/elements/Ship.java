package game.elements;

import game.elements.fields.GameField;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ship {

    private Point[] points;

    private Point[] pointsAround;

    private int numberOfAlreadyShot = 0;

    private int size;

    private int orientation;

    public int getOrientation() {
        return orientation;
    }

    public Ship(int size) {
        this.size = size;
        points = new Point[size];
    }

    public Point[] getPointsAround() {
        return pointsAround;
    }

    private void setPointsAround(Point[] pointsAround) {
        this.pointsAround = pointsAround;
    }

    private void setPoints(Point[] points) {
        this.points = points;
    }

    public Point[] getPoints() {
        return points;
    }

    public int getSize() {
        return size;
    }

    public boolean contains(Point p) {
        for (Point point : points) {
            if (p.equals(point)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSank() {
        return numberOfAlreadyShot == size;
    }

    public void hitShip() {
        numberOfAlreadyShot++;
    }


    private static Ship[] tempShips = new Ship[10];
    private static GameField.CellStatus[][] tempField = new GameField.CellStatus[10][10] ;


    public static Ship[] getRandomlyPlacedShips() {
        clearField();

        for (int i = 0, size = 4; i < 10; i++) {
            if (i == 1 || i == 3 || i == 6) {
                size--;
            }
            tempShips[i] = new Ship(size);
            placeShip(tempShips[i], size);
        }

        return tempShips.clone();
    }

    private static void placeShip(Ship ship, int size) {

        int shipDirection; //1 - right, 2 - bot, 3 - left, 4 - top
        Point firstPoint;
        do {
            firstPoint = new Point(new Random().nextInt(10), new Random().nextInt(10));
            shipDirection = getCorrectDirectionForShip(firstPoint, size,new Random().nextInt(4)+1);
        }
        while (shipDirection == 0);

        generatePointsForShip(firstPoint, ship, shipDirection);

    }

    public static void clearField(){
        for (int i = 0; i < tempField.length; i++) {
            for (int j = 0; j < tempField.length; j++) {
                tempField[i][j] = GameField.CellStatus.EMPTY;
            }
        }
    }


    private static int getCorrectDirectionForShip(Point firstPoint, int size, int direction) {
        boolean t = true;

        for (int i = 0; i < size; i++) {
            GameField.CellStatus status;
            switch (direction) {
                case 1:
                    if (firstPoint.getX() + size <= 10) {
                        status = tempField[firstPoint.getY()][firstPoint.getX() + i];
                        if (status != GameField.CellStatus.EMPTY) {
                            t = false;
                        }
                    } else {
                        t = false;
                    }
                    break;
                case 2:
                    if (firstPoint.getY() + size <= 10) {
                        status = tempField[firstPoint.getY() + i][firstPoint.getX()];
                        if (status != GameField.CellStatus.EMPTY) {
                            t = false;
                        }
                    } else {
                        t = false;
                    }
                    break;
                case 3:
                    if (firstPoint.getX() - size >= -1) {
                        status = tempField[firstPoint.getY()][firstPoint.getX() - i];
                        if (status != GameField.CellStatus.EMPTY) {
                            t = false;
                        }
                    } else {
                        t = false;
                    }
                    break;
                case 4:
                    if (firstPoint.getY() - size >= -1) {
                        status = tempField[firstPoint.getY() - i][firstPoint.getX()];
                        if (status != GameField.CellStatus.EMPTY) {
                            t = false;
                        }
                    } else {
                        t = false;
                    }
                    break;
            }
        }
        return t ? direction : 0;
    }

    public static void generatePointsForShip(Point firstPoint, Ship ship, int shipDirection) {

        Point[] shipPoints = new Point[ship.getSize()];
        Point[] pointsAroundShip;

        for (int i = 0; i < ship.getSize(); i++) {
            Point point = null;
            switch (shipDirection) {
                case 1:
                    point = new Point(firstPoint.getX() + i, firstPoint.getY());
                    break;
                case 2:
                    point = new Point(firstPoint.getX(), firstPoint.getY() + i);
                    break;
                case 3:
                    point = new Point(firstPoint.getX() - i, firstPoint.getY());
                    break;
                case 4:
                    point = new Point(firstPoint.getX(), firstPoint.getY() - i);
                    break;
            }
            assert point != null;
            tempField[point.getY()][point.getX()] = GameField.CellStatus.SHIP;
            shipPoints[i] = point;
        }

        pointsAroundShip = getPointsAroundShip(shipPoints);

        for (Point aPointsAroundShip : pointsAroundShip) {
            tempField[aPointsAroundShip.getY()][aPointsAroundShip.getX()] = GameField.CellStatus.EMPTYSHOT;
        }
        ship.setPoints(shipPoints);
        ship.setPointsAround(pointsAroundShip);
        ship.orientation = shipDirection;
    }

    private static Point[] getPointsAroundShip(Point[] points) {
        List<Point> p = new ArrayList<>();
        for (Point point : points) {
            p.addAll(pointsAroundPoint(point));
        }
        Point[] retPoints = new Point[p.size()];
        retPoints = p.toArray(retPoints);

        return retPoints;
    }

    private static List<Point> pointsAroundPoint(Point point) {
        List<Point> temp = new ArrayList<>();

        for (int i = point.getY() - 1; i <= point.getY() + 1; i++) {
            for (int j = point.getX() - 1; j <= point.getX() + 1; j++) {
                Point arP = new Point(j, i);
                if (isPointCorrect(arP)) {
                    temp.add(arP);
                }
            }
        }

        return temp;
    }


    private static boolean isPointCorrect(Point p) {
        return p.getX() >= 0 && p.getY() >= 0
                && p.getX() <= 9 && p.getY() <= 9
                && (tempField[p.getY()][p.getX()] != GameField.CellStatus.SHIP);
    }
}