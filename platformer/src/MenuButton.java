import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javax.swing.plaf.IconUIResource;

public abstract class MenuButton {

    protected int x;
    protected int y;

    protected int width;
    protected int height;


    protected String text;


    protected boolean mouseOver;


    public MenuButton(int x, int y, int width, int height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        mouseOver = false;
    }

    public String getText() {

        return text;
    }





    public void setText(String text) {

        this.text = text;
    }


    public abstract void tick();

    public void render(GraphicsContext g) {
        if (mouseOver) {
            g.setFill(Color.color(1, 1, 1, 0.5));

        } else {
            g.setFill(Color.color(0, 0, 0, 0.5));
        }
        g.fillRect(Main.correctUnit(x), Main.correctUnit(y), Main.correctUnit(width),Main.correctUnit(height));
        g.setFill(Color.WHITE);
        g.setFont(new Font(Main.correctUnit(25)));
        g.fillText(text, Main.correctUnit(x+10), Main.correctUnit(y+height/2.0));
    }



    protected void updateMouse() {

        mouseOver = (Main.mouseX > Main.correctUnit(this.x)
                && Main.mouseX < Main.correctUnit(this.x+this.width)
                && Main.mouseY > Main.correctUnit(this.y)
                && Main.mouseY < Main.correctUnit(this.y+this.height));

    }



}
