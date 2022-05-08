

public class SpeedupButton extends MenuButton {

    private double amount;
    private Map map;

    public SpeedupButton(int x, int y, int width, int height,String text, double amount, Map map) {
        super(x,y,width,height, text);
        this.amount = amount;
        this.map = map;
    }



    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;
            map.increaseSpeed(amount);
        }
    }
}
