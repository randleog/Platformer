package Menu;

import Main.Main;
import Menu.MenuElement;
import Util.MapLoader;
import Util.Settings;
import Util.SoundLoader;
import Util.UserFileHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ReplayButton extends MenuElement {

    private static final int TICK_REFRESH = 200;


    private String name;

    private int currentTick = 0;


    public ReplayButton(int x, int y, int width, int height, String name) {
        super(x,y,width,height, "level: " + name, TextType.normal);
        this.name = name;
        double time = UserFileHandler.getTime(name, 1);
        if (time == -1) {
            text = text+"\ntime: N/A";
        } else {
            text = text+"\ntime: " + String.format("%.2f",time);
        }

    }

    public void refreshTime() {

        double time = UserFileHandler.getTime(name, 1);
        if (time == -1) {
            text = "level: " + name+"\ntime: N/A";
        } else {
            text = "level: " + name+"\ntime: " + String.format("%.2f",time);
        }
    }



    public void tick() {

        currentTick++;
        if (currentTick == TICK_REFRESH) {
            refreshTime();
        }
        updateMouse();

        click();

    }


    private Color getCurrentColour() {


            if (mouseOver) {
                return  Color.color(1, 1, 1, 0.5);

            } else {
                return  Color.color(0, 0, 0, 0.5);
            }

    }


    private void click() {
        if (Main.mouseClicked && mouseOver) {
            Main.mouseClicked = false;
            SoundLoader.playButtonPress();
            Main.playMap(MapLoader.loadMap( name, 2));
        }
    }

    @Override
    public void render(GraphicsContext g) {
        g.setFill(getCurrentColour());
        g.fillRect(getRenderX(), getRenderY(), getRenderWidth(), Main.correctUnit(height));
        g.setFill(Color.WHITE);
        g.setFont(new Font(Settings.FONT,Main.correctUnit(25)));
        g.fillText(text, getRenderX()+ Main.correctUnit(10), getRenderY() +Main.correctUnit( height / 2.0));


    }
}
