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
    protected double addX = 0;

    protected int mouseTicks = 0;
    public static final double MAX_TIME = 0.15;

    public static final double BUTTON_EXTEND = 0.25;

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

    public void setAddX(double addX) {
        this.addX = addX;
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
    protected double getRenderX() {
        double width = Main.interpolate(0,1, MAX_TIME, (mouseTicks*1.0/Settings.get("fps")))*BUTTON_EXTEND*this.width;
        return Main.correctUnit(x+addX + ((x > Main.DEFAULT_WIDTH_MAP/2) ? -width : 0));
    }

    protected double getRenderWidth() {
        double width = Main.interpolate(0,1, MAX_TIME, (mouseTicks*1.0/Settings.get("fps")))*BUTTON_EXTEND*this.width;

        return Main.correctUnit(this.width + width);
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
    protected void updateMouseTicks() {
        if (mouseOver) {
            mouseTicks++;
            mouseTicks = (int)Math.min(mouseTicks, MAX_TIME*Settings.get("fps"));
        } else {
            mouseTicks--;
            mouseTicks = Math.max(mouseTicks, 0);
        }

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
        g.fillRect(getRenderX(), getRenderY(), getRenderWidth(),Main.correctUnit(height));
        g.setFill(Color.WHITE);


        String text = this.text;
        if (textType == TextType.choice) {
            text = this.choice;
        }

        if (!(textType == TextType.hide)) {
            g.setFont(new Font(Settings.FONT,Main.correctUnit(25)));
            g.fillText(text, getRenderX() + Main.correctUnit( + 20), getRenderY()  +Main.correctUnit( + height / 2.0));
        }
    }



    protected void updateMouse() {
        updateMouseTicks();
        if (hideButton)  {
            return;
        }
        if (Main.mouseUsed) {
            mouseOver = false;
            return;
        }

        mouseOver = (Main.mouseX > getRenderX()
                && Main.mouseX < getRenderX() + getRenderWidth()
                && Main.mouseY > getRenderY()
                && Main.mouseY < getRenderY()+Main.correctUnit(this.height));

        if (mouseOver) {
            Main.mouseUsed = true;
        }

    }



}
