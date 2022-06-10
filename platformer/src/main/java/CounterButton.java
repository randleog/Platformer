import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CounterButton extends MenuElement {


    private int count;
    private int max;
    private int min;

    private double fontSize;

    public CounterButton(int x, int y, int width, int height,String text, int count, int max, int min) {
        super(x,y,width,height, text, TextType.normal);
        this.count = count;
        this.max = max;
        this.min = min;
        fontSize = width;

    }



    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseClicked && mouseOver) {
            Main.mouseClicked = false;
            int newVal = Settings.get(text)+count;
            newVal = Math.max(newVal, min);
            SoundLoader.playButtonPress();
            newVal = Math.min(newVal,max);
            Settings.put(text, newVal);


        }
    }

    @Override
    public void render(GraphicsContext g) {

        g.setFont(new Font(Main.correctUnit(25)));
        g.setFill(Color.WHITE);
        g.fillText((count > 0) ? "+" : "-", Main.correctUnit(x+10), Main.correctUnit(y+height/2.0));


        if (mouseOver) {
            g.setFill(Color.color(1, 1, 1, 0.5));

        } else {
            g.setFill(Color.color(0, 0, 0, 0.5));
        }
        g.fillRect(Main.correctUnit(x), Main.correctUnit(y), Main.correctUnit(width),Main.correctUnit(height));

    }
}
