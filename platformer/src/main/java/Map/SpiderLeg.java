package Map;

import Main.Main;
import Util.Settings;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SpiderLeg extends  Fabric{

    private int randomness;

    public SpiderLeg(int length, GameEntity entity, Color color, double thickness, double gravity, double xGravity, int randomness) {
        super(length, entity, color, thickness, gravity, xGravity);


        this.randomness = randomness;
    }


    @Override
    public void tick() {

        points.set(0, new Double[]{entity.getX()+entity.getSizeX()/2,entity.getY()+entity.getSizeX()/2,0.0});

        for (int i = 1; i < points.size(); i++) {



            if (Main.getDistance(points.get(i)[0], points.get(i)[1], points.get(i-1)[0], points.get(i-1)[1]) > getSectionLength()) {
                double factor = Math.sqrt(Math.pow(points.get(i)[0] - points.get(i-1)[0], 2) + Math.pow(points.get(i)[1] - points.get(i-1)[1], 2)) + 1;

                double magX = getSectionLength() * (points.get(i)[0] - points.get(i-1)[0]) / factor;
                double magY = getSectionLength() * (points.get(i)[1] - points.get(i-1)[1]) / factor;
                points.set(i, new Double[]{magX + points.get(i-1)[0]+(Main.random.nextInt(randomness)-(Main.random.nextInt( randomness)))/Settings.get("fps")
                        , magY + points.get(i-1)[1]+(Main.random.nextInt(randomness)-(Main.random.nextInt(randomness)))/Settings.get("fps")});
            }

            points.set(i, new Double[]{points.get(i)[0]+xGravity/Settings.getD("fps"), points.get(i)[1]+gravity/Settings.getD("fps")});
        }



    }

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
