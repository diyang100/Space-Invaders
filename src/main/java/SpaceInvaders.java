import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Stack;

public class SpaceInvaders extends Application {
    float SCREEN_WIDTH = 800;
    float SCREEN_HEIGHT = 600;

    boolean gameOver = false;
    int level = 1;
    int score = 0;
    int lives = 3;

    private Scene titleScreen(Stage stage, Scene gameScreen) {
        Image title = new Image("images/logo.png", 400, 160, true, true);
        ImageView titleImg = new ImageView(title);
        StackPane imgContainer = new StackPane(titleImg);
        imgContainer.setPadding(new Insets(10, 0, 60, 0));

        VBox instructions = new VBox(5);
        Label text1 = new Label("Instructions");
        Label text2 = new Label("ENTER - Start Game");
        Label text3 = new Label("⇦ ⇨ - Move ship left or right");
        Label text4 = new Label("SPACE - Fire missile");
        Label text5 = new Label("Q - Quit Game");
        Label text6 = new Label("1 or 2 or 3 - Start game on specific level");
        Label text7 = new Label("Starting level: " + level);
        Font headerFont = Font.font("Arial", FontWeight.BOLD,35);
        Font p1Font = Font.font("Arial", 18);
        text1.setFont(headerFont);
        text2.setFont(p1Font);
        text3.setFont(p1Font);
        text4.setFont(p1Font);
        text5.setFont(p1Font);
        text6.setFont(p1Font);
        text7.setFont(p1Font);
        instructions.getChildren().addAll(text1,text2,text3,text4,text5,text6,text7);
        instructions.setAlignment(Pos.BASELINE_CENTER);

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        Font footerFont = Font.font("Arial", 10);
        Label footer = new Label("Implemented by Di Yang for CS 349, University of Waterloo, S21");
        footer.setFont(footerFont);
        VBox titleElements = new VBox(imgContainer, instructions, space, footer);

        titleElements.setAlignment(Pos.TOP_CENTER);
        Scene titleScreen = new Scene(titleElements);

        titleScreen.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DIGIT1 -> {
                    level = 1;
                    text7.setText("Starting level: " + level);
                }
                case DIGIT2 -> {
                    level = 2;
                    text7.setText("Starting level: " + level);
                }
                case DIGIT3 -> {
                    level = 3;
                    text7.setText("Starting level: " + level);
                }
                case ENTER -> stage.setScene(gameScreen);
            }
        });
        return titleScreen;
    }
    @Override
    public void start(Stage stage) {
        Scene gameScreen = new Scene(new Group());
        Scene titleScreen = titleScreen(stage, gameScreen);
        stage.setScene(titleScreen);
        stage.setResizable(false);
        stage.setTitle("Space Invaders");
        stage.setWidth(SCREEN_WIDTH);
        stage.setHeight(SCREEN_HEIGHT);
        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        stage.show();
    }
}
