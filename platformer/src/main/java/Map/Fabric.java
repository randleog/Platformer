package Map;

import Util.Settings;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

import Main.Main;
import javafx.scene.paint.Color;

public class Fabric {

    private ArrayList<Double[]> points;

    private int length;

    private GameEntity entity;

    private Color color;

    private double thickness;

    private double gravity;


    private static final int POLY_COUNT = 7;

    public Fabric(int length, GameEntity entity, Color color, double thickness, double gravity) {
        this.thickness = thickness;
        this.entity = entity;
        this.length = length;
        points = new ArrayList<>();

        this.gravity = gravity;

        for (int i = 0; i < POLY_COUNT; i++) {
            points.add(new Double[]{entity.getX(), entity.getY()});

        }


        this.color = color;


    }



    public void tick() {

        points.set(0, new Double[]{entity.getX(),entity.getY()+11,0.0});

        for (int i = 1; i < points.size(); i++) {



            if (Main.getDistance(points.get(i)[0], points.get(i)[1], points.get(i-1)[0], points.get(i-1)[1]) > getSectionLength()) {
                double factor = Math.sqrt(Math.pow(points.get(i)[0] - points.get(i-1)[0], 2) + Math.pow(points.get(i)[1] - points.get(i-1)[1], 2)) + 1;

                double magX = getSectionLength() * (points.get(i)[0] - points.get(i-1)[0]) / factor;
                double magY = getSectionLength() * (points.get(i)[1] - points.get(i-1)[1]) / factor;
                points.set(i, new Double[]{magX + points.get(i-1)[0], magY + points.get(i-1)[1]});
            }

            points.set(i, new Double[]{points.get(i)[0], points.get(i)[1]+gravity/Settings.getD("fps")});
        }



    }

    private double getSectionLength() {
        return (length * 1.0) / points.size();
    }

    public void render(GraphicsContext g, boolean facingRight, double x, double y) {

        double xOffset = x-Main.correctUnit(points.get(0)[0]-entity.getMap().cameraX);
        double yOffset = y-Main.correctUnit(points.get(0)[1]-entity.getMap().cameraY);



        g.setStroke(color);



        if (facingRight) {

            for (int i = 1; i < points.size(); i++) {
                double x1=  Main.correctUnit(points.get(i-1)[0]-entity.getMap().cameraX)+xOffset;
                double x2= Main.correctUnit(points.get(i)[0]-entity.getMap().cameraX)+xOffset;

                double y1= Main.correctUnit(points.get(i-1)[1]-entity.getMap().cameraY)+yOffset;
                double y2 =  Main.correctUnit(points.get(i)[1]-entity.getMap().cameraY)+yOffset;

                g.setLineWidth(Main.correctUnit(3));
                g.strokeLine(x1, y1, x2, y2);

            }

        } else {

            for (int i = 1; i < points.size(); i++) {

                double x1=Main.correctUnit(points.get(i-1)[0]-entity.getMap().cameraX)+xOffset;
                double x2=Main.correctUnit(points.get(i)[0]-entity.getMap().cameraX)+xOffset;


                double y1 =Main.correctUnit(points.get(i-1)[1]-entity.getMap().cameraY)+yOffset;
                double y2 =Main.correctUnit(points.get(i)[1]-entity.getMap().cameraY)+yOffset;

                g.setLineWidth(Main.correctUnit(thickness));
                g.strokeLine(x1, y1, x2, y2);
            }
        }
    }

    public void renderStill(GraphicsContext g, boolean facingRight, double offset) {



        g.setStroke(color);



        if (facingRight) {

            for (int i = 1; i < points.size(); i++) {
                double x1= Main.correctUnit(points.get(i-1)[0]-points.get(0)[0])-Main.correctUnit(25);
                double x2=Main.correctUnit(points.get(i)[0]-points.get(0)[0])-Main.correctUnit(25);


                double y1 =Main.correctUnit(points.get(i-1)[1]-points.get(0)[1]);
                double y2 =Main.correctUnit(points.get(i)[1]-points.get(0)[1]);

                g.setLineWidth(Main.correctUnit(3));
                g.strokeLine(x1, y1, x2, y2);

            }

        } else {

            for (int i = 1; i < points.size(); i++) {

                double x1= Main.correctUnit(points.get(i-1)[0]-points.get(0)[0])+Main.correctUnit(25);
                double x2=Main.correctUnit(points.get(i)[0]-points.get(0)[0])+Main.correctUnit(25);


                double y1 =Main.correctUnit(points.get(i-1)[1]-points.get(0)[1]);
                double y2 =Main.correctUnit(points.get(i)[1]-points.get(0)[1]);

                g.setLineWidth(Main.correctUnit(thickness));
                g.strokeLine(x1, y1, x2, y2);
            }
        }
    }



}
