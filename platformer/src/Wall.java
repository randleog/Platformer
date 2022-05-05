import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Wall extends GameEntity {

    public Wall(double x, double y, Map map,  double sizeX, double sizeY, InputAction side, FillType fillType, double parallax) {
        super(x,y,map,side, fillType, parallax);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.wallTile;
        if (sizeX < 10 && sizeY < 10) {
            System.out.println("wall is too small");
        }
    }


    public void tick() {

    }

    public void render(GraphicsContext g) {
        renderSquare(g);
    }
}
