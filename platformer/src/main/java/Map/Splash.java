package Map;

import javafx.scene.canvas.GraphicsContext;

public class Splash {


    private double x;
    private double y;

    private int currentTick;
    private int maxTick;

    public Splash(double x, double y, int maxTick) {
        this.maxTick = maxTick;
        this.y = y;
        this.x = x;
        currentTick = 0;
    }


    public void tick() {
        currentTick++;
    }


    public void render(GraphicsContext g) {

    }




}
