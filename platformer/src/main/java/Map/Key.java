package Map;

import Map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import Util.ImageLoader;
import Menu.Menu;

public class Key extends GameEntity {

    private int code;

    public Key(double x, double y, Map map, double parallax, int code) {
        super(x,y,map, InputAction.Default, FillType.Image, parallax);
        this.code = code;
        this.sizeX = 50;
        this.sizeY = 50;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.key;

    }


    public void collectKey() {
        map.removeEntity(this);
        map.keys.put(Integer.valueOf(code), true);
    }

    public void setCode(int code) {
        this.code = code;
    }


    public void tick() {


        if (map.player == null) {
            return;
        }
        if(this.getMainShape().intersect(map.player.getMainShape())) {
            collectKey();
        }
    }

    public void render(GraphicsContext g) {

        renderSquare(g);
    if (Menu.currentMenu.equals("editor")) {
        g.setFill(Color.WHITE);
        g.setFont(new Font("monospaced", 20));
        g.fillText("code: " + code, getRenderX(), getRenderY()+getRenderSizeY());
    }

    }

    public int getCode() {
        return code;
    }

    public String toString() {
        String line = "key " + (int)x + " " + (int)y + " " + code;

        return line;
    }
}
