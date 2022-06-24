package GameControl;

import Map.InputAction;
import Map.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import Main.Main;

import Util.Settings;
import Menu.Menu;

import java.awt.Polygon;

public class Square {

    private double x;
    private double y;
    private double sizeX;
    private double sizeY;

    private double rotation;

    private double parallax;

    private boolean flagged = false;

    public static final Square DEFAULT = new Square(0, 0, 0, 0, 1, InputAction.Default);


    private InputAction action;

    public Square(double x, double y, double xSize, double ySize, double parallax, InputAction action) {
        this.x = x;
        this.y = y;
        this.sizeX = xSize;
        this.sizeY = ySize;

        this.action = action;
        rotation = -1;

        this.parallax = parallax;


    }

    public Square(double x, double y, double xSize, double ySize, double parallax, InputAction action, double rotation) {
        this.x = x;
        this.y = y;
        this.sizeX = xSize;
        this.sizeY = ySize;

        this.action = action;
        this.rotation = rotation;
        this.parallax = parallax;
    }


    public double getRotation() {
        return rotation;
    }


    public double getParallax() {
        return parallax;
    }

    public InputAction getAction() {
        return action;
    }

    public double getSizeX() {
        return sizeX;
    }

    public double getSizeY() {
        return sizeY;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


    public boolean intersect(Square entity) {

        double x2 = entity.getX();
        double y2 = entity.getY();
        double sizeX2 = entity.getSizeX();
        double sizeY2 = entity.getSizeY();


        if (x + sizeX > x2 && x < x2 + sizeX2
                && y + sizeY > y2 && y < y2 + sizeY2) {
            if (this.rotation == -1) {

                return true;
            } else if (rotation == 225) {
                Polygon polygon = new Polygon(new int[]{(int) x, (int) x, (int) x + (int) sizeX}, new int[]{(int) y, (int) y + (int) sizeY, (int) y + (int) sizeY}, 3);

                return polygon.intersects(entity.getX(), entity.getY(), entity.getSizeX(), entity.getSizeY());
            } else if (rotation == 315) {
                Polygon polygon = new Polygon(new int[]{(int) x, (int) x + (int) sizeX, (int) x + (int) sizeX}, new int[]{(int) y + (int) sizeY, (int) y + (int) sizeY, (int) y}, 3);

                return polygon.intersects(entity.getX(), entity.getY(), entity.getSizeX(), entity.getSizeY());
            }else if (rotation == 405) {
                Polygon polygon = new Polygon(new int[]{(int) x, (int) x , (int) x + (int) sizeX}, new int[]{(int) y + (int) sizeY, (int) y , (int) y}, 3);

                return polygon.intersects(entity.getX(), entity.getY(), entity.getSizeX(), entity.getSizeY());
            }else if (rotation == 495) {
                Polygon polygon = new Polygon(new int[]{(int) x, (int) x+ (int) sizeX , (int) x+ (int) sizeX }, new int[]{(int) y , (int) y , (int) y+ (int) sizeY}, 3);

                return polygon.intersects(entity.getX(), entity.getY(), entity.getSizeX(), entity.getSizeY());
            }
        }
        return false;
    }

    public double getRenderX(double cameraX) {
        double x = Main.correctUnit(this.x) - Main.correctUnit(cameraX * parallax);

        return x;

    }


    public double getRenderY(double cameraY) {
        double y = Main.correctUnit(this.y) - Main.correctUnit(cameraY * parallax);

        return y;

    }

    public double getRenderSizeX() {
        double sizeX = Main.correctUnit(this.sizeX);
        return sizeX;
    }

    public double getRenderSizeY() {
        double sizeY = Main.correctUnit(getSizeY());
        return sizeY;
    }


    public void flag() {
        flagged = true;
    }


    public void render(GraphicsContext g, double cameraX, double cameraY, Player player) {

        if (Settings.get("debug") == 1) {
            double x = getRenderX(cameraX);
            double y = getRenderY(cameraY);


            if (!(rotation == -1)) {
                g.setFill(Color.rgb(255, 40, 40, 0.3));

                if (rotation == 225) {

                    g.fillPolygon(new double[]{x, x, x + Main.correctUnit(sizeX)}, new double[]{y, y + Main.correctUnit(sizeY), y + Main.correctUnit(sizeY)}, 3);
                } else if (rotation ==315) {

                    g.fillPolygon(new double[]{x, x + Main.correctUnit(sizeX), x + Main.correctUnit(sizeX)}, new double[]{y + Main.correctUnit(sizeY), y + Main.correctUnit(sizeY), y}, 3);
                }else if (rotation == 405) {


                    g.fillPolygon(new double[]{x, x, x + Main.correctUnit(sizeX)}, new double[]{y + Main.correctUnit(sizeY), y , y}, 3);
                }else if (rotation == 495) {


                    g.fillPolygon(new double[]{x, x + Main.correctUnit(sizeX), x + Main.correctUnit(sizeX)}, new double[]{y, y, y+ Main.correctUnit(sizeY)}, 3);
                }

            } else {
                g.setFill(Color.rgb(255, 40, 40, 0.3));
                g.fillRect(x, y, getRenderSizeX(), getRenderSizeY());

            }

            if (flagged) {
                g.setFill(Color.RED);
            } else {
                g.setFill(Color.BLACK);
            }

            g.setFill(Color.WHITE);
            g.setFont(new Font("monospaced", 15));
    //        g.fillText(action.toString() + " x:" + String.format("%.4f", this.x) + " y:" + String.format("%.4f", this.y), x, y);
            flagged = false;
        } else if (Menu.currentMenu.equals("editor")) {
            double x = getRenderX(cameraX);
            double y = getRenderY(cameraY);

            g.setFill(Color.WHITE);
            g.setFont(new Font("monospaced", 15));
       //     g.fillText(action.toString() + " x:" + String.format("%.4f", this.x) + " y:" + String.format("%.4f", this.y), x, y);
        }
    }


}
