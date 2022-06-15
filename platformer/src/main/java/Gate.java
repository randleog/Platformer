import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class Gate extends GameEntity {

    private int code;
    private Image lock;



    public Gate(double x, double y, Map map,  double sizeX, double sizeY, InputAction side, FillType fillType, double parallax, int code) {
        super(x,y,map,side, fillType, parallax);
        this.code = code;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.gate;
        if (sizeX < 10 && sizeY < 10) {
            System.out.println("wall is too small");
        }

        lock = ImageLoader.lock;

        loadWallHitbox();
    }

    @Override
    public boolean isWall() {
        return true;
    }

    public void tick() {
        if (map.keys.keySet().contains(Integer.valueOf(code))) {
            map.removeEntity(this);
        }

    }
    protected void loadWallHitbox() {
        hitbox = new ArrayList<>();
        hitbox.add(new Square(x + WALL_CORNER_SIZE, y, sizeX - WALL_CORNER_SIZE * 2, 1, parallax, InputAction.Up));
        hitbox.add(new Square(x + sizeX - 1, y + WALL_CORNER_SIZE, 1, sizeY - WALL_CORNER_SIZE * 2, parallax, InputAction.Right));
        hitbox.add(new Square(x + WALL_CORNER_SIZE, y + sizeY - 1 , sizeX - WALL_CORNER_SIZE * 2, 1, parallax, InputAction.Down));
        hitbox.add(new Square(x, y + WALL_CORNER_SIZE, 1, sizeY - WALL_CORNER_SIZE * 2, parallax, InputAction.Left));

    }
    public void render(GraphicsContext g) {

        renderSquare(g);
        g.drawImage(lock, getRenderX()+getRenderSizeX()/2-Main.correctUnit(Map.GRID_SIZE)/2, getRenderY()+getRenderSizeY()/2-Main.correctUnit(Map.GRID_SIZE)/2, Main.correctUnit(Map.GRID_SIZE), Main.correctUnit(Map.GRID_SIZE));

        if (Menu.currentMenu.equals("editor")) {
            g.setFill(Color.WHITE);
            g.setFont(new Font("monospaced", 20));
            g.fillText("code: " + code, getRenderX(), getRenderY()+getRenderSizeY()/2);
        }
    }

    public String toString() {
        String line = "gate " + (int)x + " " + (int)y + " " + (int)sizeX + " " + (int)sizeY + " " + code;

        return line;
    }
}
