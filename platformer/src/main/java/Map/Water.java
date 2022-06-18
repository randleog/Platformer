package Map;

import GameControl.Square;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import Main.Main;
import Util.ImageLoader;
import Util.Settings;
import Util.SoundLoader;


import GameControl.TimedSound;
public class Water extends GameEntity {


    private static final double DISTORT = 3;
    private static final int grid = 50;

    private static final int PARTICLE_RESOLUTION = 2;

    private static final double WAVE = 30;

    private static final double WAVE_DEPTH = 50;


    private static final double SPLASH_COUNT = 2;


    private int currentTick = 0;


    private static final double BUBBLE_WIDTH = 25;


    private Wave[] waves;

    private float[] leftDeltas;
    private float[] rightDeltas;

    public static final double PARTICLE_SPEED = 5.0;


    private static final double SPREAD = 0.003;


    private static final double SPLASH_TIME = 0.5;
    private static final double SPLASH_INTERVAL =0.4;


    private double lastSplashTime = 0;


    double lastSplashT = 0;
    private static final double PARTICLE_INTERVAL = 40;

    Image lastSplash = null;

    private static final double SPLASH_HEIGHT = 150;
    private static final double SPLASH_HEIGHT_EXTRA = 10;
    private static final double SPLASH_WIDTH = 150;

    private ArrayList<Double[]> splashes = new ArrayList<>();


    private ArrayList<ControlledParticle> particles = new ArrayList<>();

    private double lastSplashX = 0;
    private double lastSplashY = 0;

    private TimedSound bubbles;


    public Water(double x, double y, Map map, double sizeX, double sizeY, double parallax) {
        super(x, y, map, InputAction.Swim, FillType.Color, parallax);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(0.4, 0.8, 1, 0.3);
        this.image = ImageLoader.wallTile;
        if (sizeX < 10 && sizeY < 10) {
            System.out.println("wall is too small");
        }
        bubbles = new TimedSound(50);

        waves = new Wave[(int) (sizeX / grid) + 2];
        for (int i = 0; i < waves.length; i++) {
            waves[i] = new Wave(0.02, 0.09);
        }


        leftDeltas = new float[waves.length];
        Arrays.fill(leftDeltas, 0);

        rightDeltas = new float[waves.length];
        Arrays.fill(rightDeltas, 0);

        loadHitbox();
    }


    @Override
    protected void loadHitbox() {
        hitbox = new ArrayList<>();
        hitbox.add(new Square(this.x, this.y, this.sizeX, this.sizeY, parallax, InputAction.Swim));
    }


    @Override
    public boolean isWall() {
        return true;
    }


    public void splash(double x, GameEntity entity) {



        for (int i = 0; i < Math.abs(entity.getVelY()) * SPLASH_COUNT; i++) {

            double size = 30;
            ControlledParticle particle = new ControlledParticle(x+ Main.random.nextInt(50)-25, this.y - 30, size, size, ImageLoader.splash, 5 * Settings.get("fps"), 0.5);

            particle.setVelX(Main.random.nextDouble(PARTICLE_SPEED*0.15) - (PARTICLE_SPEED / 4)*0.15);
            particle.setVelY(Main.random.nextDouble(PARTICLE_SPEED*Math.abs(entity.getVelY())*0.04) - PARTICLE_SPEED*Math.abs(entity.getVelY())*0.08);

            particles.add(particle);
        }


    }

    public void tick() {

        ArrayList<ControlledParticle> removeParticles = new ArrayList<>();

        for (ControlledParticle particle : particles) {
            particle.tick();

            if (particle.getY() > this.y+SPLASH_HEIGHT_EXTRA) {
                particle.setRemove();
            }

            if (particle.isFinished()) {
                removeParticles.add(particle);
            }


        }

        for (ControlledParticle particle : removeParticles) {
            bubbles.play(SoundLoader.bubble, SoundLoader.getRandomVolume()-0.5, SoundLoader.getRandomSpeed());
            particles.remove(particle);


        }

        if (Settings.get("graphics") ==0) {
            return;
        }

            tickWaves();
            tickSplashes();


    }

    private void tickSplashes() {

        ArrayList<Double[]> removeSplashes = new ArrayList<>();

        for (Double[] splash : splashes) {
            splash[2]++;
            if (splash[2] / Settings.getD("fps") > SPLASH_TIME) {

                removeSplashes.add(splash);
            }
        }


        for (Double[] splash : removeSplashes) {
            splashes.remove(splash);
        }

        if (map.player == null) {
            return;
        }


        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < waves.length; i++) {
                if (i > 0) {
                    leftDeltas[i] = (float) (SPREAD * (waves[i].getAmplitude() - waves[i - 1].getAmplitude()));
                    waves[i - 1].addVelY(Main.correctFPS(leftDeltas[i]));
                }

                if (i < waves.length - 1) {
                    rightDeltas[i] = (float) (SPREAD * (waves[i].getAmplitude() - waves[i + 1].getAmplitude()));
                    waves[i + 1].addVelY(Main.correctFPS(rightDeltas[i]));
                }
            }

