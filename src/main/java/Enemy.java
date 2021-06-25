import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;

public class Enemy {
    public ImageView image;
    public final double width;
    public final double height;
    private final int points;
    public boolean dead;
    public final String bulletPng;
    public final AudioClip shootSound;

    public Enemy(Image image, int points, String bulletPng, AudioClip shootSound) {
        this.points = points;
        this.image = new ImageView(image);
        this.bulletPng = bulletPng;
        width = image.getWidth();
        height = image.getHeight();
        dead = false;
        this.shootSound = shootSound;
    }

    public void setPosition(double x, double y) {
        image.setX(x);
        image.setY(y);
    }

    public int getPoints() {
        return points;
    }

    public Rectangle2D getBoundary() {
        return new Rectangle2D(image.getX(), image.getY(), width, height);
    }

    public boolean intersects(PlayerBullet bullet) {
        return bullet.getBoundary().intersects(this.getBoundary());
    }
    public boolean intersects(Player player) {
        return player.getBoundary().intersects(this.getBoundary());
    }
}
