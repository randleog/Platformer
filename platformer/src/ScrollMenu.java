import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ScrollMenu extends MenuElement {


    private Menu menu;






    private boolean hasYBar;
    private boolean hasXBar;


    private MenuSlider ySlider;
    private MenuSlider xSlider;

    public ScrollMenu(int x, int y, int width, int height, Menu menu, String choice) {
        super(x,y,width,height, "", TextType.normal);

        this.menu = menu;
        this.choice = choice;


        int largestY = 0;
        int largestX = 0;
        for (MenuElement element : menu.getbuttons()) {
            element.setX((int)element.getX()+this.x);
            element.setY((int)element.getY()+this.y);

            if (element.getY() > largestY) {
                largestY = (int)element.getY();
            }
            if (element.getX()+element.getWidth() > largestX) {
                largestX = (int)(element.getWidth());
            }



        }
        largestX = largestX/2;

        if (largestY > height) {
            hasYBar = true;
            ySlider = new MenuSlider(width+x,y,50,height,"",choice + " Y",largestY,0,true);
        }

        if (largestX > width) {
            hasXBar = true;

            xSlider = new MenuSlider(x,y+height,width,50,"",choice+ " X",largestX,0,false);
        }


    }



    public void tick() {

        if (hasYBar) {
            ySlider.tick();
        }

        if (hasXBar) {
            xSlider.tick();
        }

        int scrollY = Settings.get(choice + " Y");
        int scrollX = Settings.get(choice + " X");
        for (MenuElement element : menu.getbuttons()) {
            element.setAddY(-scrollY);
            element.setAddX(-scrollX);
            if ((getSquare().intersect(element.getSquare()))) {
                element.tick();
            }
        }

    }

    @Override
    public void render(GraphicsContext g) {
        if (hasYBar) {
            ySlider.render(g);
        }

        if (hasXBar) {
            xSlider.render(g);
        }

        for (MenuElement element : menu.getbuttons()) {
            if ((getSquare().intersect(element.getSquare()))) {
                element.render(g);
            }

        }




    }




}
