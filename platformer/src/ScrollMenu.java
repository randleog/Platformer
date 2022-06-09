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
                largestY = (int)(element.getY()+element.getHeight());
            }
            if (element.getX()+element.getWidth() > largestX) {
                largestX = (int)(element.getWidth());
            }



        }
        largestX = largestX;

        largestY = Math.max(0, (int)(largestY-height));

        if (largestY > height/2) {
            hasYBar = true;
            ySlider = new MenuSlider(width+x,y,50,height,"",choice + " Y",largestY,0,true, true);
        }

        if (largestX > width) {
            hasXBar = true;

            int actualWidth = (int)(largestX-(width*0.75));


            xSlider = new MenuSlider(x,y+height,width,50,"",choice+ " X",actualWidth,0,false, true);
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
            if (element instanceof MenuText) {

                int maxLength = (int)((element.getWidth()-scrollX)-width+30);
                ((MenuText) element).setExtraWidth(Math.max(0, maxLength));


            }

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
