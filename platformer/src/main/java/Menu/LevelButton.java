package Menu;

import Main.Main;
import Util.MapLoader;
import Util.Settings;
import Util.SoundLoader;
import Util.UserFileHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import Util.Replay;


public class LevelButton extends MenuElement {




    private String name;


    double timeBefore = 0;



    public LevelButton(int x, int y, int width, int height, String name) {
        super(x, y, width, height, "level: " + name, MenuElement.TextType.normal);
        this.name = name;

        double time = UserFileHandler.getTime(name, 1);
        timeBefore = time;
        if (time == -1) {
            text = text + "\nbest time: N/A";
        } else {
            text = text + "\nbest time: " + String.format("%.2f", time);
        }

    }



    public LevelButton(String text, int x, int y, int width, int height, String name) {
        super(x, y, width, height, text, MenuElement.TextType.normal);
        this.name = name;

        double time = UserFileHandler.getTime(name, 1);
        timeBefore = time;


    }




    public void tick() {

        updateMouse();

        click();

    }


    private void click() {
        if (Main.mouseClicked && mouseOver) {
            Main.mouseClicked = false;
            SoundLoader.playButtonPress();
            Main.playMap(MapLoader.loadMap(name, 1));
        }
    }


    private Color getCurrentColour() {
        if (Settings.get("full speedrun") == 1) {

            if (Replay.canProgress(Main.currentFull, name)) {
                if (mouseOver) {
                    return Color.color(0.3, 1, 0.3, 0.6);

                } else {
                    return  Color.color(0, 0, 0, 0.5);
                }
            } else {
                if (mouseOver) {
                    return Color.color(1, 0.3, 0.3, 0.5);

                } else {
                    return  Color.color(1, 0, 0, 0.4);
                }
            }


        } else {
            if (mouseOver) {
                return  Color.color(1, 1, 1, 0.5);

            } else {
                return  Color.color(0, 0, 0, 0.5);
            }
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
