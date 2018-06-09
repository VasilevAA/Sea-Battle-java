package game.player;

import game.elements.GameField;
import game.elements.Point;
import game.elements.Ship;

public abstract class Player {

    private String name;

    private GameField field;

    GameField playersField = new GameField();

    Player(String name) {
        this.name = name;
        field = generateField();
    }

    public String name() {
        return name;
    }

    public abstract Point makeShot();

    public GameField.CellStatus getCellStatus(Point point) {
        return field.getCell(point);
    }

    public Ship setCellStatus(Point point, GameField.CellStatus status) {
        field.setCell(point, status);
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
        System.out.println("player " + name + " field:");
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

    public boolean hasAliveShips(){
        int count = 0;
        for (int i = 0; i < field.getCells().length; i++) {
            for (int j = 0; j < field.getCells()[i].length; j++) {
                if(field.getCells()[i][j] == GameField.CellStatus.SHIPSHOT){
                    count++;
                }
            }
        }
        return count != 20;
    }

    public int shipsAlive(){
        int ret = 10;
        for (int i = 0; i < field.getShips().length; i++) {
            if(field.getShips()[i].isSank()){
                ret--;
            }
        }
        return ret;
    }

    public void setInfoAboutLastShot(Point point, GameField.CellStatus shot, Ship ship){

        playersField.setCell(point, shot);
        if (ship != null && ship.isSank()) {
            for (int i = 0; i < ship.getPointsAround().length; i++) {
                playersField.setCell(ship.getPointsAround()[i], GameField.CellStatus.EMPTYSHOT);
            }
        }
    }
}

