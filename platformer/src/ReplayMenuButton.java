

public class ReplayMenuButton extends MenuButton {



    public ReplayMenuButton(int x, int y, int width, int height) {
        super(x,y,width,height, "replay menu");
    }



    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;
            Main.switchMenu(Main.replayMenu);
        }
    }
}
