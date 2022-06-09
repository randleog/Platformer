import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class MenuElement {

    public enum TextType {
        normal,
        choice,
        hide;
    }

    protected String choice;

    protected int x;
    protected int y;

    protected String key;

    protected int width;
    protected int height;


    protected String text;


    protected static final double insetWidth = 10;


    protected boolean mouseOver;

    protected TextType textType;

    protected boolean hideButton;

    protected double addY = 0;


    public MenuElement(int x, int y, int width, int height, String text, TextType textType) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        mouseOver = false;
        this.textType = textType;
        this.key = text;
        hideButton = false;
    }


    public void setAddY(double addY) {
        this.addY = addY;
    }


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    protected double getRenderY() {
        return Main.correctUnit(y+addY);
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }


    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Square getSquare() {
        return new Square(x, y+addY, width, height, 1, InputAction.Default);
    }

    public String getText() {
        String text = this.text;
        if (textType == TextType.choice) {
            text = this.choice;
        }
        return text;
    }


    public void setHideButton(boolean hideButton) {
        this.hideButton = hideButton;
    }




    public void setText(String text) {

        this.text = text;
    }


    public abstract void tick();

    public void render(GraphicsContext g) {
        if (hideButton)  {
            return;
        }

        if (mouseOver) {
            g.setFill(Color.color(1, 1, 1, 0.4));

        } else {
            g.setFill(Color.color(0, 0, 0, 0.5));
        }
        g.fillRect(Main.correctUnit(x), getRenderY(), Main.correctUnit(width),Main.correctUnit(height));
        g.setFill(Color.WHITE);


        String text = this.text;
        if (textType == TextType.choice) {
            text = this.choice;
        }

        if (!(textType == TextType.hide)) {
            g.setFont(new Font(Settings.FONT,Main.correctUnit(25)));
            g.fillText(text, Main.correctUnit(x + 20), getRenderY()  +Main.correctUnit( + height / 2.0));
        }
    }



    protected void updateMouse() {
        if (hideButton)  {
            return;
        }

        mouseOver = (Main.mouseX > Main.correctUnit(this.x)
                && Main.mouseX < Main.correctUnit(this.x+this.width)
                && Main.mouseY > getRenderY()
                && Main.mouseY < getRenderY()+Main.correctUnit(this.height));

    }



}
