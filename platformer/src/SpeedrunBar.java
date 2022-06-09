import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class SpeedrunBar extends MenuElement {

    private static final int HEIGHT = 20;

    private ArrayList<String[]> times = new ArrayList<>();


    private Map map;

    private double oldTime;

    private boolean canPlay;


    public SpeedrunBar(int x, int y, Map map) {
        super(x, y, 300, (Main.lastLevel + 2) * HEIGHT, "", TextType.normal);
        this.map = map;
        oldTime = ReplaySave.getReplay("full\\" + map.getName()).getTime();

        canPlay = (Replay.canProgress(Main.currentFull, map.getName()) || map.getName().equals("1"));

        for (Replay replay : Main.currentFull) {

            String sign = "";
            if (ReplaySave.getReplay("full\\" + replay.getMapName()).getTime() >= replay.getTime() || ReplaySave.getReplay("full\\" + replay.getMapName()).getTime() == -1) {
                sign = "-";
            } else {
                sign = "+";
            }
            times.add(new String[]{replay.getMapName() + ", " + Main.formatTime(replay.getTime()) + ", "
                    + sign + Main.formatTime((replay.getTime() - ReplaySave.getReplay("full\\" + replay.getMapName()).getTime())), sign});
        }

    }


    public void tick() {

    }

    @Override
    public void render(GraphicsContext g) {
        if (canPlay) {


            g.setFill(Color.color(0, 0, 0, 0.5));
            g.fillRect(Main.correctUnit(x), getRenderY(), Main.correctUnit(width), Main.correctUnit(height));


            g.setFill(Color.WHITE);
            g.setFont(new Font(Settings.FONT,Main.correctUnit(HEIGHT)));
            g.fillText("full speedrun progress", Main.correctUnit(x + 5), getRenderY()+Main.correctUnit( 20));

            int height = 0;
            for (String[] line : times) {

                if (line[1].equals("-")) {
                    g.setFill(Color.LIME);

                } else {
                    g.setFill(Color.RED);

                }
                g.fillText(line[0], Main.correctUnit(x + 5), getRenderY()+Main.correctUnit( height + HEIGHT + 20));
                height += HEIGHT;
            }

            if (!(map == null)) {
                double currentTime = (map.getCurrentTick()) / Settings.getD("fps");
                String sign = "";
                if (currentTime < oldTime) {
                    g.setFill(Color.LIME);
                    sign = "-";
                } else {
                    g.setFill(Color.RED);
                    sign = "+";
                }
                g.fillText(map.getName() + ", " + Main.formatTime(currentTime)
                                + ", " + sign + Main.formatTime((currentTime - oldTime))
                        , Main.correctUnit(x + 5)
                        , getRenderY()+Main.correctUnit( height + HEIGHT + 20));

            }
        }
    }


}
