package Menu;

import Main.Main;
import Menu.MenuElement;
import Util.ImageLoader;
import Util.Settings;
import Util.SoundLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SettingButton extends MenuElement {

    private Color selectColor;


    public SettingButton(int x, int y, int width, int height,String text, String choice, MenuElement.TextType textType) {
        super(x,y,width,height, text, textType);
        this.choice = choice;

        selectColor =  Color.color(0, 1, 0, 0.3);

    }


    public SettingButton(int x, int y, int width, int height, String text, String choice, MenuElement.TextType textType, Color selectColor) {
        super(x,y,width,height, text, textType);
        this.choice = choice;
        this.selectColor =  selectColor;

    }


    public void tick() {



        if (hideButton) {
            return;
        }
        updateMouse();

        click();

    }



    private void click() {
        if (hideButton) {
            return;
        }
        if (Main.mouseClicked && mouseOver) {
            SoundLoader.playButtonPress();
            Main.mouseClicked = false;
            Settings.put(key, choice);

        }
    }

    @Override
    public void render(GraphicsContext g) {
        if (hideButton) {


            return;
        }


        super.render(g);

        if (textType == TextType.hide) {
            g.drawImage(ImageLoader.getImage(choice),getRenderX() + Main.correctUnit(insetWidth), getRenderY()+Main.correctUnit(insetWidth), getRenderWidth() - Main.correctUnit(insetWidth*2), Main.correctUnit(height-insetWidth*2));
        }
        if (Settings.getStr(key).equals(choice)) {
            g.setFill(selectColor);
            g.fillRect(getRenderX() + Main.correctUnit(insetWidth), getRenderY()+Main.correctUnit(insetWidth), getRenderWidth() - Main.correctUnit(insetWidth*2),Main.correctUnit(height-insetWidth*2));
        }
    }
}
