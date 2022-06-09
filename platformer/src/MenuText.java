import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MenuText extends MenuElement {




    private int size;

    private String font = Settings.FONT;

    private String updateTag;

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

        return this.text.length()*size;
    }


    public String getUpdateTag() {
        return updateTag;
    }

    public void tick() {

    }

    @Override
    public void render(GraphicsContext g) {


        g.setFill(Color.WHITE);
        g.setFont(new Font(font,Main.correctUnit(size)));
        g.fillText(text, getRenderX(), getRenderY());
    }



}
