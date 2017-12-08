package com.aryan.ConnectFour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    public static void main (String[] args){
        launch(args);
    }

    @Override
    public void init() throws Exception {
        System.out.println("Initialized");
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Start");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("game_layout.fxml"));
        GridPane rootNode = loader.load();

        controller = loader.getController();
        controller.createPlayGround();

        MenuBar menuBar = createMenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane) rootNode.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootNode);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four Game");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenuBar(){
        // File-Menu
        Menu fileMenu = new Menu("File");
        //Menu Items
        MenuItem newGameMenuItem = new MenuItem("New Game");
        newGameMenuItem.setOnAction(actionEvent -> {
            controller.resetGame();
        });

        MenuItem resetGameMenuItem = new MenuItem("Reset Game");
        resetGameMenuItem.setOnAction(actionEvent -> {
            controller.resetGame();
        });

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem quitMenuItem = new MenuItem("Quit Game");
        quitMenuItem.setOnAction(actionEvent -> {
            quitGame();
        });

        fileMenu.getItems().addAll(newGameMenuItem, resetGameMenuItem, separatorMenuItem, quitMenuItem);

        //Action for File-Menu


        //About-Menu
        Menu helpMenu = new Menu("Help");
        //Menu Items
        MenuItem aboutGameMenuItem = new MenuItem("About Game");
        aboutGameMenuItem.setOnAction(actionEvent -> {
            aboutGame();
        });

        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
        MenuItem aboutDeveloperMenuItem = new MenuItem("About Developer");
        aboutDeveloperMenuItem.setOnAction(actionEvent -> {
            aboutDeveloper();
        });

        helpMenu.getItems().addAll(aboutGameMenuItem, separatorMenuItem1, aboutDeveloperMenuItem);

        //Adding all menus.
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;
    }

    private void quitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void aboutDeveloper() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Developer");
        alert.setHeaderText("Aryan Sahu");
        alert.setContentText("I love to play around with code and create games. Connect 4 is one of them. In free time I like to spend time with nears and dears.");
        alert.getDialogPane().setMinSize(150, 200);
        alert.show();
    }

    private void aboutGame() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How to play?");
        alert.setContentText("Connect Four is a two-player connection game in which the " +
                "players first choose a color and then take turns dropping colored discs " +
                "from the top into a seven-column, six-row vertically suspended grid. "+
                "The pieces fall straight down, occupying the next available space within the column. "+
                "The objective of the game is to be the first to form a horizontal, vertical, " +
                "or diagonal line of four of one's own discs. Connect Four is a solved game. " +
                "The first player can always win by playing the right moves.");
        alert.getDialogPane().setMinSize(250, 310);
        alert.show();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Stop");
        super.stop();
    }
}
