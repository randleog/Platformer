import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ScrollMenu extends MenuElement {


    private Menu menu;
    private double currentFactor = 0;

    private int max;


    private int currentVal;


    public ScrollMenu(int x, int y, int width, int height, Menu menu, String choice) {
        super(x,y,width,height, "", TextType.normal);

        this.menu = menu;
        this.choice = choice;


        int largestY = 0;

        for (MenuElement element : menu.getbuttons()) {
            element.setX((int)element.getX()+this.x);
            element.setY((int)element.getY()+this.y);

            if (element.getY() > largestY) {
                largestY = (int)element.getY();
            }

            max = largestY;

        }




        currentFactor = (Settings.get(choice) * 1.0) / (max-this.height );
        currentVal = Settings.get(choice);
    }



    public void tick() {


        click();
        updateMouse();
        int scroll = Settings.get(choice);


        if (Main.totalScrolls !=0) {
            scroll = scroll - Main.totalScrolls;
            Main.totalScrolls = 0;

            scroll = Math.min(max, scroll);
            scroll = Math.max(0, scroll);


            Settings.put(choice, scroll);
            currentFactor = (scroll * 1.0) / (max);

        }


        for (MenuElement element : menu.getbuttons()) {
            element.setAddY(-scroll);
            if ((getSquare().intersect(element.getSquare()))) {
                element.tick();
            }
        }

    }

    @Override
    public void render(GraphicsContext g) {
        for (MenuElement element : menu.getbuttons()) {
            if ((getSquare().intersect(element.getSquare()))) {
                element.render(g);
            }

        }



        double x = this.x + width;

        g.setFont(new Font(Settings.FONT, Main.correctUnit(25)));


        if (mouseOver) {
            g.setFill(Color.color(1, 1, 1, 0.5));

        } else {
            g.setFill(Color.color(0, 0, 0, 0.5));
        }



        g.fillRect(Main.correctUnit(x - insetWidth), Main.correctUnit(y - insetWidth), Main.correctUnit(50 + insetWidth * 2), Main.correctUnit(height + insetWidth * 2));

        g.setFill(Color.color(0, 1, 0, 0.5));

        g.fillRect(Main.correctUnit(x), getRenderY(), Main.correctUnit(50), Main.correctUnit(currentFactor * height));
    }

    protected void updateMouse() {
        if (hideButton)  {
            return;
        }
        double x = this.x + width;
        mouseOver = (Main.mouseX > Main.correctUnit(x)
                && Main.mouseX < Main.correctUnit(x+50)
                && Main.mouseY > getRenderY()
                && Main.mouseY < getRenderY()+Main.correctUnit(this.height));

    }

    private void click() {
        if (Main.mouseDown && mouseOver) {
            //  Main.mouseDown = false;


            int newVal;

            newVal =(int) (((Main.mouseY - Main.correctUnit(this.y)) / Main.correctUnit((this.height))) * (max));
            System.out.println(max);

            newVal = Math.min(newVal, max);
            currentFactor = (newVal*1.0) / (max);
            currentVal = newVal;
            Settings.put(choice, newVal);


        }
    }
}
