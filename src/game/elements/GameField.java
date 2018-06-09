package game.elements;

public class GameField {

    public enum CellStatus {EMPTYSHOT, EMPTY, SHIPSHOT, SHIP}

    private CellStatus[][] cells = new CellStatus[10][10];

    private Ship[] ships;

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

    public void placeShips(Ship[] nships){
        ships = nships;
        for (Ship ship : ships) {
            for (int j = 0; j < ship.getPoints().length; j++) {
                setCell(ship.getPoints()[j], CellStatus.SHIP);

            }

        }
    }

}



