import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class SpeedrunBar extends MenuButton {

    private static final int HEIGHT = 25;

    private ArrayList<String[]> times = new ArrayList<>();


    private Map map;

    private double oldTime;

    private boolean canPlay;


    public SpeedrunBar(int x, int y, Map map) {
        super(x, y, 300, (Main.lastLevel + 2) * HEIGHT, "");
        this.map = map;
        oldTime = ReplaySave.getReplay("full\\" + map.getName()).getTime();

        canPlay = Replay.canProgress(Main.currentFull, map.getName());

        for (Replay replay : Main.currentFull) {

            String sign = "";
            if (ReplaySave.getReplay("full\\" + replay.getName()).getTime() >= replay.getTime() || ReplaySave.getReplay("full\\" + replay.getName()).getTime() == -1) {
                sign = "-";
            } else {
                sign = "+";
            }
            times.add(new String[]{replay.getName() + ", " + String.format("%.2f", replay.getTime()) + ", " + sign + String.format("%.2f", (replay.getTime() - ReplaySave.getReplay("full\\" + replay.getName()).getTime())), sign});
        }

    }


    public void tick() {

    }

    @Override
    public void render(GraphicsContext g) {
        if (canPlay) {


            g.setFill(Color.color(0, 0, 0, 0.5));
            g.fillRect(Main.correctUnit(x), Main.correctUnit(y), Main.correctUnit(width), Main.correctUnit(height));


            g.setFill(Color.WHITE);
            g.setFont(new Font(Main.correctUnit(HEIGHT)));
            g.fillText("full speedrun progress", Main.correctUnit(x + 5), Main.correctUnit(y + 20));

            int height = 0;
            for (String[] line : times) {

                if (line[1].equals("-")) {
                    g.setFill(Color.LIME);

                } else {
                    g.setFill(Color.RED);

                }
                g.fillText(line[0], Main.correctUnit(x + 5), Main.correctUnit(y + height + HEIGHT + 20));
                height += HEIGHT;
            }

            if (!(map == null)) {
                double currentTime = (map.getCurrentTick()) / Main.fps;
                String sign = "";
                if (currentTime < oldTime) {
                    g.setFill(Color.LIME);
                    sign = "-";
                } else {
                    g.setFill(Color.RED);
                    sign = "+";
                }
                g.fillText(map.getName() + ", " + String.format("%.2f"
                                , currentTime) + ", " + sign + String.format("%.2f"
                                , (currentTime - oldTime)), Main.correctUnit(x + 5)
                        , Main.correctUnit(y + height + HEIGHT + 20));

            }
        }
    }


}
