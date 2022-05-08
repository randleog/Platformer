import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class ReplayPlayer extends GameEntity {





    private static final double SPRINT_HEIGHT_FACTOR = 0.7;

    private boolean hooking = false;

    private double speedFactor;

    private ArrayList<Integer[]> frames;

    private boolean isReplay;

    public ReplayPlayer(double x, double y, Map map, ArrayList<Integer[]> frames, boolean isReplay) {
        super(x, y, map, InputAction.Default, FillType.Image, 1);
        this.isReplay = isReplay;
        this.frames = frames;


        speedFactor = (frames.get(0)[0]*1.0) /  Main.fps;
        System.out.println(speedFactor + " " + Main.fps + " " + (frames.get(0)[0]*1.0));
        frames.remove(0);

        this.sizeX = 50;
        this.sizeY = 50;

        map.player = new Player(-1000,-1000, map);


    }



    public void tick() {
        int currentTick =(int)(map.getCurrentTick()*speedFactor);

        if (currentTick < 0) {
            currentTick = (currentTick+(Math.abs(currentTick)/(frames.size()-1))*(frames.size()-1))+(frames.size()-1);
        }
        currentTick = (currentTick)% (frames.size()-1);


        x = frames.get(currentTick)[0];
        y = frames.get(currentTick)[1];





        if (isReplay) {
            map.cameraX = x - 700;
            map.cameraY = y - 500;
            map.playerX = x;
            map.playerY = y;
        }
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

        g.save();
        g.setGlobalAlpha(0.5);
        renderSquare(g);
        g.restore();

    }
}
