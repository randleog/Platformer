package Map;

import GameControl.TimedSound;
import Main.Main;
import Util.ImageLoader;
import Util.Settings;
import Util.SoundLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;


public class MetaballFrame extends GameEntity {

    private static final double SPLASH_HEIGHT = 150;
    private static final double SPLASH_HEIGHT_EXTRA = 10;
    private static final double SPLASH_WIDTH = 150;
    private TimedSound bubbles = new TimedSound(50);


    public static final double PARTICLE_SPEED = 5.0;
    private static final double SPLASH_TIME = 0.5;
    private static final double SPLASH_INTERVAL =0.4;
    private static final double SPLASH_COUNT = 2;
    private static final int PARTICLE_RESOLUTION = 2;
    private double lastSplashTime = 0;

    private double lastSplashX = 0;
    private double lastSplashY = 0;

    double lastSplashT = 0;
    private static final double PARTICLE_INTERVAL = 40;


    private ArrayList<ControlledParticle> particles = new ArrayList<>();

    private boolean isLiquid = false;
    private Liquid liquid;
    Image lastSplash = null;

    public MetaballFrame(double x, double y, Map map, double sizeX, double sizeY) {
        super(x, y, map, InputAction.Swim, FillType.Color, 1);




    }


    public MetaballFrame(double x, double y, Map map, double sizeX, double sizeY, Liquid liquid) {
        super(x, y, map, InputAction.Swim, FillType.Color, 1);
        this.isLiquid = true;
        this.liquid = liquid;
        this.color = liquid.color;



    }


    @Override
    public void tick() {

       ArrayList<ControlledParticle> removeParticles = new ArrayList<>();
        for (ControlledParticle particle : particles) {
            particle.tick();

            if (particle.getY() > liquid.getY()+SPLASH_HEIGHT_EXTRA) {
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
    }


    public void splash(double x, GameEntity entity) {

        if (!isLiquid) {
            System.out.println("im not a liquid");
            return;
        }



        for (int i = 0; i < Math.abs(entity.getVelY()) * SPLASH_COUNT; i++) {

            double size = 30;
            ControlledParticle particle = new ControlledParticle(x+ Main.random.nextInt(50)-25, liquid.getY() - 30, size, size, ImageLoader.splash, 5 * Settings.get("fps"), 0.5, true);

            particle.setVelX(Main.random.nextDouble(PARTICLE_SPEED*0.15) - (PARTICLE_SPEED / 4)*0.15);
            particle.setVelY(Main.random.nextDouble(PARTICLE_SPEED*Math.abs(entity.getVelY())*0.04) - PARTICLE_SPEED*Math.abs(entity.getVelY())*0.08);

            particles.add(particle);
        }


    }

    public void render(GraphicsContext g) {

        if (particles.size() < 1) {
            return;
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


        if (x > liquid.getX() && liquid.getX() < liquid.getX()+liquid.getSizeX()) {
            x = x+liquid.getGrid()/2.0;
            double pos = (x-liquid.getX())/liquid.getGrid();
            if (pos > liquid.getWaves().length-1 && pos > 0) {
                return liquid.getWaves()[(int)pos-1].getAmplitude()+liquid.getY();
            }
            return Main.interpolate(liquid.getWaves()[(int)pos].getAmplitude(), liquid.getWaves()[(int)pos+1].getAmplitude(), 1, pos-(int)pos)+liquid.getY();
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
                if (isLiquid) {
                    total += 45 * (Main.correctUnit(100) / Main.getDistance(actualX, actualY, actualX, surfaceY));
                }
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




}
