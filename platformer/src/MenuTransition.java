import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;

public class MenuTransition extends MenuButton {

    private Image image;
    private int currentTick;
    private double time;



    public MenuTransition(double time) {
        super(0,0,100,100, "", TextType.normal);


        currentTick = 0;
        this.time = time;

    }

    public void loadImage(Image image) {
        this.image = image;
    }



    public void tick() {
        if (hideButton) {
            return;
        }
        currentTick++;
        if (currentTick/Settings.getD("fps") > time) {
            this.setHideButton(true);
        }

    }





    @Override
    public void render(GraphicsContext g) {
        if (hideButton) {
            return;
        }

        g.save();
        g.setGlobalAlpha(1-(currentTick/Settings.getD("fps")/time));
        g.drawImage(image,0,0,g.getCanvas().getWidth(),g.getCanvas().getHeight());
        g.restore();

    }
}
