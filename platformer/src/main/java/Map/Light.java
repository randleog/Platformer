package Map;

import Map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.*;
import Util.ImageLoader;

public class Light extends GameEntity {

    private double ratio;

    public Light(double x, double y, double sizeX, double sizeY, Map map) {
        super(x,y,map, InputAction.Default, FillType.Image, 1);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.pinkWall;



        ratio = sizeX/sizeY;
    }


    public void tick() {




    }

    public void render(GraphicsContext g) {



        g.save();

        g.setGlobalBlendMode(BlendMode.OVERLAY);
        Color light = Color.color(0.6,0.5,1, 1);
        Color fade = Color.color(0,0,0, 0);


/*
        g.setGlobalBlendMode(BlendMode.SOFT_LIGHT);
        Color light = Color.color(0.6,0.5,1, 1);
        Color fade = Color.color(0,0,0, 0);

 */


        //  Stop[] stops = new Stop[] { new Stop(0, fade),new Stop(0.5, light),new Stop(1, fade)};
        Stop[] stops = new Stop[] {new Stop(0, light),new Stop(1, fade)};
        RadialGradient fadeLight = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, stops);
        g.setFill(fadeLight);





        g.fillRect(getRenderX(), getRenderY(), getRenderSizeX(), getRenderSizeY());


      //  g.fillRect(getRenderX(), getRenderY(), getRenderSizeX(), getRenderSizeY());
        g.restore();


    }


    public String toString() {
        String line = "candleNot " + (int)x + " " + (int)y+ " " + (int)sizeX + " " + (int)sizeY;

        return line;
    }
}
