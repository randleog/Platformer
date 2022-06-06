import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ToggleButton extends MenuButton {




    public ToggleButton(int x, int y, int width, int height,String text) {
        super(x,y,width,height, text);

    }



    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;
            Main.settings.put(text, -Main.settings.get(text));

        }
    }

    @Override
    public void render(GraphicsContext g) {
        if (Main.settings.get(text) == -1) {
           // g.setFill(Color.color(1, 0, 0, 0.5));
        } else {

            g.setFill(Color.color(0, 1, 0, 0.3));
            g.fillRect(Main.correctUnit(x), Main.correctUnit(y), Main.correctUnit(width),Main.correctUnit(height));
        }


        super.render(g);

    }
}
