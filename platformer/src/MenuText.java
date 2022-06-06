import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MenuText extends MenuButton {




    private int size;

    private String updateTag;

    public MenuText(int x, int y, String text, int size, String updateTag) {
        super(x,y,1,1, text, TextType.normal);
        this.size = size;
        this.updateTag = updateTag;

    }



    public String getUpdateTag() {
        return updateTag;
    }

    public void tick() {

    }

    @Override
    public void render(GraphicsContext g) {


        g.setFill(Color.WHITE);
        g.setFont(new Font(Main.correctUnit(size)));
        g.fillText(text, Main.correctUnit(x), Main.correctUnit(y));
    }



}
