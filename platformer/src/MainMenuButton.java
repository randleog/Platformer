

public class MainMenuButton extends MenuButton {



    public MainMenuButton(int x, int y, int width, int height, String text) {
        super(x,y,width,height, text);
    }



    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;
            Main.switchMenu(Main.mainMenu);
        }
    }
}
