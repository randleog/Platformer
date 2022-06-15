import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Arrays;

public class Water extends GameEntity {


    private static final double DISTORT = 3;
    private static final int grid = 25;

    private static final double WAVE = 30;

    private static final double WAVE_DEPTH = 50;


    private int currentTick = 0;


    private static final double BUBBLE_WIDTH = 25;


    private Wave[] waves;


    private static final double SPLASH_TIME = 1;
    private static final double SPLASH_INTERVAL = 0.4;

    private double lastSplashTime = 0;

    private ArrayList<Double[]> splashes = new ArrayList<>();


    public Water(double x, double y, Map map, double sizeX, double sizeY, double parallax) {
        super(x, y, map, InputAction.Swim, FillType.Color, parallax);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(0.4, 0.8, 1, 0.3);
        this.image = ImageLoader.wallTile;
        if (sizeX < 10 && sizeY < 10) {
            System.out.println("wall is too small");
        }


        waves = new Wave[(int) (sizeX / grid)];
        for (int i = 0; i < waves.length; i++) {
            waves[i] = new Wave();
        }


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


    public void tick() {


        tickSplashes();

        tickWaves();


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

        if (this.getMainShape().intersect(map.player.getMainShape())) {
            if (Math.abs(map.player.getVelX()) > 1 || Math.abs(map.player.getVelY()) > 1) {
                for (int i = 0; i < waves.length; i++) {

                    if (new Square(this.x + i * grid, this.y - grid, grid, WAVE_DEPTH, 2, InputAction.Default).intersect(map.player.getMainShape())) {


                        waves[i].addVelY(map.player.getVelY() * 0.1);

                        if (this.x + i * grid - grid / 2.0 > map.playerX) {
                            waves[i].addVelY((Math.abs(map.player.getVelX()) * 0.1));
                        } else {
                            waves[i].addVelY(-(Math.abs(map.player.getVelX()) * 0.1));
                        }

                    }
                }
                if (Math.sqrt(Math.pow(map.player.getVelX(), 2) + Math.pow(map.player.getVelY(), 2)) > 1) {

                    if (((System.currentTimeMillis() - lastSplashTime) / Settings.get("fps") > SPLASH_INTERVAL)) {
                        lastSplashTime = System.currentTimeMillis();

                        splashes.add(new Double[]{map.playerX + Main.random.nextDouble(map.player.getSizeX()), map.playerY + Main.random.nextDouble(map.player.getSizeY()), 0.0});

                        if (!this.getMainShape().intersect(new Square(splashes.get(splashes.size() - 1)[0], splashes.get(splashes.size() - 1)[1], 1, 1, 1, InputAction.Default))) {
                            splashes.remove(splashes.size()-1);

                        }
                    }
                }
            }
        }
    }


            private void tickWaves () {
                currentTick++;


                for (int i = 0; i < (sizeX / grid); i++) {
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


                for (int i = 0; i < waves.length; i++) {
                    double value = (Main.random.nextDouble(10) - 5);

                    waves[i].addAmplitude((Main.random.nextDouble(1) - 0.5));

                }
            }


            public void render (GraphicsContext g){
                //renderSquare(g);

                double x = getRenderX();
                double y = getRenderY();

                for (Double[] splash : splashes) {
                    double currentAnimate = Main.correctUnit(Main.interpolate(0, BUBBLE_WIDTH, SPLASH_TIME * Settings.get("fps"), splash[2]));
                    currentAnimate = Math.min(BUBBLE_WIDTH, currentAnimate);
                    g.setLineWidth(Main.correctUnit(1));
                    g.setStroke(Color.color(1, 1, 1, 1 - ((currentAnimate) / BUBBLE_WIDTH) / 2.0));
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


                for (int i = 0; i < (sizeX / grid); i++) {
                    if (map.screenInersect(new Square(this.x + i * grid, this.y - DISTORT, grid, DISTORT, 1, InputAction.Default))) {
                        g.setFill(color);


                        double[] ys = new double[]{y + getRenderSizeY()
                                , y + Math.min(getRenderSizeY(), Main.correctUnit(waves[i].getAmplitude()))
                                , y + Math.min(getRenderSizeY(), Main.correctUnit(waves[i].getAmplitude()))
                                , y + Math.min(getRenderSizeY(), (Main.correctUnit(waves[i].getAmplitude())))
                                , y + Math.min(getRenderSizeY(), Main.correctUnit(waves[i].getAmplitude()))
                                , y + getRenderSizeY()};


                        double[] xs = new double[]{(int)(x + i * Main.correctUnit(grid))
                                , (int)(x + i * Main.correctUnit(grid))
                                , x + i * Main.correctUnit(grid) + Main.correctUnit(grid) / 3
                                , x + i * Main.correctUnit(grid) + Main.correctUnit(grid) * 2.0 / 3
                                , (int)(x + i * Main.correctUnit(grid) + Main.correctUnit(grid))
                                , (int)(x + i * Main.correctUnit(grid) + Main.correctUnit(grid))};

                        if (i > 0) {
                            ys = new double[]{y + getRenderSizeY()
                                    , y + Math.min(getRenderSizeY(), Main.correctUnit(waves[i-1].getAmplitude()))
                                    , y + Math.min(getRenderSizeY(), Main.correctUnit(waves[i-1].getAmplitude()) * 2.0 / 3 + Main.correctUnit(waves[i].getAmplitude() / 3))
                                    , y + Math.min(getRenderSizeY(), (Main.correctUnit(waves[i-1].getAmplitude())) / 3 + (Main.correctUnit(waves[i].getAmplitude()) * 2.0 / 3))
                                    , y + Math.min(getRenderSizeY(), Main.correctUnit(waves[i].getAmplitude()))
                                    , y + getRenderSizeY()};
                        }




                        g.fillPolygon(xs, ys, 6);


                        ys = new double[]{ys[1], ys[2],ys[3],ys[4]};
                        xs = new double[]{x + i * Main.correctUnit(grid)
                                , x + i * Main.correctUnit(grid) + Main.correctUnit(grid/3.0)
                                , x + i * Main.correctUnit(grid) + Main.correctUnit(grid*2/3.0)
                                , x + i * Main.correctUnit(grid) + Main.correctUnit(grid)};
                        g.setStroke(Color.color(1, 1, 1, 0.7));
                        g.setLineWidth(Main.correctUnit(2));
                        g.strokePolygon(xs, ys, 4);


                    }
                }


                for (Square square : hitbox) {
                    square.render(g, map.cameraX, map.cameraY, (Player) map.player);
                }


            }




            public String toString () {
                String line = "water " + (int) x + " " + (int) y + " " + (int) sizeX + " " + (int) sizeY;

                return line;
            }
        }
