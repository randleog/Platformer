package Map;

import Map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

import Util.Replay;
import Util.Settings;
import Menu.Menu;
import Util.Replay;
import Main.Main;


public class ReplayPlayer extends GameEntity {

    private static final double SPRINT_HEIGHT_FACTOR = 0.7;


    private double speedFactor;

    private ArrayList<Integer[]> frames;

    private double fps;

    private boolean isReplay;

    private String type;

    private boolean hasFinished = false;

    private static final double OFFSET = 4;


    public ReplayPlayer(double x, double y, Map map, Replay replay, boolean isReplay, String type) {
        super(x, y, map, InputAction.Default, FillType.Image, 1);
        this.isReplay = isReplay;
        this.frames = replay.getFrames();
        this.type = type;

        if (!(type == null)) {

            map.availableReplays.put(type, replay);
        }



        this.fps = replay.getFps();

        speedFactor = fps / Settings.getD("fps");

        if (type.equals("gold")) {
            this.fillType = FillType.Color;
            this.color = Color.color(1, 0.75, 0);
        } else if (type.equals("author")) {
            System.out.println("wut");
            this.fillType = FillType.Color;
            this.color = Color.color(0, 0.6, 0);
        } else {

            this.color = Color.color(0.5, 0, 1);
        }

        this.sizeX = 50;
        this.sizeY = 50;


        if (isReplay) {
            map.player = new Player(-1000, -1000, map);

        }


        map.initialiseButtons();


    }


    public void tick() {

        if (frames.size() < 1) {
            map.removeEntity(this);
        }
        int currentTick = getCurrentTick();
        if (currentTick >= frames.size() - 1) {
            if (!hasFinished) {
                map.finished++;
            }
            hasFinished = true;
            if (!(isReplay || Menu.currentlyMenu)) {
                map.removeEntity(this);
            } else {
                if (type.equals(Settings.getStr("focus"))) {
                    map.resetTimer();
                    hasFinished = false;
                }
            }
            x = frames.get(1)[0];
            y = frames.get(1)[1];
        } else {
            x = frames.get(currentTick)[0];
            y = frames.get(currentTick)[1];
        }


        setAlignMap();
    }

    @Override
    public double getRenderSizeY() {
        if (frames.get(0).length > 2) {
            double size = map.correctUnit(this.sizeY - getVelStretchX()) ;
            if (getCurrentTick() < frames.size() - 1) {
                double x1 = map.correctUnit(frames.get(getCurrentTick() )[2] ) ;
                double x2 = map.correctUnit(frames.get(getCurrentTick() + 1)[2]) ;

                size= interpolate(x1, x2);
            }

            return size;


        } else {
            return super.getRenderSizeY();
        }

    }


    private int getCurrentTick() {


        int currentTick = (int) (map.getCurrentTick() * speedFactor);

        int size = frames.size() - 1;

        if (currentTick < 0) {
            currentTick = (currentTick + (Math.abs(currentTick) / (size)) * (size)) + (size);
        }

        return currentTick;
    }

    private double getCurrent() {
        double currentTick = (map.getCurrentTick() * fps / (Settings.getD("fps")) - (int) ((map.getCurrentTick() * fps) / Settings.getD("fps")));

        if (map.getCurrentTick() < 0) {
            currentTick = Math.min(0, currentTick);
            currentTick = Math.max(-1, currentTick);
        } else {
            currentTick = Math.min(1, currentTick);
            currentTick = Math.max(0, currentTick);
        }
        return currentTick;

    }

    private double interpolate(double v1, double v2) {

        if (map.getCurrentTick() < 0) {
            return Main.interpolate(v2, v1, -1, getCurrent());
        } else {
            return Main.interpolate(v1, v2, 1, getCurrent());
        }


    }


    @Override
    public double getRenderX() {
        double x = map.correctUnit(this.x - getVelStretchX()) - map.correctUnit(map.cameraX * parallax);
        if (getCurrentTick() < frames.size() - 1) {
            double x1 = map.correctUnit(this.x - getVelStretchX()) - map.correctUnit(map.cameraX * parallax);
            double x2 = map.correctUnit(frames.get(getCurrentTick() + 1)[0] - getVelStretchX()) - map.correctUnit(map.cameraX * parallax);

            x = interpolate(x1, x2);
        }

        return x;

    }


    @Override
    public double getRenderY() {



        double y = map.correctUnit(this.y - getVelStretchY()) - map.correctUnit(map.cameraY * parallax);
        if (getCurrentTick() < frames.size() - 1) {
            double y1 = map.correctUnit(this.y - getVelStretchY()) - map.correctUnit(map.cameraY * parallax);
            double y2 = map.correctUnit(frames.get(getCurrentTick() + 1)[1] - getVelStretchY()) - map.correctUnit(map.cameraY * parallax);

            y = interpolate(y1, y2);


        }
        return y;

    }

    private void alignMap() {
        double x = this.x;
        double y = this.y;
        if (getCurrentTick() < frames.size() - 1) {

            x = interpolate(x, frames.get(getCurrentTick() + 1)[0]);
            y = interpolate(y, frames.get(getCurrentTick() + 1)[1]);

        }

        map.cameraX = x - 700;
        map.cameraY = y - 500;
        map.playerX = x;
        map.playerY = y;
    }


    private void setAlignMap() {

        if (!(isReplay || Main.isVictory)) {
            return;
        }


        if (Settings.getStr("focus").equals(type)) {

            System.out.println("weay");
            alignMap();
        }

    }


    public void render(GraphicsContext g) {

        boolean isFocus = Settings.getStr("focus").equals(type);

        double focus = 0.3;
        if ((isReplay || Main.isVictory)) {
            if (isFocus) {
                focus = 0.8;
            } else {
                focus = 0.3;
            }
        }


        if (Settings.get("show " + type) > 0) {


            g.save();
            g.setGlobalAlpha(focus);
            renderSquare(g);
            g.restore();


            if (Menu.currentlyMenu) {
               // g.restore();
                return;
            }

            if (isReplay) {
                if (isFocus) {

                    double currentOffset = Math.cos(map.getCurrentTick() / (Settings.getD("fps") / 6)) * OFFSET;
                    g.setStroke(color);

                    g.setLineDashes(map.correctUnit(10));

                    g.setLineWidth(map.correctUnit(2));
                    g.strokeRect(getRenderX() - Main.correctUnit(currentOffset), getRenderY() - Main.correctUnit(currentOffset), getRenderSizeX() + Main.correctUnit(currentOffset * 2), getRenderSizeY() + Main.correctUnit(currentOffset * 2));
                }
            }


            g.setFill(Color.WHITE);
            g.setFont(new Font(Settings.FONT, Main.correctUnit(20)));
            g.fillText(type, getRenderX(), getRenderY());
       //     g.restore();

        }

    }
}
