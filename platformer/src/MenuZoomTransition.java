import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;

public class MenuZoomTransition extends MenuButton {

    private Image image;
    private int currentTick;
    private double time;



    public MenuZoomTransition(double time,int x, int y) {
        super(x,y,100,100, "", TextType.normal);


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

        double x = Main.interpolate(0,Main.correctUnit(this.x),time*Settings.get("fps"), currentTick);
        double y = Main.interpolate(0,Main.correctUnit(this.y),time*Settings.get("fps"), currentTick);

        double width = Main.interpolate(0,g.getCanvas().getWidth(),time*Settings.get("fps"), currentTick);
        double height = Main.interpolate(0,g.getCanvas().getHeight(),time*Settings.get("fps"), currentTick);


        g.drawImage(image,x,y,width,height);


    }
}