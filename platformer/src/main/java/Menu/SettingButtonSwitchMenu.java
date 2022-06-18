package Menu;

import Main.Main;
import Menu.Menu;
import Util.Settings;
import Util.SoundLoader;

public class SettingButtonSwitchMenu extends MenuElement {


    private String menu;

    public SettingButtonSwitchMenu(int x, int y, int width, int height, String text,String choice, String menu, TextType type) {
        super(x,y,width,height, text, type);

        this.choice = choice;



        this.menu = menu;
    }




    public void tick() {

        updateMouse();


        click();

    }



    private void click() {
        if (Main.mouseClicked && mouseOver) {
            Settings.put(key, choice);
            SoundLoader.playButtonPress();
            Main.mouseClicked = false;
            Menu.switchMenu(menu);
        }
    }





}
