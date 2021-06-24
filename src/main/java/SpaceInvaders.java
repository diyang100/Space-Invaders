import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SpaceInvaders extends Application {
    float SCREEN_WIDTH = 800;
    float SCREEN_HEIGHT = 600;
    float ENEMY_START_HEIGHT = 50;
    float ENEMY_START_WIDTH = 100;

    public static double PLAYER_SPEED = 0;
    public static final double PLAYER_BULLET_SPEED = 6.0;
    long LAST_PLAYER_FIRE = System.currentTimeMillis();
    public static double ENEMY_SPEED = 0.5;
    public static final double ENEMY_VERTICAL_SPEED = 10.0;
    public static int ENEMIES_ALIVE = 50;

    boolean gameOver = false;
    boolean leftKeyPressed = false;
    boolean rightKeyPressed = false;
    int level = 1;
    int score = 0;
    int lives = 3;

    Random numGen = new Random();

    AudioClip playerShoot = new AudioClip(getClass().getClassLoader().getResource("sounds/shoot.wav").toString());
    AudioClip enemyShoot1 = new AudioClip(getClass().getClassLoader().getResource("sounds/fastinvader1.wav").toString());
    AudioClip enemyShoot2 = new AudioClip(getClass().getClassLoader().getResource("sounds/fastinvader2.wav").toString());
    AudioClip enemyShoot3 = new AudioClip(getClass().getClassLoader().getResource("sounds/fastinvader3.wav").toString());
    AudioClip enemyMove = new AudioClip(getClass().getClassLoader().getResource("sounds/fastinvader4.wav").toString());
    AudioClip shipExplosion = new AudioClip(getClass().getClassLoader().getResource("sounds/explosion.wav").toString());
    AudioClip invaderDeath = new AudioClip(getClass().getClassLoader().getResource("sounds/invaderkilled.wav").toString());
    MediaPlayer themeMusic = new MediaPlayer(new Media(getClass().getClassLoader().getResource("sounds/hayden-folker-cloud-nine.wav").toString()));

    private Scene gameScreen(Stage stage, Scene titleScreen, Label levelLabel) {
        Group gameRoot = new Group();
        HBox header = new HBox();
        Font p1Font = Font.font("Arial", 18);
        Label scoreLabel = new Label("Score: " + score);
        Label livesLabel = new Label("Lives: " + lives);
        scoreLabel.setFont(p1Font);
        livesLabel.setFont(p1Font);
        levelLabel.setFont(p1Font);
        livesLabel.setTextFill(Color.WHITE);
        scoreLabel.setTextFill(Color.WHITE);
        levelLabel.setTextFill(Color.WHITE);
        header.getChildren().addAll(scoreLabel, livesLabel, levelLabel);
        header.setSpacing(200);
        gameRoot.getChildren().addAll(header);

        ArrayList<ArrayList<Enemy>> enemies = new ArrayList<>();
        ArrayList<PlayerBullet> playerBullets = new ArrayList<>();
        ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();
        for (int row = 0; row < 5; row++) {
            String url;
            String bulletUrl;
            AudioClip enemyShoot;
            int points;
            ArrayList<Enemy> enemyRow = new ArrayList<>();
            if (row == 0 || row == 1){
                url = "images/enemy1.png";
                bulletUrl = "images/bullet1.png";
                points = 50;
                enemyShoot = enemyShoot1;
            } else if (row == 2 || row == 3) {
                url = "images/enemy2.png";
                bulletUrl = "images/bullet2.png";
                points = 40;
                enemyShoot = enemyShoot2;
            } else {
                url = "images/enemy3.png";
                bulletUrl = "images/bullet3.png";
                points = 30;
                enemyShoot = enemyShoot3;
            }
            for (int col = 0; col < 10; col++) {
                Image enemyPng = new Image(url, 40, 32, false, true);
                Enemy e1 = new Enemy(enemyPng, points, bulletUrl, enemyShoot);
                e1.setPosition(col*43 + ENEMY_START_WIDTH, row * 35 + ENEMY_START_HEIGHT);
                gameRoot.getChildren().addAll(e1.image);
                enemyRow.add(e1);
            }
            enemies.add(enemyRow);
        }

        Player player = new Player();
        player.setPosition(SCREEN_WIDTH/2, SCREEN_HEIGHT-100);
        gameRoot.getChildren().addAll(player.image);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // advancing stage or checking if won
                if (ENEMIES_ALIVE <= 0) {
                    if (level < 3) {
                        level++;
                        levelLabel.setText("Level: " + level);
                        ENEMIES_ALIVE = 50;
                        ENEMY_SPEED = (float)level/2;
                        stage.setScene(gameScreen(stage, titleScreen, levelLabel));

                    } else {
                        stage.setScene(endScreen(stage, titleScreen, "YOU WIN", levelLabel));
                    }
                    this.stop();
                }
                // Check if enemies hit edge
                enemyLoop:
                for (ArrayList<Enemy> enemyRow : enemies) {
                    for (Enemy enemy : enemyRow) {
                        if (!enemy.dead) {
                            if (enemy.image.getX() < 0 || enemy.image.getX() + enemy.width > SCREEN_WIDTH) {
                                ENEMY_SPEED *= -1;
                                for (ArrayList<Enemy> enemyRow1 : enemies) {
                                    for (Enemy enemy1 : enemyRow1) {
                                        enemy1.setPosition(enemy1.image.getX(), enemy1.image.getY() + ENEMY_VERTICAL_SPEED);
                                    }
                                }
                                EnemyBullet bullet = new EnemyBullet(enemy.bulletPng, (float)enemy.getPoints()/20 + (float)level/2);
                                bullet.setPosition(enemy.image.getX() + enemy.width/2, enemy.image.getY());
                                gameRoot.getChildren().addAll(bullet.image);
                                enemyBullets.add(bullet);
                                enemy.shootSound.play();
                                break enemyLoop;
                            }
                        }
                    }
                }

                // enemies fire bullets
                if (enemyBullets.size() < (2 + level) * ((float)ENEMIES_ALIVE/50) + 1){
                    for (ArrayList<Enemy> enemyRow : enemies) {
                        for (Enemy enemy : enemyRow) {
                            if (!enemy.dead && numGen.nextFloat() * 10 <= level && enemyBullets.size() < 3 + level) {
                                EnemyBullet bullet = new EnemyBullet(enemy.bulletPng, (float)enemy.getPoints()/20 + (float)level/2);
                                bullet.setPosition(enemy.image.getX() + enemy.width/2, enemy.image.getY());
                                gameRoot.getChildren().addAll(bullet.image);
                                enemyBullets.add(bullet);
                                enemy.shootSound.play();
                            }
                        }
                    }
                }

                // move enemies
                for (ArrayList<Enemy> enemyRow : enemies) {
                    for (Enemy enemy : enemyRow) {
                        if (!enemy.dead) {
                            enemy.setPosition(enemy.image.getX() + ENEMY_SPEED, enemy.image.getY());
                        }
                    }
                }

                // Check if player bullets hit enemy or goes off screen
                Iterator<PlayerBullet> playerBulletsItr = playerBullets.iterator();
                playerBulletsLoop:
                while (playerBulletsItr.hasNext()) {
                    PlayerBullet bullet = playerBulletsItr.next();
                    for (ArrayList<Enemy> enemyRow : enemies) {
                        for (Enemy enemy : enemyRow) {
                            if (!enemy.dead && bullet.intersects(enemy)) {
                                ENEMY_SPEED += (ENEMY_SPEED >= 0 ? 0.05 : -0.05);
                                enemy.dead = true;
                                ENEMIES_ALIVE--;
                                score += enemy.getPoints();
                                scoreLabel.setText("Score: " + score);
                                gameRoot.getChildren().remove(enemy.image);
                                gameRoot.getChildren().remove(bullet.image);
                                playerBulletsItr.remove();
                                invaderDeath.play();
                                continue playerBulletsLoop;
                            }
                        }
                    }

                    if (bullet.image.getY() < 0) {
                        gameRoot.getChildren().remove(bullet.image);
                        playerBulletsItr.remove();
                    }
                }

                // Check if enemy bullets hit player or goes off screen
                Iterator<EnemyBullet> enemyBulletsItr = enemyBullets.iterator();
                while (enemyBulletsItr.hasNext()) {
                    EnemyBullet bullet = enemyBulletsItr.next();
                    if (bullet.intersects(player) && lives > 1) {
                        lives--;
                        livesLabel.setText("Lives: " + lives);
                        player.setPosition(numGen.nextFloat() * 800, player.image.getY());
                        gameRoot.getChildren().remove(bullet.image);
                        enemyBulletsItr.remove();
                        shipExplosion.play();
                    } else if (bullet.intersects(player) && lives <= 1) {
                        shipExplosion.play();
                        this.stop();
                        stage.setScene(endScreen(stage, titleScreen, "YOU LOSE (no more lives)", levelLabel));
                        break;
                    }

                    if (bullet.image.getY() > SCREEN_HEIGHT) {
                        gameRoot.getChildren().remove(bullet.image);
                        enemyBulletsItr.remove();
                    }
                }
                // remove bullets that hit player during spawn
                Iterator<EnemyBullet> enemyBulletsItr2 = enemyBullets.iterator();
                while (enemyBulletsItr2.hasNext()) {
                    EnemyBullet bullet = enemyBulletsItr2.next();
                    if (bullet.intersects(player)) {
                        gameRoot.getChildren().remove(bullet.image);
                        enemyBulletsItr2.remove();
                    }
                }

                // Move player bullets
                for (PlayerBullet bullet : playerBullets) {
                    bullet.setPosition(bullet.image.getX(), bullet.image.getY() - PLAYER_BULLET_SPEED);
                }

                // Move enemy bullets
                for (EnemyBullet bullet : enemyBullets) {
                    bullet.setPosition(bullet.image.getX(), bullet.image.getY() + bullet.speed);
                }

                // move player
                if (!(player.image.getX() < 0 && PLAYER_SPEED < 0) &&
                        !(player.image.getX() + player.width > SCREEN_WIDTH && PLAYER_SPEED > 0)) {
                    player.setPosition(player.image.getX() + PLAYER_SPEED, player.image.getY());
                }

                // if enemies strikes the bottom, game over!
                enemyLoop:
                for (ArrayList<Enemy> enemyRow : enemies) {
                    for (Enemy enemy : enemyRow) {
                        if (!enemy.dead && enemy.image.getY() > SCREEN_HEIGHT-(120 + player.height)) {
                            this.stop();
                            stage.setScene(endScreen(stage, titleScreen, "YOU LOSE (enemy reached your ship)", levelLabel));
                            break enemyLoop;
                        }
                    }
                }
            }
        };
        timer.start();
        Scene gameScreen = new Scene(gameRoot);
        gameScreen.setFill(Color.BLACK);
        gameScreen.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT:
                    leftKeyPressed = true;
                    PLAYER_SPEED = -4.0;
                    break;
                case RIGHT:
                    rightKeyPressed = true;
                    PLAYER_SPEED = 4.0;
                    break;
                case Q:
                    ENEMY_SPEED = (float)level/2;
                    lives = 3;
                    score = 0;
                    PLAYER_SPEED = 0;
                    leftKeyPressed = false;
                    rightKeyPressed = false;
                    ENEMIES_ALIVE = 50;
                    timer.stop();
                    stage.setScene(titleScreen);
                    break;
                case SPACE:
                    if (System.currentTimeMillis() - LAST_PLAYER_FIRE > 500) {
                        LAST_PLAYER_FIRE = System.currentTimeMillis();
                        PlayerBullet bullet = new PlayerBullet();
                        bullet.setPosition(player.image.getX() + player.width/2, player.image.getY());
                        gameRoot.getChildren().addAll(bullet.image);
                        playerBullets.add(bullet);
                        playerShoot.play();
                    }
            }
        });
        gameScreen.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case LEFT -> {
                    leftKeyPressed = false;
                    if (rightKeyPressed){
                        PLAYER_SPEED = 4.0;
                    } else {
                        PLAYER_SPEED = 0;
                    }
                }
                case RIGHT -> {
                    rightKeyPressed = false;
                    if (leftKeyPressed){
                        PLAYER_SPEED = -4.0;
                    } else {
                        PLAYER_SPEED = 0;
                    }
                }
            }
        });
        return gameScreen;
    }

    private Scene titleScreen(Stage stage, Label levelLabel) {
        Image title = new Image("images/logo.png", 400, 160, true, true);
        ImageView titleImg = new ImageView(title);
        StackPane imgContainer = new StackPane(titleImg);
        imgContainer.setPadding(new Insets(10, 0, 60, 0));

        VBox instructions = new VBox(5);
        Label text1 = new Label("Instructions");
        Label text2 = new Label("ENTER - Start Game");
        Label text3 = new Label("⇦ ⇨ - Move ship left or right");
        Label text4 = new Label("SPACE - Fire missile");
        Label text5 = new Label("Q - Quit Game (or go to title screen when in game)");
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
        Label footer = new Label("Implemented by Di Yang (20828456) for CS 349, University of Waterloo, S21");
        footer.setFont(footerFont);
        VBox titleElements = new VBox(imgContainer, instructions, space, footer);

        titleElements.setAlignment(Pos.TOP_CENTER);
        Scene titleScreen = new Scene(titleElements);

        titleScreen.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DIGIT1 -> {
                    level = 1;
                    ENEMY_SPEED = 0.5;
                    text7.setText("Starting level: " + level);
                    levelLabel.setText("Level: " + level);
                }
                case DIGIT2 -> {
                    level = 2;
                    ENEMY_SPEED = 1;
                    text7.setText("Starting level: " + level);
                    levelLabel.setText("Level: " + level);
                }
                case DIGIT3 -> {
                    level = 3;
                    ENEMY_SPEED = 1.5;
                    text7.setText("Starting level: " + level);
                    levelLabel.setText("Level: " + level);
                }
                case ENTER -> stage.setScene(gameScreen(stage, titleScreen, levelLabel));
                case Q -> System.exit(0);
            }
        });
        return titleScreen;
    }

    private Scene endScreen(Stage stage, Scene titleScreen, String winLoseMsg, Label levelLabel) {

        VBox instructions = new VBox(5);
        Label text1 = new Label("Game Over");
        Label text2 = new Label(winLoseMsg);
        Label text3 = new Label("Your Score: " + score);
        Label text4 = new Label("ENTER - Back to Title Screen");
        Label text5 = new Label("Q - Quit Game");
        Font headerFont = Font.font("Arial", FontWeight.BOLD,35);
        Font p1Font = Font.font("Arial", 18);
        text1.setFont(headerFont);
        text2.setFont(p1Font);
        text3.setFont(p1Font);
        text4.setFont(p1Font);
        text5.setFont(p1Font);
        instructions.getChildren().addAll(text1,text2,text3,text4,text5);
        instructions.setAlignment(Pos.BASELINE_CENTER);

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);
        Region space1 = new Region();
        VBox.setVgrow(space1, Priority.ALWAYS);

        Font footerFont = Font.font("Arial", 10);
        Label footer = new Label("Implemented by Di Yang (20828456) for CS 349, University of Waterloo, S21");
        footer.setFont(footerFont);
        VBox titleElements = new VBox(space, instructions, space1, footer);

        titleElements.setAlignment(Pos.TOP_CENTER);
        Scene endScreen = new Scene(titleElements);

        endScreen.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> {
                    level = 1;
                    levelLabel.setText("Level: " + level);
                    ENEMY_SPEED = (float)level/2;
                    lives = 3;
                    score = 0;
                    leftKeyPressed = false;
                    rightKeyPressed = false;
                    PLAYER_SPEED = 0;
                    ENEMIES_ALIVE = 50;
                    stage.setScene(titleScreen);
                }
                case Q -> System.exit(0);
            }
        });
        return endScreen;
    }

    @Override
    public void start(Stage stage) {
        Label levelLabel = new Label("Level: " + level);
        Scene titleScreen = titleScreen(stage, levelLabel);

        themeMusic.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                themeMusic.seek(Duration.ZERO);
            }
        });
        themeMusic.play();

        stage.setScene(titleScreen);
        stage.setResizable(false);
        stage.setTitle("Space Invaders");
        stage.setWidth(SCREEN_WIDTH);
        stage.setHeight(SCREEN_HEIGHT);
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.show();
    }
}
