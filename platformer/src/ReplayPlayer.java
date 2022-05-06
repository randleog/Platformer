import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class ReplayPlayer extends GameEntity {





    private static final double SPRINT_HEIGHT_FACTOR = 0.7;

    private boolean hooking = false;

    private ArrayList<Integer[]> frames;

    public ReplayPlayer(double x, double y, Map map, ArrayList<Integer[]> frames) {
        super(x, y, map, InputAction.Default, FillType.Image, 1);
        this.frames = frames;
        this.sizeX = 50;
        this.sizeY = 50;

        map.player = new Player(-1000,-1000, map);


    }



    public void tick() {
        int currentTick = Math.max(map.getCurrentTick(), 0);
        if (currentTick >= frames.size()-1) {
            Main.switchMenu(Main.replayMenu);
        }

        x = frames.get(currentTick)[0];
        y = frames.get(currentTick)[1];


        map.cameraX = x-700;
        map.cameraY = y-500;
        //  x = startX;



        map.playerX = x;
        map.playerY = y;
    }

    @Override
    public double getSizeY() {
        double sizeBuff = 1;
        if (Main.isKeyDown(InputAction.Down)) {
            sizeBuff = SPRINT_HEIGHT_FACTOR;
        }
        return sizeY * sizeBuff;
    }





    public void render(GraphicsContext g) {


        renderSquare(g);

    }
}
