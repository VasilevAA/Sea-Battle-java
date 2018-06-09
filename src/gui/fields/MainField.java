package gui.fields;

import game.Game;
import game.elements.GameField;
import game.elements.Point;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainField extends Stage {

    private Button[][] opponentField = new Button[10][10];
    private Button[][] playerField = new Button[10][10];

    private Game game;

    private VBox opponentPart;

    private Label statusBar;

    private static int cellSize = 28;

    public MainField(Game game) {
        this.game = game;
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);

        HBox node = new HBox();
        Separator sep = new Separator(Orientation.VERTICAL);
        sep.setMinWidth(20);
        opponentPart = setUpOpponentField();
        VBox playerPart = setUpPlayerField();
        node.getChildren().addAll(opponentPart, sep, playerPart);

        statusBar = new Label("Your Turn");
        HBox node2 = new HBox();
        node2.getChildren().addAll(new Label("Status: "), statusBar);

        VBox mainNode = new VBox();
        mainNode.getChildren().addAll(node, new Separator(), node2);

        Scene scene = new Scene(mainNode);
        setScene(scene);
    }

    private void updateFields() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                GameField.CellStatus status = game.getOpponent().getCellStatus(new Point(j, i));

                if (status == GameField.CellStatus.SHIPSHOT) {
                    opponentField[i][j].setStyle("-fx-background-color:blue;-fx-background-image:url(resources/img/fire.gif);");
                    opponentField[i][j].setDisable(true);
                } else if (status == GameField.CellStatus.EMPTYSHOT) {
                    opponentField[i][j].setStyle("-fx-background-image:url(resources/img/empty.png);");
                    opponentField[i][j].setDisable(true);
                }

                status = game.getPlayer().getCellStatus(new Point(j, i));

                if (status == GameField.CellStatus.SHIPSHOT) {
                    playerField[i][j].setStyle("-fx-background-color:red;-fx-background-image:url(resources/img/fire.gif);");
                } else if (status == GameField.CellStatus.EMPTYSHOT) {
                    playerField[i][j].setStyle("-fx-background-image:url(resources/img/empty.png);");
                }
            }
        }
    }

    private void getShot() {

        Point point = game.getShot();
        statusBar.setText("Opponent Turn");

        Task task = new Task() {
            @Override
            protected Object call() {
                opponentPart.setDisable(true);

                try {
                    Thread.sleep(/*new Random().nextInt(2000) + 500*/500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                opponentPart.setDisable(false);
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            updateFields();

            if(game.getWinner() != null){
                System.out.println("game winner is " + game.getWinner().name());
                return;
            }

            GameField.CellStatus status = game.getPlayer().getCellStatus(point);

            if (status == GameField.CellStatus.SHIPSHOT) {
                getShot();
            }
            statusBar.setText("Your Turn");
        });

        new Thread(task).start();
    }

    private VBox setUpPlayerField() {

        VBox vb = new VBox(4);

        Label lb1 = new Label("Your field: " + game.getPlayer().name());
        vb.getStylesheets().add("resources/styles/PlayerField.css");

        GridPane grid = new GridPane();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button but =
                        playerField[i][j] = new Button(" ");
                but.setMinSize(cellSize, cellSize);
                if (game.getPlayer().getCellStatus(new Point(j, i)) == GameField.CellStatus.SHIP) {
                    but.setStyle("-fx-background-color: limegreen");
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
        Label lb1 = new Label("Opponent field: " + game.getOpponent().name());

        GridPane grid = new GridPane();

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
                //but.setFocusTraversable(false);
                but.setMinSize(cellSize, cellSize);
                but.setCursor(new ImageCursor(im, im.getWidth() / 2, im.getHeight() / 2));
                int finalI = i;
                int finalJ = j;

                but.setOnAction(event -> {

                    this.game.makeShot(new Point(finalJ, finalI));
                    updateFields();

                    if(game.getWinner() != null){
                        System.out.println("game winner is " + game.getWinner().name());
                        return;
                    }

                    GameField.CellStatus status = this.game.getOpponent().getCellStatus(new Point(finalJ, finalI));

                    if (status != GameField.CellStatus.SHIPSHOT) {
                        getShot();
                    }
                });

                grid.add(but, j, i);
            }

        }
        vb.getChildren().addAll(lb1, grid);
        return vb;
    }

}
