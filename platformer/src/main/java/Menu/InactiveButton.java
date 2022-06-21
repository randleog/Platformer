package Menu;

import Main.Main;
import Menu.Menu;
import Util.SoundLoader;

public class InactiveButton extends MenuElement {




    public InactiveButton(int x, int y, int width, int height, String text) {
        super(x,y,width,height, text, TextType.normal);


    }




    public void tick() {

        updateMouse();




    }




}
