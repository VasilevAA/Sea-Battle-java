package gui.fields;

import game.Game;
import game.elements.GameField;
import game.elements.Point;
import game.elements.Ship;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashSet;

public class MainField extends Stage {

    private Button[][] opponentField = new Button[10][10];
    private Button[][] playerField = new Button[10][10];

    private Game game;

    private VBox opponentPart;

    private Label statusBar;

    private Label numberOfOpponentShips;
    private Label numberOfPlayerShips;

    private static int cellSize = 28;

    public MainField(Game game) {
        this.game = game;
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);
        getIcons().add(new Image("resources/img/cursor.png"));
        setTitle("Battleship");

        HBox node = new HBox();
        Separator sep = new Separator(Orientation.VERTICAL);
        sep.setMinWidth(20);
        opponentPart = setUpOpponentField();
        VBox playerPart = setUpPlayerField();
        node.setPadding(new Insets(5));
        node.getChildren().addAll(opponentPart, sep, playerPart);

        statusBar = new Label("Your Turn");
        statusBar.setMinWidth(60);
        HBox node2 = new HBox();
        node2.setMinHeight(20);
        node2.getChildren().addAll(new Label("   Status: "), statusBar);

        numberOfOpponentShips = new Label("10");
        numberOfOpponentShips.setMinWidth(159);
        numberOfPlayerShips = new Label("10");
        HBox node3 = new HBox();
        node3.setMinHeight(20);
        node3.getChildren().addAll(new Label("   Alive opponents ships: "), numberOfOpponentShips, new Separator(Orientation.VERTICAL), new Label("   Your ships alive: "), numberOfPlayerShips);

        VBox mainNode = new VBox();
        mainNode.getChildren().addAll(node, new Separator(), node3, new Separator(), node2);

        Scene scene = new Scene(mainNode);
        scene.getStylesheets().setAll("resources/styles/modena.css");
        setScene(scene);
    }

    private void updateFields() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                GameField.CellStatus status = game.getOpponent().getCellStatus(new Point(j, i));

                if (status == GameField.CellStatus.SHIPSHOT) {
                    opponentField[i][j].setStyle("-fx-background-color:rgba(0,0,0,0.8);-fx-background-image:url(resources/img/fire.gif);");
                    opponentField[i][j].setDisable(true);
                } else if (status == GameField.CellStatus.EMPTYSHOT) {
                    opponentField[i][j].setStyle("-fx-background-color: transparent;");
                    opponentField[i][j].setDisable(true);
                }

                status = game.getPlayer().getCellStatus(new Point(j, i));

                if (status == GameField.CellStatus.SHIPSHOT) {
                    playerField[i][j].setStyle("-fx-background-image:url(resources/img/fire.gif);");
                } else if (status == GameField.CellStatus.EMPTYSHOT) {
                    playerField[i][j].setStyle("-fx-background-image:url(resources/img/empty.png);");
                }
            }
        }

        numberOfPlayerShips.setText(String.valueOf(game.getPlayer().shipsAlive()));
        numberOfOpponentShips.setText(String.valueOf(game.getOpponent().shipsAlive()));
    }

    private void receiveShot() {

        Point point = game.receiveShotFromOpponent();
        statusBar.setText("Opponent Turn");

        Task task = new Task() {
            @Override
            protected Object call() {
                opponentPart.setDisable(true);

                try {
                    Thread.sleep(/*new Random().nextInt(2000) + 500*/100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                opponentPart.setDisable(false);
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            updateFields();

            if (winnerIsFound()) return;

            GameField.CellStatus status = game.getPlayer().getCellStatus(point);

            if (status == GameField.CellStatus.SHIPSHOT) {
                receiveShot();
            }
            statusBar.setText("Your Turn");
        });

        new Thread(task).start();
    }

    private boolean winnerIsFound() {
        if (game.getWinner() != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Congrats!");
            alert.setHeaderText(null);

            alert.setContentText(game.getWinner().name() + " won!");
            alert.showAndWait();
            close();
            return true;
        }
        return false;
    }

    private VBox setUpPlayerField() {

        VBox vb = new VBox(4);

        Label lb1 = new Label("  Your field: " + game.getPlayer().name());
        lb1.setMinHeight(20);
        vb.getStylesheets().add("resources/styles/PlayerField.css");

        GridPane grid = new GridPane();
        Image ima = new Image("resources/img/sea.png");
        grid.setBackground(new Background(new BackgroundImage(ima,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,BackgroundSize.DEFAULT)));

        HashSet<Ship> ships = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button but =
                        playerField[i][j] = new Button(" ");
                but.setMinSize(cellSize, cellSize);
                if (game.getPlayer().getCellStatus(new Point(j, i)) == GameField.CellStatus.SHIP) {

                   Ship temp =  game.getPlayer().getField().getShip(new Point(j,i));

                   if(!ships.contains(temp)) {
                       ships.add(temp);
                       int sizeTemp = temp.getSize();
                       int orient = temp.getOrientation();
                       Point points[] = temp.getPoints();
                       ImageView vi;
                       if (sizeTemp == 4) {
                           for (int k = 0; k < sizeTemp; k++) {

                               vi = new ImageView("resources/img/ships/size" + sizeTemp + "/" + String.valueOf(k + 1) + ".png");


                               switch (orient) {
                                   case 1:
                                       break;
                                   case 2:
                                       vi.setRotate(90);
                                       break;
                                   case 3:
                                       vi.setRotate(180);
                                       break;
                                   case 4:
                                       vi.setRotate(270);
                               }
                               grid.add(vi, points[k].getX(), points[k].getY());
                           }
                       }
                   }


                }
                but.setDisable(true);
                grid.add(but, j, i);
            }
        }
        vb.getChildren().addAll(lb1, grid);
        return vb;
    }

    private VBox setUpOpponentField() {

        VBox vb = new VBox(4);
        vb.getStylesheets().add("resources/styles/OpponentField.css");
        Label lb1 = new Label("  Opponent field: " + game.getOpponent().name());
        lb1.setMinHeight(20);
        lb1.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        Image ima = new Image("resources/img/sea.png");
        grid.setBackground(new Background(new BackgroundImage(ima,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,BackgroundSize.DEFAULT)));
       // grid.add(new ImageView(new Image("resources/img/empty.png")),0,0);
        Image im = new Image("resources/img/cursor.png");
//        ImageView v = new ImageView(im);
//        v.setRotate(45);
//        SnapshotParameters params = new SnapshotParameters();
//        params.setFill(Color.TRANSPARENT);
//        im = v.snapshot(params, null);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button but =
                        opponentField[i][j] = new Button(" ");
                but.setMinSize(cellSize, cellSize);
                but.setCursor(new ImageCursor(im, im.getWidth() / 2, im.getHeight() / 2));
                int finalI = i;
                int finalJ = j;

                but.setOnAction(event -> {

                    this.game.sendShotToOpponent(new Point(finalJ, finalI));
                    updateFields();

                    if (winnerIsFound()) return;

                    GameField.CellStatus status = this.game.getOpponent().getCellStatus(new Point(finalJ, finalI));

                    if (status != GameField.CellStatus.SHIPSHOT) {
                        receiveShot();
                    }
                });

                grid.add(but, j, i);
            }

        }
        vb.getChildren().addAll(lb1, grid);
        return vb;
    }

}
