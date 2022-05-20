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

    private String type;



    public ReplayPlayer(double x, double y, Map map, ArrayList<Integer[]> frames, boolean isReplay, String type) {
        super(x, y, map, InputAction.Default, FillType.Image, 1);
        this.isReplay = isReplay;
        this.frames = frames;
        this.type = type;



        speedFactor = (frames.get(0)[0]*1.0) /  Main.fps;
        this.fps=frames.get(0)[0];

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
        int currentTick =getCurrentTick();
        if (currentTick >= frames.size()-1) {

            if (!isReplay) {
                map.removeEntity(this);
            } else {
                if (type.equals("player")) {
                    map.resetTimer();
                    currentTick =getCurrentTick();

                }
            }
        }

        x = frames.get(currentTick)[0];
        y = frames.get(currentTick)[1];


        alignMap();
    }

    @Override
    public double getSizeY() {
        double sizeBuff = 1;
        if (Main.isKeyDown(InputAction.Down)) {
            sizeBuff = SPRINT_HEIGHT_FACTOR;
        }
        return sizeY * sizeBuff;
    }



    private int getCurrentTick() {
        int currentTick =(int)(map.getCurrentTick()*speedFactor);

        if (currentTick < 0) {
            currentTick = (currentTick+(Math.abs(currentTick)/(frames.size()-1))*(frames.size()-1))+(frames.size()-1);
        }


        return currentTick;
    }

    @Override
    public double getRenderX() {
        double x = map.correctUnit(this.x - getVelStretchX()) - map.correctUnit(map.cameraX * parallax);
        if (getCurrentTick() < frames.size()-1) {
            double x1 = map.correctUnit(this.x - getVelStretchX()) - map.correctUnit(map.cameraX * parallax);
            double x2 = map.correctUnit(frames.get(getCurrentTick() + 1)[0] - getVelStretchX()) - map.correctUnit(map.cameraX * parallax);
            x = Main.interpolate(x1, x2, 1, map.getCurrentTick() - (int) map.getCurrentTick());
        }

        return x;

    }

    @Override
    public double getRenderY() {
        double y = map.correctUnit(this.y - getVelStretchY()) - map.correctUnit(map.cameraY * parallax);
        if (getCurrentTick() < frames.size()-1) {
            double y1 = map.correctUnit(this.y - getVelStretchY()) - map.correctUnit(map.cameraY * parallax);
            double y2 = map.correctUnit(frames.get(getCurrentTick() + 1)[1] - getVelStretchY()) - map.correctUnit(map.cameraY * parallax);
            y = Main.interpolate(y1, y2, 1, map.getCurrentTick() - (int) map.getCurrentTick());
        }
        return y;

    }


    private void alignMap() {

        double x = this.x;
        double y = this.y;
        if (getCurrentTick() < frames.size()-1) {

            x = Main.interpolate(x, frames.get(getCurrentTick() + 1)[0], 1, map.getCurrentTick() - (int) map.getCurrentTick());
            y = Main.interpolate(y, frames.get(getCurrentTick() + 1)[1], 1, map.getCurrentTick() - (int) map.getCurrentTick());
        }
        if (isReplay) {
            map.cameraX = x - 700;
            map.cameraY = y - 500;
            map.playerX = x;
            map.playerY = y;
        }
    }

    public void render(GraphicsContext g) {


        g.save();
        g.setGlobalAlpha(0.5);
        renderSquare(g);
        g.restore();

    }
}
