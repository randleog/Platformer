package Menu;

import Main.Main;

public class ExitButton extends MenuElement {




    public ExitButton(int x, int y, int width, int height, String text) {
        super(x,y,width,height, text, MenuElement.TextType.normal);

    }



    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseClicked && mouseOver) {
            Main.mouseClicked = false;
            Main.planExit();
        }
    }
}
