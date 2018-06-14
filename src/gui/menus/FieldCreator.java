package gui.menus;

import game.elements.GameField;
import game.elements.Point;
import game.elements.Ship;
import game.player.Player;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Iterator;

class FieldCreator extends Stage {

    private GameField field;
    private Player player;
    private Button[][] playerField = new Button[10][10];

    private FlowPane pane = null;
    private GridPane grid = null;


    class ShipItem extends ImageView {
        private int direction;
        private int size;

        int getDirection() {
            return direction;
        }

        int getSize() {
            return size;
        }

        void setDirection(int direction) {
            this.direction = direction;
        }

        ShipItem(String name, int direction, int size) {
            super(name);
            this.direction = direction;
            this.size = size;

        }
    }

    FieldCreator(Player player) {
        field = new GameField();
        this.player = player;
        setResizable(false);
        initModality(Modality.APPLICATION_MODAL);
        getIcons().add(new Image("resources/img/cursor.png"));
        setTitle(player.name() + "'s field setup");


        HBox hb = new HBox();
        hb.getChildren().addAll(setUpPlayerField(), new Separator(Orientation.VERTICAL), setUpRightPart());

        Scene scene = new Scene(hb);
        scene.getStylesheets().add("resources/styles/modena.css");
        setScene(scene);

//        label that, ship placement was canceled (need in main menu)
        setOnCloseRequest(event -> player.getField().setCell(new Point(0, 0), GameField.CellStatus.SHIPSHOT));

    }

