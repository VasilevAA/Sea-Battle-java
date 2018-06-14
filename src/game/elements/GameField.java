package game.elements;

import java.util.ArrayList;

public class GameField {

    public enum CellStatus {EMPTYSHOT, EMPTY, SHIPSHOT, SHIP}

    private CellStatus[][] cells = new CellStatus[10][10];

    private Ship[] ships = null;

    public GameField() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                cells[i][j] = CellStatus.EMPTY;

            }
        }
    }

    public Ship[] getShips() {
        return ships;
    }

    public CellStatus[][] getCells() {
        return cells;
    }

    public CellStatus getCell(Point p) {
        return cells[p.getY()][p.getX()];
    }

    public void setCell(Point p, CellStatus st) {

        cells[p.getY()][p.getX()] = st;
    }

    public Ship getShip(Point point) {
        for (Ship ship : ships) {
            if (ship.contains(point)) {
                return ship;
            }
        }
        return null;
    }

    public void placeShips(Ship[] nships) {
        ships = nships;
        for (Ship ship : ships) {
            for (int j = 0; j < ship.getPoints().length; j++) {
                setCell(ship.getPoints()[j], CellStatus.SHIP);
            }
        }
    }

    private ArrayList<Ship> tempShips = new ArrayList<>();

    public void placeShip(Ship ship){
        for (int j = 0; j < ship.getPoints().length; j++) {
            setCell(ship.getPoints()[j], CellStatus.SHIP);
        }

        for (int i = 0; i < ship.getPointsAround().length ; i++) {
            setCell(ship.getPointsAround()[i],CellStatus.EMPTYSHOT);
        }

        tempShips.add(ship);

        ships = new Ship[tempShips.size()];

        ships = tempShips.toArray(ships);
    }

    private void removeShip(Ship ship){
        if(ship == null){
            return;
        }

        for (int j = 0; j < ship.getPoints().length; j++) {
            setCell(ship.getPoints()[j], CellStatus.EMPTY);
        }
        for (int i = 0; i < ship.getPointsAround().length ; i++) {
            setCell(ship.getPointsAround()[i],CellStatus.EMPTY);
        }


        tempShips.remove(ship);

        ships = new Ship[tempShips.size()];

        ships = tempShips.toArray(ships);
    }

    public void removeShip(Point point){
        removeShip(getShip(point));
    }

    public boolean isPlaceCorrectForShip(Point firstPoint, int size, int direction){
        boolean t = true;

        for (int i = 0; i < size; i++) {
            CellStatus status;
            switch (direction) {
                case 1:
                    if (firstPoint.getX() + size <= 10) {
                        status = cells[firstPoint.getY()][firstPoint.getX() + i];
                        if (status != CellStatus.EMPTY) {
                            t = false;
                        }
                    } else {
                        t = false;
                    }
                    break;
                case 2:
                    if (firstPoint.getY() + size <= 10) {
                        status = cells[firstPoint.getY() + i][firstPoint.getX()];
                        if (status != CellStatus.EMPTY) {
                            t = false;
                        }
                    } else {
                        t = false;
                    }
                    break;
                case 3:
                    if (firstPoint.getX() - size >= -1) {
                        status = cells[firstPoint.getY()][firstPoint.getX() - i];
                        if (status != CellStatus.EMPTY) {
                            t = false;
                        }
                    } else {
                        t = false;
                    }
                    break;
                case 4:
                    if (firstPoint.getY() - size >= -1) {
                        status = cells[firstPoint.getY() - i][firstPoint.getX()];
                        if (status != CellStatus.EMPTY) {
                            t = false;
                        }
                    } else {
                        t = false;
                    }
                    break;
            }
        }
        return t;
    }

}