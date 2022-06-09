import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MenuText extends MenuElement {




    public static final double TEXT_WIDTH_FACTOR = 0.6;

    private int size;

    private String font = Settings.FONT;

    private String updateTag;


    private int extraWidth = 0;

    public MenuText(int x, int y, String text, int size) {
        super(x,y,1,1, text, TextType.normal);
        this.updateTag = "none";
        this.size = size;


    }


    public MenuText(int x, int y, String text, int size, String updateTag) {
        super(x,y,1,1, text, TextType.normal);
        this.size = size;
        this.updateTag = updateTag;

    }

    public MenuText(int x, int y, String text, int size, String updateTag, String font) {
        super(x,y,1,1, text, TextType.normal);
        this.size = size;
        this.updateTag = updateTag;

        this.font = font;

    }

    @Override
    public double getWidth() {

        return this.text.length()*size*TEXT_WIDTH_FACTOR;
    }




    public String getUpdateTag() {
        return updateTag;
    }

    public void tick() {

    }


    public void setExtraWidth(int extraWidth) {
        this.extraWidth = (int)(extraWidth/(size*TEXT_WIDTH_FACTOR));
    }

    @Override
    public void render(GraphicsContext g) {


        g.setFill(Color.WHITE);
        g.setFont(new Font(font,Main.correctUnit(size)));
        g.fillText(text.substring(0,Math.max(0,text.length()-extraWidth)), getRenderX(), getRenderY());
    }



}
