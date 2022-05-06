import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MenuText extends MenuButton {


    private String name;

    private int size;

    public MenuText(int x, int y, String text, int size) {
        super(x,y,1,1, text);
        this.size = size;

        double time = UserFileHandler.getUserTime(name, 1);
        if (time == -1) {
            text = text+"\nbest time: N/A";
        } else {
            text = text+"\nbest time: " + String.format("%.2f",time);
        }

    }



    public void tick() {

    }

    @Override
    public void render(GraphicsContext g) {


        g.setFill(Color.WHITE);
        g.setFont(new Font(size));
        g.fillText(text, Main.correctUnit(x), Main.correctUnit(y));
    }



}
