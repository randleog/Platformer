

public class LevelsMenuButton extends MenuButton {



    public LevelsMenuButton(int x, int y, int width, int height) {
        super(x,y,width,height, "level menu");
    }



    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;

        }
    }
}
