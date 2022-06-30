package Map;

import Util.Settings;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

import Main.Main;
import javafx.scene.paint.Color;

public class SpiderEye extends Fabric{

    protected ArrayList<Double[]> points;

    protected int length;

    protected GameEntity entity;

    protected Color color;

    protected double thickness;

    protected double gravity;

    protected double xGravity;


    public static final int POLY_COUNT = 7;

    public SpiderEye(int length, GameEntity entity, Color color, double thickness, double gravity, double xGravity) {
        super(length, entity, color, thickness, gravity, xGravity);


        this.thickness = thickness;
        this.entity = entity;
        this.length = length;
        this.xGravity = xGravity;
        points = new ArrayList<>();

        this.gravity = gravity;

        for (int i = 0; i < POLY_COUNT; i++) {
            points.add(new Double[]{entity.getX(), entity.getY()});

        }


        this.color = color;


    }



    @Override
    public void tick() {

        points.set(0, new Double[]{entity.getX(),entity.getY()+11,0.0});

        for (int i = 1; i < points.size(); i++) {



            if (Main.getDistance(points.get(i)[0], points.get(i)[1], points.get(i-1)[0], points.get(i-1)[1]) > getSectionLength()) {
                double factor = Math.max(Math.sqrt(Math.pow(points.get(i)[0] - points.get(i-1)[0], 2) + Math.pow(points.get(i)[1] - points.get(i-1)[1], 2)) + 1, getSectionLength());

                double magX = getSectionLength() * (points.get(i)[0] - points.get(i-1)[0]) / factor;
                double magY = getSectionLength() * (points.get(i)[1] - points.get(i-1)[1]) / factor;
                points.set(i, new Double[]{magX + points.get(i-1)[0], magY + points.get(i-1)[1]});
            }

            points.set(i, new Double[]{points.get(i)[0]+xGravity/Settings.getD("fps"), points.get(i)[1]+gravity/Settings.getD("fps")});
        }



    }



    @Override
    public void renderStill(GraphicsContext g, boolean facingRight, double offset) {



        g.setStroke(color);

        for (int i = 1; i < points.size(); i++) {
            double x1= Main.correctUnit(points.get(i-1)[0]-points.get(0)[0]);
            double x2=Main.correctUnit(points.get(i)[0]-points.get(0)[0]);


            double y1 =Main.correctUnit(points.get(i-1)[1]-points.get(0)[1]);
            double y2 =Main.correctUnit(points.get(i)[1]-points.get(0)[1]);

            g.setLineWidth(Main.correctUnit(thickness));
            g.strokeLine(x1, y1, x2, y2);

        }
    }





}
