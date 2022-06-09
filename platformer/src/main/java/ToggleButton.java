import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ToggleButton extends MenuElement {

    private Color selectColor;

    private String text1;
    private String text2;


    public ToggleButton(int x, int y, int width, int height,String text1, String text2, String key) {
        super(x,y,width,height, text1, TextType.normal);

        selectColor =  Color.color(0, 1, 0, 0.3);
        this.text1 =text1;
        this.text2 = text2;
        this.key = key;


    }


    public ToggleButton(int x, int y, int width, int height,String text1, String text2, String key, Color selectColor) {
        super(x,y,width,height, text1, TextType.normal);

        this.text1 =text1;
        this.text2 = text2;

        this.selectColor =  selectColor;
        this.key = key;


    }


    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseClicked && mouseOver) {
            Main.mouseClicked = false;

            Settings.put(key, -Settings.get(key));

        }
    }

    @Override
    public void render(GraphicsContext g) {

        if (Settings.get(key) >0) {
            this.text = text2;
        } else {
            this.text = text1;
        }
        if (Settings.get(key) == -1) {
           // g.setFill(Color.color(1, 0, 0, 0.5));
        } else {

            g.setFill(selectColor);
            g.fillRect(getRenderX() + Main.correctUnit(insetWidth), getRenderY()+Main.correctUnit(insetWidth), getRenderWidth() -Main.correctUnit(insetWidth*2),Main.correctUnit(height-insetWidth*2));

        }


        super.render(g);

    }
}