    private GridPane setUpPlayerField() {
        grid = new GridPane();
        grid.getStylesheets().add("resources/styles/PlayerField.css");
        Image ima = new Image("resources/img/sea.png");
        grid.setBackground(new Background(new BackgroundImage(ima, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                playerField[i][j] = makeCell(i, j);
                grid.add(playerField[i][j], j, i);
            }
        }

        return grid;
    }

    private Button makeCell(int i, int j) {
        Button cell = new Button("");

        setUpDroppingInCell(i, j, cell);
        setUpDragingInCell(cell);

        cell.setMinSize(28, 28);

        return cell;
    }

    private void setUpDragingInCell(Button cell) {
        cell.setOnDragOver((DragEvent event) -> {
            String id = event.getDragboard().getString();
            ShipItem shipItem = pane.lookup("#" + id) != null
                    ? (ShipItem) pane.lookup("#" + id)
                    : (ShipItem) grid.lookup("#" + id);

            int direction = shipItem.getDirection();
            int size = shipItem.getSize();

            int x = GridPane.getColumnIndex(cell);
            int y = GridPane.getRowIndex(cell);

            if (event.getDragboard().hasString() && field.isPlaceCorrectForShip(new Point(x, y), size, direction)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
    }

    private void setUpDroppingInCell(int i, int j, Button cell) {
        cell.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();

            String id = db.getString();

            ShipItem itemInPane = (ShipItem) pane.lookup("#" + id);
            ShipItem itemInGrid = (ShipItem) grid.lookup("#" + id);

            if (itemInPane != null) {
                int direction = itemInPane.getDirection();
                int size = itemInPane.getSize();

                Ship ship = new Ship(size);
                Ship.clearField();
                Ship.placeShipCorrectly(new Point(j, i), ship, direction);

                field.placeShip(ship);

                grid.add(itemInPane, j, i, direction == 1 ? size : 1, direction == 2 ? size : 1);
                pane.getChildren().remove(itemInPane);
            }

            if (itemInGrid != null) {
                int direction = itemInGrid.getDirection();
                int size = itemInGrid.getSize();

                int x = GridPane.getColumnIndex(itemInGrid);
                int y = GridPane.getRowIndex(itemInGrid);

                field.removeShip(new Point(x, y));

                Ship ship = new Ship(size);
                Ship.clearField();
                Ship.placeShipCorrectly(new Point(j, i), ship, direction);

                field.placeShip(ship);

                grid.getChildren().remove(itemInGrid);
                grid.add(itemInGrid, j, i, direction == 1 ? size : 1, direction == 2 ? size : 1);
            }

            updatePlayerField();

            event.setDropCompleted(true);
            event.consume();
        });
    }

    private VBox setUpRightPart() {
        VBox vb = new VBox();
        vb.setPrefWidth(400);

        pane = new FlowPane();
        pane.setMinHeight(230);
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);

        for (int i = 0, size = 4; i < 10; i++) {
            if (i == 1 || i == 3 || i == 6) {
                size--;
            }
            ShipItem imv = makeShipItem(i, size);
            pane.getChildren().add(imv);
        }

        pane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                String id = db.getString();

                ShipItem temp = (ShipItem) grid.lookup("#" + id);
                if (temp != null) {
                    int x = GridPane.getColumnIndex(temp);
                    int y = GridPane.getRowIndex(temp);

                    grid.getChildren().remove(temp);
                    field.removeShip(new Point(x, y));

                    pane.getChildren().add(temp);
                }
            }
            updatePlayerField();
            event.setDropCompleted(true);
            event.consume();
        });

        pane.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != pane &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        Button generateRandom = new Button("Random");
        generateRandom.setOnAction(event -> {
            clearPlayerField();
            field.placeShips(Ship.getRandomlyPlacedShips());
            updatePlayerField();
        });


        Button clear = new Button("Clear Field");
        clear.setOnAction(event -> {
            clearPlayerField();
            updatePlayerField();
        });

        Label lab = new Label();
        lab.setMinWidth(140);
        lab.setAlignment(Pos.CENTER_RIGHT);

        Button ok = new Button("Start game");
        ok.setOnAction(event -> {
            if (field.getShips() == null || field.getShips().length != 10) {
                lab.setText("Place all ships!");
                return;
            }

            player.getField().placeShips(field.getShips());
            close();
        });

        HBox hb = new HBox(generateRandom, clear, lab, ok);
        hb.setSpacing(10);
        hb.setPadding(new Insets(10));

        vb.getChildren().addAll(pane, new Separator(), hb);


        return vb;
    }

    private ShipItem makeShipItem(int index, int size) {
        ShipItem shipItem = new ShipItem("resources/img/ships/size" + size + "/full.png", 1, size);
        shipItem.setId(this.getClass().getSimpleName() + System.currentTimeMillis() + index);

        shipItem.setOnDragDetected(event -> {
            Dragboard db = shipItem.startDragAndDrop(TransferMode.MOVE);
            db.setDragView(shipItem.getImage(), 14, 14);

            ClipboardContent cont = new ClipboardContent();
            cont.putString(shipItem.getId());
            db.setContent(cont);
            event.consume();
        });

        shipItem.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                if(grid.lookup("#" + shipItem.getId()) != null){
                    int direction = shipItem.getDirection() ==1 ? 2 : 1 ;

                    int x = GridPane.getColumnIndex(shipItem);
                    int y = GridPane.getRowIndex(shipItem);
                    Ship retShip = field.getShip(new Point(x,y));
                    field.removeShip(new Point(x,y));
                    if(field.isPlaceCorrectForShip(new Point(x, y), size, direction)){
                        Ship ship = new Ship(size);
                        Ship.clearField();
                        Ship.placeShipCorrectly(new Point(x,y), ship, direction);
                        field.placeShip(ship);

                        grid.getChildren().remove(shipItem);

                        shipItem.setRotate(90);
                        Image nIm = shipItem.snapshot(new SnapshotParameters(), null);
                        shipItem.setRotate(0);
                        shipItem.setImage(nIm);
                        shipItem.setDirection(shipItem.getDirection() == 1 ? 2 : 1);

                        grid.add(shipItem, x, y, direction == 1 ? size : 1, direction == 2 ? size : 1);

                        updatePlayerField();

                    } else{
                        field.placeShip(retShip);
                    }

                } else{
                    shipItem.setRotate(90);
                    Image nIm = shipItem.snapshot(new SnapshotParameters(), null);
                    shipItem.setRotate(0);
                    shipItem.setImage(nIm);
                    shipItem.setDirection(shipItem.getDirection() == 1 ? 2 : 1);
                }
            }

            event.consume();
        });
        return shipItem;
    }

    private void clearPlayerField(){
        if(grid.getChildren() != null) {
            Iterator<Node> i  = grid.getChildren().iterator();
            while(i.hasNext()){
                Node n = i.next();

                if(n.getId() != null && n.getId().contains("FieldCreator")){
                    i.remove();
                    pane.getChildren().add(n);
                }
            }

        }
        field = new GameField();
    }

    private void updatePlayerField() {

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                field.setCell(new Point(j, i), GameField.CellStatus.EMPTY);
                playerField[i][j].setStyle("-fx-background-color: transparent;");

            }
        }

        if (field.getShips() != null) {
            for (Ship ship : field.getShips()) {
                for (int i = 0; i < ship.getPoints().length; i++) {
                    field.setCell(ship.getPoints()[i], GameField.CellStatus.SHIP);
                    playerField[ship.getPoints()[i].getY()][ship.getPoints()[i].getX()].setStyle("-fx-background-color: orange;");
                }
                for (int i = 0; i < ship.getPointsAround().length; i++) {
                    field.setCell(ship.getPointsAround()[i], GameField.CellStatus.EMPTYSHOT);

                }
            }
        }
    }


    void drawShips(){
        Ship[] ships = player.getField().getShips();

        for (Ship shep : ships) {
            int tempSize = shep.getSize();
            int orientation = shep.getOrientation();
            Point[] points = shep.getPoints();
            for (int k = 0; k < tempSize; k++) {
                ImageView vi = new ImageView("resources/img/ships/size" + tempSize + "/" + String.valueOf(k + 1) + ".png");
                switch (orientation) {
                    case 1:
                        grid.add(vi, points[k].getX(), points[k].getY());
                        break;
                    case 2:
                        vi.setRotate(90);
                        grid.add(vi, points[k].getX(), points[k].getY());
                        break;
                    case 3:
                        vi.setRotate(180);
                        grid.add(vi, points[k].getX(), points[k].getY());
                        break;
                    case 4:
                        vi.setRotate(270);
                        grid.add(vi, points[k].getX(), points[k].getY());
                }
            }
        }
    }
}
