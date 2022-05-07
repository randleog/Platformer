import java.util.ArrayList;

public class SwitchMenuButton extends MenuButton {


    private ArrayList<MenuButton> menu;

    public SwitchMenuButton(int x, int y, int width, int height, String text, ArrayList<MenuButton> menu) {
        super(x,y,width,height, text);

        this.menu = menu;
    }



    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;
            Main.switchMenu(menu);
        }
    }
}
