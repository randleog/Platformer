import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

import java.util.ArrayList;


public class CornerWall extends GameEntity {

    private double rotation;

    public CornerWall(double x, double y, Map map, double sizeX, double sizeY, InputAction side, FillType fillType, double parallax, double rotation) {
        super(x, y, map, side, fillType, parallax);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.rotation = rotation;
        this.color = Color.color(1, 0.5, 0);
        this.image = ImageLoader.wallTile;
        if (sizeX < 10 && sizeY < 10) {
            System.out.println("wall is too small");
        }
        loadCornerWallHitbox();
    }

    @Override
    public boolean isWall() {
        return true;
    }


    public double getRotation() {
        return rotation;
    }


    public void tick() {

    }

    protected void loadCornerWallHitbox() {
        hitbox = new ArrayList<>();
        hitbox.add(new Square(x , y, sizeX, sizeY, parallax, InputAction.Corner, rotation));
       // hitbox.add(new Shape(x + sizeX, y + WALL_CORNER_SIZE, 1, sizeY - WALL_CORNER_SIZE * 2, parallax, InputAction.Right, rotation));
     //   hitbox.add(new Shape(x + WALL_CORNER_SIZE, y + sizeY - WALL_CORNER_SIZE-1, sizeX - WALL_CORNER_SIZE * 2, 1, parallax, InputAction.Down, rotation));
     //   hitbox.add(new Shape(x, y + WALL_CORNER_SIZE, 1, sizeY - WALL_CORNER_SIZE * 2, parallax, InputAction.Left, rotation));

    }


    public void render(GraphicsContext g) {

        if (rotation == 225) {
            g.setFill(new ImagePattern(image, getRenderX(), getRenderY(), map.correctUnit(tileSize) * parallax, map.correctUnit(tileSize) * parallax, false));
            g.fillPolygon(new double[]{getRenderX(), getRenderX(), getRenderX() + map.correctUnit(sizeX)}, new double[]{getRenderY(), getRenderY() + map.correctUnit(sizeY), getRenderY() + map.correctUnit(sizeY)}, 3);

        } else if (rotation == 315) {
            g.setFill(new ImagePattern(image, getRenderX(), getRenderY(), map.correctUnit(tileSize) * parallax, map.correctUnit(tileSize) * parallax, false));
            g.fillPolygon(new double[]{getRenderX(), getRenderX()+ map.correctUnit(sizeX), getRenderX() + map.correctUnit(sizeX)}, new double[]{getRenderY()+ map.correctUnit(sizeY), getRenderY() + map.correctUnit(sizeY), getRenderY()}, 3);
        } else {
            g.save();
            g.translate(getRenderX() + map.correctUnit(sizeX / 2), getRenderY() + map.correctUnit(sizeY / 2));
            g.rotate(rotation);


            renderStill(g);
            g.restore();
        }
        for (Square shape : hitbox) {
            shape.render(g, map.cameraX, map.cameraY, (Player)map.player);
        }



    }


    public String toString() {
        String line = "corner " + (int) x + " " + (int) y + " " + (int) sizeX + " " + (int) sizeY + " " + rotation;

        return line;
    }
}
