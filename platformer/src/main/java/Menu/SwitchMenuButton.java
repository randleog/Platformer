package Menu;

import Main.Main;
import Menu.Menu;
import Util.SoundLoader;

public class SwitchMenuButton extends MenuElement {


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
        if (Main.mouseClicked && mouseOver) {
            SoundLoader.playButtonPress();
            Main.mouseClicked = false;
            Menu.switchMenu(menu);
        }
    }
}
