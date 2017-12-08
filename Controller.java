package com.aryan.ConnectFour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    private static final int COLUMN = 7;
    private static final int ROW = 6;
    private static final int CIRCLE_DIAMETER = 100;
    private static final String discColor1 = "#24303E";
    private static final String discColor2 = "#4CAA88";

    private static String PLAYER_ONE = "player one";
    private static String PLAYER_TWO = "Player Two";

    private boolean isPlayerOneTurn = true;

    @FXML
    public GridPane gridRootNode;

    @FXML
    public Pane insertedDiscPane;

    @FXML
    public Pane menuPane;

    @FXML
    public VBox playerVPane;

    @FXML
    public Label playerNameLabel;

    @FXML
    public Label playerTurn;

    @FXML
    public Button setNamesButton;

    @FXML
    public TextField playerTwoTextField, playerOneTextField;

    private boolean isAllowedToInsert = true;                      //Flag for preventing the number of disc inserted my the same player.

    //Declaring 2DArray of the "Disc class" which is define bellow.
    private Disc[][] insertedDiscArray = new Disc[ROW][COLUMN];                    //For the structural changes: For developer.

    public void createPlayGround(){

        setNamesButton.setOnAction(actionEvent -> {
             PLAYER_ONE = playerOneTextField.getText();
             PLAYER_TWO = playerTwoTextField.getText();

             playerNameLabel.setText(PLAYER_ONE);
        });

        Shape rectangleWidthHole = createGameStructureGrid();
        gridRootNode.add(rectangleWidthHole, 0,1);

        List<Rectangle> rectangleList = createClickableColumn();
        for (Rectangle rectangle: rectangleList) {
            gridRootNode.add(rectangle, 0,1);
        }
    }

    //Creating the hole structure of the play ground and Calling from "createPayGround()".
    private Shape createGameStructureGrid() {
        Shape rectangleWidthHole = new Rectangle((COLUMN + 1) * CIRCLE_DIAMETER, (ROW + 1) * CIRCLE_DIAMETER);

        for (int row = 0; row < ROW; row++){
            for (int col = 0; col < COLUMN; col++){
                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2);
                circle.setCenterX(CIRCLE_DIAMETER / 2);
                circle.setCenterY(CIRCLE_DIAMETER / 2);
                circle.setSmooth(true);

                circle.setTranslateX(col * (CIRCLE_DIAMETER + 6) + CIRCLE_DIAMETER / 3);
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 6) + CIRCLE_DIAMETER / 3);

                rectangleWidthHole = Shape.subtract(rectangleWidthHole, circle);
            }
        }

        rectangleWidthHole.setFill(Color.WHITE);
        return rectangleWidthHole;
    }

    //Creating click able columns of the play ground and Calling from "createPayGround()".
    public List<Rectangle> createClickableColumn(){

        List<Rectangle> rectangleList = new ArrayList<>();

        for(int col = 0; col < COLUMN; col++){
            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROW + 1) * CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(CIRCLE_DIAMETER / 3);
            rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 6) + CIRCLE_DIAMETER / 3);

            //Mouse Hover effect
            rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee32")));
            rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT));

            //Inserting disc on the column where player clicked.
            final int column = col;
            rectangle.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (isAllowedToInsert){
                    isAllowedToInsert = false;                       //once the dsc is drop another disc can not be inserted by same player.
                    insertDisc(new Disc(isPlayerOneTurn), column);
                }
            });

            rectangleList.add(rectangle);
        }

        return rectangleList;
    }

    //Inserting disc on the particular column where the use clicked and Calling from "createClickableColumn()".
    private void insertDisc(Disc disc, int column) {
        int row = ROW-1;
        while (row >= 0){
            if(getDiscIfPresent(row, column) == null)
                break;
                row--;
            }
        if (row < 0)
            return;  //if it is full we can not insert any more.

        insertedDiscArray[row][column] = disc;        //For the structural changes: For developer.
        insertedDiscPane.getChildren().add(disc);   //For the visual structure: For player.

        disc.setTranslateX(column * (CIRCLE_DIAMETER + 6) + CIRCLE_DIAMETER / 3);

        final int currentRow = row;
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.4), disc);
        transition.setToY(row * (CIRCLE_DIAMETER + 6) + CIRCLE_DIAMETER / 3);
        transition.setOnFinished(actionEvent -> {

            isAllowedToInsert = true;  //once the disc drop by the player one then it will true of another player.
            if(gameEnded(currentRow, column)){
                gameOver();
                return;
            }

            isPlayerOneTurn = !isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE: PLAYER_TWO);
        });
        transition.play();
    }

    private void gameOver() {
        String winner = isPlayerOneTurn? PLAYER_ONE: PLAYER_TWO;
        System.out.println("winner is: "+ winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four Game");
        alert.setHeaderText("Winner is " + winner);
        alert.setContentText("Want to play again?");

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No, Exit");
        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Platform.runLater(() -> {

            Optional<ButtonType> clickedBtn = alert.showAndWait();
            if (clickedBtn.isPresent() && clickedBtn.get() == yesBtn){
                //... User wants to play game again.
                resetGame();
            }else {
                //... User wants to exit the game.
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void resetGame() {
        insertedDiscPane.getChildren().clear();  // Remove all the disc from pane.

        for (int row = 0; row < insertedDiscArray.length; row++){
            for (int col = 0; col < insertedDiscArray[row].length; col++){
                insertedDiscArray[row][col] = null;
            }
        }

        isPlayerOneTurn = true;
        playerNameLabel.setText(PLAYER_ONE);

        createPlayGround();
    }

    private boolean gameEnded(int row, int column) {
        //vertical collection of four dick.
        List<Point2D> verticalPoint = IntStream.rangeClosed(row - 3, row + 3).
                                      mapToObj(r -> new Point2D(r, column)).
                                      collect(Collectors.toList());

        //Horizontal collection of four dick.
        List<Point2D> horizontalPoint = IntStream.rangeClosed(column - 3, column + 3).
                mapToObj(col -> new Point2D(row, col)).
                collect(Collectors.toList());

        //Diagonal1 collection of four dick.
        Point2D startingPoint1 = new Point2D(row -3, column + 3 );
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6).
                                        mapToObj(i -> startingPoint1.add(i, -i)).
                                        collect(Collectors.toList());

        //Diagonal2 collection of four dick.
        Point2D startingPoint2 = new Point2D(row -3, column - 3 );
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6).
                mapToObj(i -> startingPoint2.add(i, i)).
                collect(Collectors.toList());

        boolean isEnded = checkCombinations(verticalPoint) || checkCombinations(horizontalPoint)|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);

        return isEnded;
    }

    private boolean checkCombinations(List<Point2D> points) {
        int chain = 0;

        for(Point2D point: points){
            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

            if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn){
                chain++;
                if(chain == 4){
                    return true;
                }
            }else {
                chain = 0;
            }
        }

        return false;
    }

    //prevent from ArrayBoundException
    private Disc getDiscIfPresent(int row, int column){
        if (row >= ROW || row < 0 || column >= COLUMN || column < 0){    // if row or column index is invalid.
            return null;
        }

        return insertedDiscArray[row][column];
    }

    //Creating disc (Circle) for particular player with color (assigned to the particular player) and Calling from "createClickableColumn()".
    private class Disc extends Circle {
        private final boolean isPlayerOneMove;

        public Disc(boolean isPlayerOneMove) {
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER / 2);
            setFill(isPlayerOneMove? Color.valueOf(discColor1): Color.valueOf(discColor2));
            setCenterX(CIRCLE_DIAMETER / 2);
            setCenterY(CIRCLE_DIAMETER / 2);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
