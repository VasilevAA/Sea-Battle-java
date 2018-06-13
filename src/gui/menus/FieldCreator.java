package gui.menus;

import game.elements.GameField;
import game.elements.Point;
import game.elements.Ship;
import game.player.Player;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

class FieldCreator extends Stage {

    private GameField field;
    private Player player;
    private Button[][] playerField = new Button[10][10];

    private FlowPane pane = null;
    private GridPane grid = null;



    FieldCreator(Player player) {
        field = new GameField();
        this.player = player;
        setResizable(false);

        initModality(Modality.APPLICATION_MODAL);

        HBox hb = new HBox();

        hb.getChildren().addAll(setUpPlayerField(), new Separator(Orientation.VERTICAL), setUpRightPart());

        Scene scene = new Scene(hb);
        setScene(scene);

        setOnCloseRequest(event -> player.getField().setCell(new Point(0, 0), GameField.CellStatus.SHIPSHOT));

    }

    private GridPane setUpPlayerField() {


        grid = new GridPane();
        grid.getStylesheets().add("resources/styles/PlayerField.css");
        Image ima = new Image("resources/img/sea.png");
        grid.setBackground(new Background(new BackgroundImage(ima, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));


        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button but =
                        playerField[i][j] = new Button(" ");

                int finalI = i;
                int finalJ = j;
                but.setOnDragDropped((DragEvent event) -> {
                    Dragboard db = event.getDragboard();

                    if (db.hasString()) {
                        String id = db.getString();

                        ImageView temp = (ImageView) pane.lookup("#" + id);
                        ImageView temp2 = (ImageView) grid.lookup("#" + id);
                        if (temp != null) {

                           double cosplain = temp.getImage().getWidth()/temp.getImage().getHeight();

                           int row = 1;
                           int col = 1;
                           if(cosplain > 1){
                               col = (int)cosplain;

                           }
                           if(cosplain <1){
                               row = (int)(1/cosplain);
                           }

                            grid.add(temp, finalJ, finalI, col,row);


                            pane.getChildren().remove(temp);
                        }

                        if (temp2 != null) {
                            grid.getChildren().remove(temp2);

                            double cosplain = temp2.getImage().getWidth()/temp2.getImage().getHeight();

                            int row = 1;
                            int col = 1;
                            if(cosplain > 1){
                                col = (int)cosplain;

                            }
                            if(cosplain <1){
                                row = (int)(1/cosplain);
                            }
                            grid.add(temp2, finalJ, finalI, col,row);
                        }


                    }

                    event.setDropCompleted(true);
                    event.consume();
                });


                but.setOnDragOver((DragEvent event) -> {
                    if (
                            event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });

                but.setMinSize(28, 28);

//                but.setDisable(true);
                grid.add(but, j, i);
            }
        }

        return grid;
    }


    private VBox setUpRightPart() {
        VBox vb = new VBox();

        vb.setPrefWidth(400);


        Button generateRandom = new Button("Random");
        generateRandom.setOnAction(event -> {
            field = new GameField();
            field.placeShips(Ship.getRandomlyPlacedShips());
            updateFields();
        });

        Button ok = new Button("Start game");
        ok.setOnAction(event -> {
            player.getField().placeShips(field.getShips());
            close();
        });


        pane = new FlowPane();
        pane.setPadding(new Insets(0));

        pane.setMinHeight(240);
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);


        for (int i = 0, size = 4; i < 10; i++) {
            if (i == 1 || i == 3 || i == 6) {
                size--;
            }
            ImageView imv = new ImageView("resources/img/ships/size" + size + "/full.png");
            imv.setId(this.getClass().getSimpleName() + System.currentTimeMillis() + i);
            imv.setOnDragDetected(event -> {
                Dragboard db = imv.startDragAndDrop(TransferMode.MOVE);


                db.setDragView(imv.getImage(), 14, 14);


                ClipboardContent cont = new ClipboardContent();
                cont.putString(imv.getId());
                db.setContent(cont);
                event.consume();
            });

            imv.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY
                        && pane.lookup("#" + imv.getId()) != null) {
                    imv.setPreserveRatio(true);
                    imv.setRotate(90);
                    Image nIm = imv.snapshot(new SnapshotParameters(), null);
                    imv.setRotate(0);
                    imv.setImage(nIm);

                }
                event.consume();
            });
            pane.getChildren().add(imv);
        }

        pane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String id = db.getString();

                ImageView temp = (ImageView) grid.lookup("#" + id);
                if (temp != null) {

                    grid.getChildren().remove(temp);

                    pane.getChildren().add(temp);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        pane.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != pane &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });


        HBox hb = new HBox(generateRandom, ok);

        vb.getChildren().addAll(pane, new Separator(), hb);


        return vb;
    }


    private void updateFields() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                GameField.CellStatus status = field.getCell(new Point(j, i));
                if (status == GameField.CellStatus.SHIP) {
                    playerField[i][j].setStyle("-fx-background-color: orange;");
                } else if (status == GameField.CellStatus.EMPTY) {
                    playerField[i][j].setStyle("-fx-background-color: transparent;");
                }

            }
        }
    }
}
