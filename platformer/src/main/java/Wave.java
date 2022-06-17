public class Wave {

    private double amplitude;

    private double velY;
    private double velX;


    private double acceleration = 0;



    private static final double SPRING_CONSTANT = 0.025;
    private static final double DAMPENING_FACTOR = 0.09;


    private static final double SPEED = 144;

    private double targetHeight = 10;



    private static final double MASS = 1; // spring constant/mass = current constant

    public Wave() {
        this.amplitude = targetHeight;
        this.velY = 0;
        this.velX = 0;
    }



    public void tick(Wave... additional) {

        double displacement = amplitude-targetHeight;
        double accel = -SPRING_CONSTANT*displacement -DAMPENING_FACTOR*velY;

     //   velY = velY+Main.random.nextDouble(0.1)-0.025;

        amplitude+=Main.correctFPS(velY);
        velY+=Main.correctFPS(accel);



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
