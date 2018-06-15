package game.players;

import game.elements.fields.GameField;
import game.elements.Point;
import game.elements.Ship;

public abstract class Player {

    private String name;

    private GameField field;

    public Player(String name) {
        this.name = name;
        field = generateField();
    }

    public String name() {
        return name;
    }

    public abstract Point makeShot();

    public GameField.CellStatus getCellStatus(Point point) {
        return field.getCellStatus(point);
    }

    public GameField getField() {
        return field;
    }

    public Ship setCellStatus(Point point, GameField.CellStatus status) {
        field.setCellStatus(point, status);
        if (status == GameField.CellStatus.SHIPSHOT) {
            Ship temp = field.getShip(point);
            temp.hitShip();

            if (temp.isSank()) {
                Point[] p = temp.getPointsAround();
                for (Point aP : p) {
                    setCellStatus(aP, GameField.CellStatus.EMPTYSHOT);
                }
            }
            return temp;
        }
        return null;

    }

    public abstract GameField generateField();

    public void printField() {
        System.out.println("players " + name + " field:");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                char c = (getCellStatus(new Point(j, i)) == GameField.CellStatus.EMPTYSHOT) ? 'o' :
                        getCellStatus(new Point(j, i)) == GameField.CellStatus.SHIP ? '#' :
                                getCellStatus(new Point(j, i)) == GameField.CellStatus.SHIPSHOT ? 'X' : '_';
                System.out.print(Character.toString(c) + ' ');
            }
            System.out.println();
        }

        System.out.println();
    }

    public boolean allShipsDead() {
        for (Ship sh : field.getShips()) {
            if (!sh.isSank()) {
                return false;
            }
        }
        return true;
    }

    public int shipsAlive() {
        int ret = 10;
        for (int i = 0; i < field.getShips().length; i++) {
            if (field.getShips()[i].isSank()) {
                ret--;
            }
        }
        return ret;
    }

    public void setInfoAboutLastShot(Point point, GameField.CellStatus shot, Ship ship, int maxSize) {
    }

    public int maxSizeOfAlivesShips() {
        int max = 1;

        for (int i = 0; i < field.getShips().length; i++) {
            if (!field.getShips()[i].isSank() && field.getShips()[i].getSize() > max) {
                max = field.getShips()[i].getSize();
            }
        }
        System.out.println(max);

        return max;
    }
}

