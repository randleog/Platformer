

public class DecreaseFpsButton extends MenuButton {



    public DecreaseFpsButton(int x, int y, int width, int height) {
        super(x,y,width,height, "-");
    }



    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;
            Main.decreaseFPS();

        }
    }
}
