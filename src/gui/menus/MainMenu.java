package gui.menus;

import game.Game;
import gameelements.Ship;
import gui.fields.MainField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import player.ComputerPlayer;
import player.HumanPlayer;
import player.Player;

public class MainMenu{

    private TextField inputNickname = new TextField("nickname");

    public void createMainMenu(Stage primaryStage) {
        VBox vb = new VBox(10);
        vb.setPadding(new Insets(10));
        vb.setAlignment(Pos.CENTER);
        primaryStage.getIcons().add(new Image("css/img/cursor.png"));

        Scene scene = new Scene(vb);

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
        createGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int k = 10000000;

                Ship.getRandomlyPlacedShips();
                int min = Ship.cent;
                Ship.cent = 0;

                for (int i = 0; i < k; i++) {
                    Ship.getRandomlyPlacedShips();

                    if(Ship.cent < min){
                        min = Ship.cent;
                    }
                    Ship.cent = 0;
                }
                System.out.println("average: " + min);
            }
        });

        Button createGameVsComputerButton = new Button("Play VS computer");
        createGameVsComputerButton.setAlignment(Pos.CENTER);
        createGameVsComputerButton.setPrefWidth(Double.MAX_VALUE);
        createGameVsComputerButton.setOnAction(event -> createVSComputerGame());

        inputNickname.textProperty().addListener((observable, oldValue, newValue) -> {
            createGameButton.setDisable(newValue.isEmpty() || newValue.length()<3);
            findGameButton.setDisable(newValue.isEmpty()|| newValue.length()<3);
            createGameVsComputerButton.setDisable(newValue.isEmpty()|| newValue.length()<3);
        });

        vb.getChildren().addAll(hb1,findGameButton,createGameButton,new Separator(),createGameVsComputerButton);
        primaryStage.show();
    }


    private void findRoomWindow(){
        Stage w = new Stage();
        w.initModality(Modality.APPLICATION_MODAL);
        VBox vb = new VBox(10);
        vb.setPadding(new Insets(10));
        vb.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vb);

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

        vb.getChildren().addAll(hb1,findGameButton);


        w.show();
    }

    private void createVSComputerGame(){
        Player first = new HumanPlayer(inputNickname.getText());
        Player second = new ComputerPlayer("Computer");

        Game mGame = new Game(first,second);

        MainField playerField = new MainField(mGame);
        playerField.show();

    }
}


