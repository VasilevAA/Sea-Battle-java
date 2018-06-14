package gui.menus;

import game.Game;
import game.elements.GameField;
import game.elements.Point;
import game.player.computer.ComputerPlayer;
import game.player.human.HumanPlayer;
import gui.fields.MainField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainMenu {

    private TextField inputNickname = new TextField("nickname");

    public void createMainMenu(Stage primaryStage) {
        VBox vb = new VBox(10);
        vb.setPadding(new Insets(10));
        vb.setAlignment(Pos.CENTER);
        primaryStage.getIcons().add(new Image("resources/img/cursor.png"));

        Scene scene = new Scene(vb);
        scene.getStylesheets().setAll("resources/styles/modena.css");
        primaryStage.setWidth(200);
        primaryStage.setHeight(200);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Sea Battle");

        Label inf_lab = new Label("Nickname: ");
        inf_lab.setMinWidth(60);
        HBox hb1 = new HBox(5, inf_lab, inputNickname);


        Button findGameButton = new Button("Find game");
        findGameButton.setAlignment(Pos.CENTER);
        findGameButton.setPrefWidth(Double.MAX_VALUE);
        findGameButton.setOnAction(event -> findRoomWindow());

        Button createGameButton = new Button("Create game room");
        createGameButton.setAlignment(Pos.CENTER);
        createGameButton.setPrefWidth(Double.MAX_VALUE);

        Button createGameVsComputerButton = new Button("Play VS computer");
        createGameVsComputerButton.setAlignment(Pos.CENTER);
        createGameVsComputerButton.setPrefWidth(Double.MAX_VALUE);
        createGameVsComputerButton.setOnAction(event -> createVSComputerGame());

        inputNickname.textProperty().addListener((observable, oldValue, newValue) -> {
            createGameButton.setDisable(newValue.isEmpty() || newValue.length() < 3);
            findGameButton.setDisable(newValue.isEmpty() || newValue.length() < 3);
            createGameVsComputerButton.setDisable(newValue.isEmpty() || newValue.length() < 3);
        });

        vb.getChildren().addAll(hb1, findGameButton, createGameButton, new Separator(), createGameVsComputerButton);
        primaryStage.show();
    }


    private void findRoomWindow() {
        Stage w = new Stage();
        w.initModality(Modality.APPLICATION_MODAL);
        VBox vb = new VBox(10);
        vb.setPadding(new Insets(10));
        vb.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vb);
        scene.getStylesheets().setAll("resources/styles/modena.css");

        w.setWidth(240);
        w.setHeight(120);
        w.setScene(scene);
        w.setResizable(false);
        w.setTitle("Sea Battle");

        Label inf_lab = new Label("Room ID: ");
        inf_lab.setMinWidth(60);
        TextField input = new TextField();
        HBox hb1 = new HBox(5, inf_lab, input);

        Button findGameButton = new Button("Search");
        findGameButton.setAlignment(Pos.CENTER);

        vb.getChildren().addAll(hb1, findGameButton);


        w.show();
    }

    private void createVSComputerGame() {
        HumanPlayer human = new HumanPlayer(inputNickname.getText());

        new FieldCreator(human).showAndWait();

        if(human.getCellStatus(new Point(0,0)) == GameField.CellStatus.SHIPSHOT){
            return;
        }

        Game mGame = new Game(human, new ComputerPlayer("Computer"));

        MainField playerField = new MainField(mGame);
        playerField.show();

    }
}