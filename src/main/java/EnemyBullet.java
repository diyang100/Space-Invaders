import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EnemyBullet {
    public ImageView image;
    public final double width;
    public final double height;
    public final float speed;

    public EnemyBullet(String bulletUrl, float speed) {
        Image enemyBulletPng = new Image(bulletUrl, 4, 14, false, true);
        this.speed = speed;
        this.image = new ImageView(enemyBulletPng);
        width = enemyBulletPng.getWidth();
        height = enemyBulletPng.getHeight();
    }

    public void setPosition(double x, double y) {
        image.setX(x);
        image.setY(y);
    }

    public Rectangle2D getBoundary() {
        return new Rectangle2D(image.getX(), image.getY(), width, height);
    }

    public boolean intersects(Player player) {
        return player.getBoundary().intersects(this.getBoundary());
    }
}
