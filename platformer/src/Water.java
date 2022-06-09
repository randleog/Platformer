import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Water extends GameEntity {



    private ArrayList<int[]> centers = new ArrayList<>();



    public Water(double x, double y, Map map,  double sizeX, double sizeY, double parallax) {
        super(x,y,map,InputAction.Swim, FillType.Color, parallax);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(0,0.7,1, 0.3);
        this.image = ImageLoader.wallTile;
        if (sizeX < 10 && sizeY < 10) {
            System.out.println("wall is too small");
        }








    }




    @Override
    public boolean isWall() {
        return true;
    }



    public void tick() {


    }

    public void render(GraphicsContext g) {
        renderSquare(g);


    }



    public String toString() {
        String line = "wall " + (int)x + " " + (int)y + " " + (int)sizeX + " " + (int)sizeY;

        return line;
    }
}
