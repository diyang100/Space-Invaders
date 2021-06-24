import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player {
    public ImageView image;
    public final double width;
    public final double height;

    public Player() {
        Image playerPng = new Image("images/player.png", 40, 24, false, true);
        this.image = new ImageView(playerPng);
        width = playerPng.getWidth();
        height = playerPng.getHeight();
    }

    public void setPosition(double x, double y) {
        image.setX(x);
        image.setY(y);
    }

    public Rectangle2D getBoundary() {
        return new Rectangle2D(image.getX(), image.getY(), width, height);
    }

//    public boolean intersects(EnemyBullet bullet) {
//        return bullet.getBoundary().intersects(this.getBoundary());
//    }
}
