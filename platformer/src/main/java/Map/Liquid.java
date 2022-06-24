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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class Liquid extends GameEntity {


    private static final double DISTORT = 3;
    private int grid = 50;

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


    private double SPREAD = 0.003;


    private static final double SPLASH_TIME = 0.5;
    private static final double SPLASH_INTERVAL =0.4;

    private static final int DRIP_LENGTH = 500;
    private static final int DRIP_SCAN = 25;

    private static final double DRIP_FALLOUT = 0.7;


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

    private String name;

    double spring;
    double dampening;


    public Liquid(double x, double y, Map map, double sizeX, double sizeY, InputAction action, Color color, int gridSize, double spread, double spring, double dampening, String name) {
        super(x, y, map, action, FillType.Color, 1);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = color;

        this.spring = spring;
        this.dampening =dampening;

        this.grid = gridSize;
        this.SPREAD = spread;
        this.name = name;


        bubbles = new TimedSound(50);

        loadHitbox();


        leftDeltas = new float[waves.length];
        Arrays.fill(leftDeltas, 0);

        rightDeltas = new float[waves.length];
        Arrays.fill(rightDeltas, 0);


    }


    @Override
    protected void loadHitbox() {
        hitbox = new ArrayList<>();
        hitbox.add(new Square(this.x, this.y, this.sizeX, this.sizeY, parallax, action));



        loadDrip();
    }

    private void loadDrip() {
        if ((int) (sizeX / grid) + 2 < 1) {
            return;
        }

        waves = new Wave[(int) (sizeX / grid) + 2];
        for (int i = 0; i < waves.length; i++) {

            int actualX = i-1;
            double currentY = 0;
            boolean loop = true;
            while (loop) {


                if (map.getActions(new Wall(this.x + actualX * grid, this.y+currentY, map, grid
                        , this.sizeY+DRIP_SCAN, InputAction.Default, FillType.Image
                        , 1)).contains(InputAction.Up)) {

                    loop = false;
                } else if (currentY > DRIP_LENGTH) {
                    loop = false;
                } else {
                    currentY+=DRIP_SCAN;
                }


            }
            waves[i] = new Wave(spring, dampening, currentY);
        }
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

    private boolean hasStuck = false;





        public void tick() {




        //until it is touching something, gravity.

        if (!canJump) {
            canJump = false;
            gravity();
            loadDrip();
            physics();
            collision();

        } else {
            if (!hasStuck) {
                loadHitbox();
                hasStuck = true;
            }
        }


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

                if (waves[i].getDripLength() > 100) {

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

                if (waves[i].getDripLength() > 1) {
                    Stop[] stops = new Stop[] { new Stop(0, color),new Stop(DRIP_FALLOUT, color), new Stop(1, Color.rgb((int)(color.getRed()*255),(int)(color.getGreen()*255),(int)(color.getBlue()*255),0))};
                    LinearGradient fade = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
                    g.setFill(fade);
                    g.fillRect(x + actualPos *Main.correctUnit(grid), y+getRenderSizeY(), Main.correctUnit(grid), Main.correctUnit(waves[i].getDripLength()));
                }

                //    g.setStroke(Color.color(1, 1, 1));
                //       g.setLineWidth(Main.Main.correctUnit(2));
                //      g.strokeLine(xs[1], ys[1], xs[2], ys[2]);
                //      g.strokeLine(xs[2], ys[2], xs[3], ys[3]);
                //      g.strokeLine(xs[3], ys[3], xs[4], ys[4]);



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
            g.setGlobalAlpha(color.getOpacity());
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
                        image.getPixelWriter().setColor(x, y, Color.color(color.getRed(), color.getGreen(), color.getBlue()));
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
        g.setGlobalAlpha(color.getOpacity());

        g.drawImage(image, Main.correctUnit(furthestLeft-map.cameraX),Main.correctUnit(lowest-SPLASH_HEIGHT-map.cameraY), Main.correctUnit(SPLASH_WIDTH), Main.correctUnit(SPLASH_HEIGHT+SPLASH_HEIGHT_EXTRA));

        g.restore();
    }



    public String toString() {
        String line = name + " " + (int) x + " " + (int) y + " " + (int) sizeX + " " + (int) sizeY;

        return line;
    }
}
