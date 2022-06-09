

public class ExitButton extends MenuElement {




    public ExitButton(int x, int y, int width, int height, String text) {
        super(x,y,width,height, text, TextType.normal);

    }



    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;
            Main.exit();
        }
    }
}
