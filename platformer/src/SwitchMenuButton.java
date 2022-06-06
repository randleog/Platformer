import java.util.ArrayList;

public class SwitchMenuButton extends MenuButton {


    private String menu;

    public SwitchMenuButton(int x, int y, int width, int height, String text, String menu) {
        super(x,y,width,height, text, TextType.normal);

        this.menu = menu;
    }



    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;
            Menu.switchMenu(menu);
        }
    }
}
