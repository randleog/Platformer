import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ToggleButton extends MenuButton {

    private Color selectColor;


    public ToggleButton(int x, int y, int width, int height,String text) {
        super(x,y,width,height, text, TextType.normal);

        selectColor =  Color.color(0, 1, 0, 0.3);

    }


    public ToggleButton(int x, int y, int width, int height,String text, Color selectColor) {
        super(x,y,width,height, text, TextType.normal);
        this.selectColor =  selectColor;
    }


    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;
            Settings.put(key, -Settings.get(key));

        }
    }

    @Override
    public void render(GraphicsContext g) {
        if (Settings.get(key) == -1) {
           // g.setFill(Color.color(1, 0, 0, 0.5));
        } else {

            g.setFill(selectColor);
            g.fillRect(Main.correctUnit(x+insetWidth), Main.correctUnit(y+insetWidth), Main.correctUnit(width-insetWidth*2),Main.correctUnit(height-insetWidth*2));

        }


        super.render(g);

    }
}
