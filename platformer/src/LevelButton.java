import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LevelButton extends MenuButton {

    private static final int TICK_REFRESH = 200;


    private String name;

    private int currentTick = 0;
    double timeBefore = 0;

    private int celebrationTicks;

    private static final int CELEBRATION_TIME = 2;

    public LevelButton(int x, int y, int width, int height, String name) {
        super(x,y,width,height, "level: " + name);
        this.name = name;
        celebrationTicks = 0;
        double time = UserFileHandler.getUserTime(name, 1);
        timeBefore = time;
        if (time == -1) {
            text = text+"\nbest time: N/A";
        } else {
            text = text+"\nbest time: " + String.format("%.2f",time);
        }

    }

    public void refreshTime() {


        double time = UserFileHandler.getUserTime(name, 1);
        if (time < timeBefore) {
            celebrationTicks = CELEBRATION_TIME*Main.fps;
        }

        if (time == -1) {
            text = "level: " + name+"\nbest time: N/A";
        } else {
            text = "level: " + name+"\nbest time: " + String.format("%.2f",time);
        }
    }



    public void tick() {

        celebrationTicks = Math.max(celebrationTicks--, 0);

        currentTick++;
        if (currentTick == TICK_REFRESH) {
            refreshTime();
        }
        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;
            Main.playMap(MapLoader.loadMap(name, 1));
        }
    }


    @Override
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

        if (celebrationTicks > 0) {
            g.fillText("NEW PB!", Main.correctUnit(x+10), Main.correctUnit(y+height/2.0)-Main.correctUnit(height/2.0));
        }
    }
}
