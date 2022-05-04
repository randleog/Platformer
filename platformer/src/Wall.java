import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Wall extends GameEntity {

    public Wall(double x, double y, Map map,  double sizeX, double sizeY, InputAction side) {
        super(x,y,map,side);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(1,0.5,0);
    }


    public void tick() {

    }

    public void render(GraphicsContext g) {
        renderSquare(g);
    }
}
