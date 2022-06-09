import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MenuSlider extends MenuElement {


    private int max;
    private int min;

    private int recommendedValue;

    private int currentVal = 0;
    private double currentFactor = 0;

    private double fontSize;


    boolean vertical = false;

    public MenuSlider(int x, int y, int width, int height, String text, String choice, int max, int min, boolean vertical) {
        super(x, y, width, height, text, TextType.normal);

        this.max = max;
        this.min = min;
        fontSize = width;
        this.choice = choice;

        currentFactor = (Settings.get(choice) * 1.0) / (max - min);
        currentVal = Settings.get(choice);


        recommendedValue = min - 1;
        this.vertical = vertical;
    }

    public MenuSlider(int x, int y, int width, int height, String text, String choice, int max, int min, boolean vertical, int recommendedValue) {
        super(x, y, width, height, text, TextType.normal);

        this.max = max;
        this.min = min;
        fontSize = width;
        this.choice = choice;

        currentFactor = (Settings.get(choice) * 1.0) / (max - min);
        currentVal = Settings.get(choice);

        this.recommendedValue = recommendedValue;

        this.x = this.x + (int) insetWidth;
        this.y = this.y + (int) insetWidth;

        this.width = this.width - (int) (insetWidth * 2);
        this.height = this.height - (int) (insetWidth * 2);
        this.vertical = vertical;
    }


    public void tick() {

        boolean changeMouse = false;
        if (mouseOver) {
            changeMouse = true;
        }

        updateMouse();

        click();

        if (changeMouse) {
            if (!mouseOver) {
                Main.mouseDown = false;
            }
        }

    }


    private void click() {
        if (Main.mouseDown && mouseOver) {
            //  Main.mouseDown = false;


            int newVal;

            if (vertical) {
                newVal = min + (int) (((Main.mouseY - getRenderY()) / Main.correctUnit((this.height))) * (max - min));
            } else {
                newVal = min + (int) (((Main.mouseX - Main.correctUnit(x)) / Main.correctUnit((this.width))) * (max - min));
            }

            newVal = Math.max(newVal, min);
            newVal = Math.min(newVal, max);
            currentFactor = (newVal - min * 1.0) / (max - min);
            currentVal = newVal;
            Settings.put(choice, newVal);


        }
    }

    @Override
    public void render(GraphicsContext g) {

        g.setFont(new Font(Settings.FONT, Main.correctUnit(25)));


        if (mouseOver) {
            g.setFill(Color.color(1, 1, 1, 0.5));

        } else {
            g.setFill(Color.color(0, 0, 0, 0.5));
        }
        g.fillRect(Main.correctUnit(x - insetWidth), Main.correctUnit(y - insetWidth), Main.correctUnit(width + insetWidth * 2), Main.correctUnit(height + insetWidth * 2));

        g.setFill(Color.color(0, 1, 0, 0.5));

        if (vertical) {
            g.fillRect(Main.correctUnit(x), getRenderY(), Main.correctUnit(width), Main.correctUnit(currentFactor * height));
        } else {
            g.fillRect(Main.correctUnit(x), getRenderY(), Main.correctUnit(currentFactor * width), Main.correctUnit(height));
            g.setFill(Color.WHITE);
            g.fillText(text + " " + currentVal + ((currentVal == recommendedValue) ? " (recommended)" : ""), Main.correctUnit(x + width / 3.0), getRenderY() + Main.correctUnit(height / 2.0));

        }


    }
}
