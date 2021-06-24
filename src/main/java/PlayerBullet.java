import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlayerBullet {
    public ImageView image;
    public final double width;
    public final double height;

    public PlayerBullet() {
        Image playerBulletPng = new Image("images/player_bullet.png", 4, 14, false, true);
        this.image = new ImageView(playerBulletPng);
        width = playerBulletPng.getWidth();
        height = playerBulletPng.getHeight();
    }

    public void setPosition(double x, double y) {
        image.setX(x);
        image.setY(y);
    }

    public Rectangle2D getBoundary() {
        return new Rectangle2D(image.getX(), image.getY(), width, height);
    }

    public boolean intersects(Enemy enemy) {
        return enemy.getBoundary().intersects(this.getBoundary());
    }
}
