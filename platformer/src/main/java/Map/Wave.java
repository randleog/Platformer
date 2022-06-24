package Map;

import GameControl.Square;
import Main.Main;
import Util.Settings;


public class Wave {

    private double amplitude;

    private double velY;
    private double velX;


    private double acceleration = 0;



    private double spring = 0.02;
    private double dampening = 0.09;


    private static final double SPEED = 144;

    private double targetHeight = 10;

    private double dripLength = 0;



    private static final double MASS = 1; // spring constant/mass = current constant

    public Wave(double spring, double dampening, double dripLength) {
        this.spring = spring;
        this.dampening = dampening;
        this.dripLength = dripLength;
        this.amplitude = targetHeight;

        this.velY = 0;
        this.velX = 0;
    }

    public void setDripLength(double dripLength) {
        this.dripLength = dripLength;
    }

    public double getDripLength() {

        return dripLength;
    }


    public void tick(Wave... additional) {



        double displacement = amplitude-targetHeight;
        double accel = -spring *displacement - dampening *velY;

     //   velY = velY+Main.Main.random.nextDouble(0.1)-0.025;

        amplitude+= Main.correctFPS(velY);
        velY+= Main.correctFPS(accel);



    }



    private void factorVelY(Wave... additional) {
        double totalVel = velY;
        if (additional.length >0) {
            for (int i = 0; i < additional.length; i++) {
                totalVel = totalVel + (additional[i].getVelY() / additional.length);
            }
            totalVel = totalVel/2;
        }

        amplitude+=totalVel;
    }


    public void addVelY(double add) {
        velY+=add;
    }

    public void addVelX(double add) {
        velX+=add;
    }

    public double getVelY() {
        return velY;
    }

    public double getVelX() {
        return velX;
    }



    public void setVelY(double amplitude) {
        this.amplitude = amplitude;
    }
    public void setVelX(double amplitude) {
        this.amplitude = amplitude;
    }


    public void addAmplitude(double amplitude) {
        this.amplitude += amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public double getAmplitude() {
        return amplitude;
    }


}
