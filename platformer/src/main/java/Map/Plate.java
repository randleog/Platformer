package Map;

import Map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import Util.ImageLoader;
import Menu.Menu;

import java.util.ArrayList;

public class Plate extends GameEntity {

    private int code;
    private boolean pressed;

    public Plate(double x, double y, Map map, double parallax, int code) {
        super(x,y,map, InputAction.Default, FillType.Image, parallax);
        this.code = code;
        this.sizeX = 50;
        this.sizeY = 50;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.plate;
        pressed = false;
        map.keys.put(code, false);
    }



    public void setCode(int code) {
        this.code = code;
    }


    public void tick() {



        if (map.player == null) {
            return;
        }

        ArrayList<InputAction> actions = map.getActions(this);

        if(this.getMainShape().intersect(map.player.getMainShape()) || actions.contains(InputAction.Shurikan)) {
            pressed = true;
            map.keys.put(code, true);
        } else {
            pressed = false;
            map.keys.put(code, false);
        }
    }

    public void render(GraphicsContext g) {
        if (pressed) {
            this.y = y+5;
        }

        renderSquare(g);
        if (Menu.currentMenu.equals("editor")) {
            g.setFill(Color.WHITE);
            g.setFont(new Font("monospaced", 20));
            g.fillText("code: " + code, getRenderX(), getRenderY()+getRenderSizeY());
        }
        if (pressed) {
            this.y = y-5;
        }


    }

    public int getCode() {
        return code;
    }

    public String toString() {
        String line = "plate " + (int)x + " " + (int)y + " " + code;

        return line;
    }
}
