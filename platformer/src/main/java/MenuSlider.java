import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MenuSlider extends MenuElement {


    private int max;
    private int min;

    private int recommendedValue;

    private int currentVal = 0;
    private double currentFactor = 0;

    private double fontSize;


    boolean vertical = false;

    private boolean hideText = false;


    private int lastValue = 0;

    private double lastInteraction = 0;
    private int timeBetweenSounds = 50;

    public MenuSlider(int x, int y, int width, int height, String text, String choice, int max, int min, boolean vertical, boolean hideText) {
        super(x, y, width, height, text, TextType.normal);

        this.max = max;
        this.min = min;
        fontSize = width;
        this.hideText = hideText;

        this.choice = choice;

        currentFactor = (Settings.get(choice) * 1.0) / (max - min);
        currentVal = Settings.get(choice);


        recommendedValue = min - 1;
        this.vertical = vertical;
    }

    public MenuSlider(int x, int y, int width, int height, String text, String choice, int max, int min, boolean vertical, int recommendedValue) {
        super(x, y, width, height, text, TextType.normal);

        this.max = max;
        this.min = min;
        fontSize = width;
        this.choice = choice;

        currentFactor = (Settings.get(choice) * 1.0) / (max - min);
        currentVal = Settings.get(choice);

        this.recommendedValue = recommendedValue;

        this.x = this.x + (int) insetWidth;
        this.y = this.y + (int) insetWidth;

        this.width = this.width - (int) (insetWidth * 2);
        this.height = this.height - (int) (insetWidth * 2);
        this.vertical = vertical;
    }


    public void tick() {

        boolean changeMouse = false;
        if (mouseOver) {
            changeMouse = true;
        }

        if (vertical) {
            if (Main.totalScrolls != 0) {



                currentVal = currentVal - Main.totalScrolls;

                currentVal = Math.min(max, currentVal);
                currentVal = Math.max(min, currentVal);

                Main.totalScrolls = 0;


                Settings.put(choice, currentVal);
                currentFactor = (currentVal - min * 1.0) / (max - min);



            }
        }

        updateMouse();

        click();

        if (changeMouse) {
            if (!mouseOver) {
                Main.mouseDown = false;
            }
        }

    }


    @Override
    protected double getRenderX() {

        return Main.correctUnit(x+addX);
    }

    private void click() {
        if (Main.mouseDown && mouseOver) {
            //  Main.mouseDown = false;


            int newVal;

            if (vertical) {
                if (Main.totalScrolls !=0) {

                    currentVal = currentVal - Main.totalScrolls;
                    Main.totalScrolls = 0;



                    Settings.put(choice, currentVal);
                    currentFactor = (currentVal - min * 1.0) / (max - min);



                }
                newVal = min + (int) (((Main.mouseY - getRenderY()) / Main.correctUnit((this.height))) * (max - min));
            } else {
                newVal = min + (int) (((Main.mouseX - getRenderX()) / Main.correctUnit((this.width))) * (max - min));
            }

            newVal = Math.max(newVal, min);
            newVal = Math.min(newVal, max);
            currentFactor = (newVal - min * 1.0) / (max - min);
            currentVal = newVal;

           playSound();



            Settings.put(choice, newVal);

            SoundLoader.adjustMusicVolume();



        }
    }

    public void playSound() {
        if (currentVal != lastValue) {
            if (System.currentTimeMillis() -lastInteraction > timeBetweenSounds) {
                SoundLoader.playScroll();
                lastInteraction = System.currentTimeMillis();

            }

        }
        lastValue = (currentVal);

    }

    @Override
    public void render(GraphicsContext g) {
        currentFactor = (Settings.get(choice) - min * 1.0) / (max - min);
        g.setFont(new Font(Settings.FONT, Main.correctUnit(25)));


        if (mouseOver) {
            g.setFill(Color.color(1, 1, 1, 0.5));

        } else {
            g.setFill(Color.color(0, 0, 0, 0.5));
        }
        g.fillRect(getRenderX()-Main.correctUnit(insetWidth), Main.correctUnit(y - insetWidth)
                , Main.correctUnit(width + insetWidth * 2), Main.correctUnit(height + insetWidth * 2));

        g.setFill(Color.color(0, 1, 0, 0.5));


        if (!hideText) {
            if (vertical) {
                g.fillRect(getRenderX(), getRenderY(), Main.correctUnit(width), Main.correctUnit(currentFactor * height));
            } else {
                g.fillRect(getRenderX(), getRenderY(), Main.correctUnit(currentFactor * width), Main.correctUnit(height));
            }
            g.setFill(Color.WHITE);
            g.fillText(text + " " + currentVal + ((currentVal == recommendedValue) ? " (recommended)" : "")
                    , getRenderX() + Main.correctUnit(width / 3.0), getRenderY() + Main.correctUnit(height / 2.0));
        } else {
            if (vertical) {
                g.fillRect(getRenderX(), Main.correctUnit(currentFactor * height)+getRenderY()-Main.correctUnit(width/2.0), Main.correctUnit(width), Main.correctUnit(width));
            } else {
                g.fillRect(Main.correctUnit(currentFactor * width)+getRenderX()-Main.correctUnit(height/2.0), getRenderY(), Main.correctUnit(height), Main.correctUnit(height));
            }
        }

    }
}