            for (int i = 0; i < waves.length; i++) {
                if (i > 0) {
                    waves[i - 1].addAmplitude(Main.correctFPS(leftDeltas[i]));
                }
                if (i < waves.length - 1) {
                    waves[i + 1].addAmplitude(Main.correctFPS(rightDeltas[i]));
                }
            }
        }


        if (this.getMainShape().intersect(map.player.getMainShape())) {
            if (Math.abs(map.player.getVelX()) > 1 || Math.abs(map.player.getVelY()) > 1) {


                if (new Square(this.x, this.y - grid, this.sizeX, WAVE_DEPTH, 2, InputAction.Default).intersect(map.player.getMainShape())) {

                    Wave wave = waves[(int) (((map.playerX - this.x) + 1) / grid) + 1];


                    wave.addVelY(map.player.getVelY() * 0.5);
                    wave.addVelY(Math.abs(map.player.getVelX()) * 0.2);

                }

                if (Math.sqrt(Math.pow(map.player.getVelX(), 2) + Math.pow(map.player.getVelY(), 2)) > 1) {

                    if (((System.currentTimeMillis() - lastSplashTime) / Settings.get("fps") > SPLASH_INTERVAL)) {
                        lastSplashTime = System.currentTimeMillis();

                        splashes.add(new Double[]{map.playerX + Main.random.nextDouble(map.player.getSizeX()), map.playerY + Main.random.nextDouble(map.player.getSizeY()), 0.0});

                        if (!this.getMainShape().intersect(new Square(splashes.get(splashes.size() - 1)[0], splashes.get(splashes.size() - 1)[1], 1, 1, 1, InputAction.Default))) {
                            splashes.remove(splashes.size() - 1);

                        }
                    }
                }
            }
        }
    }


    private void tickWaves() {
        currentTick++;


        for (int i = 0; i < waves.length; i++) {
            if (map.screenInersect(new Square(this.x + i * grid, this.y, grid, grid, 1, InputAction.Default))) {

                if (i == 0) {
                    waves[i].tick(waves[1]);
                } else if (i == waves.length - 1) {
                    waves[i].tick(waves[waves.length - 2]);
                } else {
                    waves[i].tick(waves[i - 1], waves[i + 1]);
                }


            }

        }


    }


    public void render(GraphicsContext g) {


        if (Settings.get("graphics") ==0) {
            renderSquare(g);
            return;
        }


        double x = getRenderX();
        double y = getRenderY();

        for (Double[] splash : splashes) {
            double currentAnimate = Main.correctUnit(Main.interpolate(0, BUBBLE_WIDTH, SPLASH_TIME * Settings.get("fps"), splash[2]));
            currentAnimate = Math.min(BUBBLE_WIDTH, currentAnimate);
            g.setLineWidth(Main.correctUnit(1));
            g.setStroke(Color.color(1, 1, 1, 1 - ((currentAnimate) / BUBBLE_WIDTH)));
            g.strokeRect(Main.correctUnit(splash[0] - map.cameraX) - currentAnimate / 2
                    , Main.correctUnit(splash[1] - map.cameraY) - currentAnimate / 2
                    , currentAnimate
                    , currentAnimate);
        }

        if ((sizeX / grid) < 2) {
            return;
        }
        if (waves.length < 2) {
            return;
        }


        g.setFill(color);


        for (int i = 1; i < waves.length - 1; i++) {
            int actualPos = i - 1;


            if (map.screenInersect(new Square(this.x + actualPos * grid, this.y - DISTORT, grid, DISTORT, 1, InputAction.Default))) {
                g.setFill(color);


                double[] ys = new double[]{y + getRenderSizeY()
                        , y + Math.min(getRenderSizeY(), Main.correctUnit(waves[i - 1].getAmplitude()) * 3.0 / 6 + Main.correctUnit(waves[i].getAmplitude() * 3.0 / 6))
                        , y + Math.min(getRenderSizeY(), Main.correctUnit(waves[i - 1].getAmplitude()) / 6 + Main.correctUnit(waves[i].getAmplitude() * 5 / 6))
                        , y + Math.min(getRenderSizeY(), (Main.correctUnit(waves[i].getAmplitude())) * 5.0 / 6 + (Main.correctUnit(waves[i + 1].getAmplitude()) / 6))
                        , y + Math.min(getRenderSizeY(), Main.correctUnit(waves[i].getAmplitude()) * 3.0 / 6 + (Main.correctUnit(waves[i + 1].getAmplitude() * 3.0 / 6)))
                        , y + getRenderSizeY()};


                double[] xs = new double[]{(int) (x + actualPos * Main.correctUnit(grid))
                        , (int) (x + actualPos * Main.correctUnit(grid))
                        , x + actualPos * Main.correctUnit(grid) + Main.correctUnit(grid) / 3
                        , x + actualPos * Main.correctUnit(grid) + Main.correctUnit(grid) * 2.0 / 3
                        , (int) (x + actualPos * Main.correctUnit(grid) + Main.correctUnit(grid))
                        , (int) (x + actualPos * Main.correctUnit(grid) + Main.correctUnit(grid))};


               g.fillPolygon(xs, ys, 6);

            //    g.setStroke(Color.color(1, 1, 1));
         //       g.setLineWidth(Main.Main.correctUnit(2));
          //      g.strokeLine(xs[1], ys[1], xs[2], ys[2]);
          //      g.strokeLine(xs[2], ys[2], xs[3], ys[3]);
          //      g.strokeLine(xs[3], ys[3], xs[4], ys[4]);


            }
        }


        for (Square square : hitbox) {
            square.render(g, map.cameraX, map.cameraY, (Player) map.player);
        }

        if (Settings.get("graphics") ==0) {
            return;
        }
        if (Settings.get("graphics") <4) {
            for (ControlledParticle particle : particles) {
                particle.render(g, map.cameraX, map.cameraY);
            }
        } else {
            renderSplash(g);
        }



    }

    public void renderSplash(GraphicsContext g) {

        if (particles.size() < 1) {
            return;
        }
       // renderActual(g);
        if (lastSplash != null && System.currentTimeMillis()- lastSplashT < PARTICLE_INTERVAL) {

            g.save();
            g.setGlobalAlpha(0.3);
            g.drawImage(lastSplash,  Main.correctUnit(lastSplashX-map.cameraX),Main.correctUnit(lastSplashY - SPLASH_HEIGHT- map.cameraY), Main.correctUnit(SPLASH_WIDTH), Main.correctUnit(SPLASH_HEIGHT+SPLASH_HEIGHT_EXTRA));
            g.restore();

        } else {
            lastSplashT = System.currentTimeMillis();
            renderActual(g);
        }




    }

    public double getSurfaceY(double x) {

        if (x > this.x && x < this.x+sizeX) {
            x = x+grid/2.0;
            double pos = (x-this.x)/grid;
            if (pos > waves.length-1) {
                return waves[(int)pos].getAmplitude()+this.y;
            }
            return Main.interpolate(waves[(int)pos].getAmplitude(), waves[(int)pos+1].getAmplitude(), 1, pos-(int)pos)+this.y;
        }
        return this.sizeY;
    }



    private void renderActual(GraphicsContext g) {



        double furthestLeft = 9999999;
        double lowest = -999999;
        for (ControlledParticle particle : particles) {
            if (particle.getX()-particle.getSize() < furthestLeft) {
                furthestLeft = particle.getX()-particle.getSize();
            }

            if (particle.getY()+particle.getSize() > lowest) {
                lowest = particle.getY()+particle.getSize();
            }
        }


        int res =PARTICLE_RESOLUTION;




        WritableImage image = new WritableImage((int) Main.correctUnit(SPLASH_WIDTH/res), (int) Main.correctUnit((SPLASH_HEIGHT+SPLASH_HEIGHT_EXTRA)/res));




        for (int x = 0; x < image.getWidth(); x ++) {
            for (int y = 0; y < image.getHeight(); y ++) {






                float actualX = (float)(Main.reverseUnit(x*res)+furthestLeft);
                float actualY = (float)(Main.reverseUnit(y*res)+lowest-SPLASH_HEIGHT-SPLASH_HEIGHT_EXTRA);

                double surfaceY = getSurfaceY(actualX);

                int total = 0;
                for (ControlledParticle particle : particles) {
                    total += 45 * (Main.correctUnit(particle.getSize()) / Main.getDistance(actualX, actualY , particle.getX(), particle.getY()));
                   // System.out.println(Main.Main.getDistance(actualX*res, actualY*res, particle.getX(), particle.getY()));
                    // total += 20 * (particle.getSize() / getDistance(x*res, y*res, particle.getX(), particle.getY()));
                }


             //   if (actualY < this.y) {
                    total+= 45 * (Main.correctUnit(100) / Main.getDistance(actualX, actualY , actualX, surfaceY));
           //     }

             //   if (actualY > this.y) {

           //     }

                if (total > 2550) {
                    total = 2550;
                }



                    if (total > 900) {
                         if (!(actualY+SPLASH_HEIGHT_EXTRA+1 > surfaceY)) {
                             image.getPixelWriter().setColor(x, y, Color.color(0.4, 0.8, 1));
                         }
//
                   }



            }
        }



        lastSplash = image;







        lastSplashX =furthestLeft;
        lastSplashY = lowest;

       // g.save();
   //     g.setGlobalBlendMode(BlendMode.ADD);
        g.save();
        g.setGlobalAlpha(0.3);

        g.drawImage(image, Main.correctUnit(furthestLeft-map.cameraX),Main.correctUnit(lowest-SPLASH_HEIGHT-map.cameraY), Main.correctUnit(SPLASH_WIDTH), Main.correctUnit(SPLASH_HEIGHT+SPLASH_HEIGHT_EXTRA));

        g.restore();
    }



    public String toString() {
        String line = "water " + (int) x + " " + (int) y + " " + (int) sizeX + " " + (int) sizeY;

        return line;
    }
}
