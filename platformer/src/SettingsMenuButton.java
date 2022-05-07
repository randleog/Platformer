

public class SettingsMenuButton extends MenuButton {



    public SettingsMenuButton(int x, int y, int width, int height) {
        super(x,y,width,height, "settings menu");
    }



    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;
            Main.switchMenu(Main.settingsMenu);
        }
    }
}
