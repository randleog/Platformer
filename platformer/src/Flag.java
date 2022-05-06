import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Flag extends GameEntity {

    public Flag(double x, double y, Map map) {
        super(x,y,map,InputAction.Default, FillType.Image, 1);
        this.sizeX = 75;
        this.sizeY = 75;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.flag;

    }


    public void tick() {

        if (this.intersect(map.player)) {
            UserFileHandler.saveUserTime(map.getName(), map.getTime());
            Main.switchMenu(Main.levelMenu);
        }

    }

    public void render(GraphicsContext g) {
        renderSquare(g);
    }
}
