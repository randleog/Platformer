package Map;

import Map.Map;
import Util.ImageLoader;
import Util.UserFileHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Trophy extends GameEntity {

    private String name;

    public Trophy(double x, double y, Map map, String name) {
        super(x,y,map, InputAction.Default, FillType.Image, 1);
        this.sizeX = 75;
        this.name = name;
        this.sizeY = 75;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.trophy;

    }


    public void tick() {

        if (this.intersect(map.player)) {
            map.removeEntity(this);

            if (!UserFileHandler.getTrophies(map.getName()).contains(name)) {
                UserFileHandler.saveUserTrophy(map.getName(), name);

            }

        }

    }

    public void render(GraphicsContext g) {
        renderSquare(g);
    }


    public String toString() {
        String line = "trophy " + (int)x + " " + (int)y + " " + name;

        return line;
    }
}
