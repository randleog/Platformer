import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SettingButton extends MenuButton {

    private Color selectColor;


    public SettingButton(int x, int y, int width, int height,String text, String choice, MenuButton.TextType textType) {
        super(x,y,width,height, text, textType);
        this.choice = choice;

        selectColor =  Color.color(0, 1, 0, 0.3);

    }


    public SettingButton(int x, int y, int width, int height,String text, String choice, MenuButton.TextType textType, Color selectColor) {
        super(x,y,width,height, text, textType);
        this.choice = choice;
        this.selectColor =  selectColor;

    }


    public void tick() {
        if (hideButton) {
            return;
        }
        updateMouse();

        click();

    }



    private void click() {
        if (hideButton) {
            return;
        }
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;
            Settings.put(key, choice);

        }
    }

    @Override
    public void render(GraphicsContext g) {
        if (hideButton) {
            return;
        }
        if (Settings.getStr(key).equals(choice)) {
            g.setFill(selectColor);
            g.fillRect(Main.correctUnit(x+insetWidth), Main.correctUnit(y+insetWidth), Main.correctUnit(width-insetWidth*2),Main.correctUnit(height-insetWidth*2));
        }


        super.render(g);

    }
}