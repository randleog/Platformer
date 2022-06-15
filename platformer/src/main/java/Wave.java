public class Wave {

    private double amplitude;

    private double velY;
    private double velX;


    public Wave() {
        this.amplitude = 0;
        this.velY = 0;
        this.velX = 0;
    }
    public Wave(double amplitude) {
        this.amplitude = amplitude;
        this.velY = 0;
        this.velX = 0;
    }


    public void tick(Wave... additional) {


        factorVelY(additional);
        velY = velY +10/ Settings.getD("fps");
        velY = velY * Math.pow(0.1, 1.0 / Settings.getD("fps"));



        if (amplitude > 0) {
            if (velY > 0) {
                velY = velY * Math.pow(0.01, 1.0 / Settings.getD("fps"));
            }
            amplitude = amplitude * Math.pow(0.005, 1.0 / Settings.getD("fps"));
        }
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
