import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class ReplayPlayer extends GameEntity {





    private static final double SPRINT_HEIGHT_FACTOR = 0.7;

    private boolean hooking = false;

    private double speedFactor;

    private ArrayList<Integer[]> frames;

    private double fps;

    private boolean isReplay;

    public ReplayPlayer(double x, double y, Map map, ArrayList<Integer[]> frames, boolean isReplay, String type) {
        super(x, y, map, InputAction.Default, FillType.Image, 1);
        this.isReplay = isReplay;
        this.frames = frames;


        speedFactor = (frames.get(0)[0]*1.0) /  Main.fps;
        this.fps=frames.get(0)[0];
        System.out.println(speedFactor + " " + Main.fps + " " + (frames.get(0)[0]*1.0));
        frames.remove(0);

        if (type.equals("gold")) {
            this.fillType = FillType.Color;
            this.color = Color.color(1,0.75,0);
        } else if (type.equals("author")) {
            this.fillType = FillType.Color;
            this.color = Color.color(0,0.6,0);
        }

        this.sizeX = 50;
        this.sizeY = 50;

        map.player = new Player(-1000,-1000, map);


    }



    public void tick() {
        if (frames.size() < 1) {
            map.removeEntity(this);
        }
        int currentTick =(int)(map.getCurrentTick()*speedFactor);

        if (currentTick < 0) {
            currentTick = (currentTick+(Math.abs(currentTick)/(frames.size()-1))*(frames.size()-1))+(frames.size()-1);
        }
        if (currentTick >= frames.size()-1) {
            if (!isReplay) {
                map.removeEntity(this);
            }
        }
        currentTick = (currentTick)% (frames.size()-1);


        x = frames.get(currentTick)[0];
        y = frames.get(currentTick)[1];

        /*
            if (currentTick < frames.size() - 1) {
                x = Main.interpolate(x, frames.get(currentTick + 1)[0], speedFactor, map.getCurrentTick() % speedFactor);
                y = Main.interpolate(y, frames.get(currentTick + 1)[1], speedFactor, map.getCurrentTick() % speedFactor);
            }



         */




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
