package GameControl;

import Util.SoundLoader;
import javafx.scene.media.AudioClip;

public class TimedSound {

    private double lastInteraction = 0;

    private double interval;

    public TimedSound(double interval) {
        this.interval = interval;

    }


    public void play(AudioClip sound, double volume, double speed)  {


        if (System.currentTimeMillis() - lastInteraction > interval) {
            SoundLoader.playSound(sound, volume, 0, speed);
            lastInteraction = System.currentTimeMillis();


        }
    }



    public void playVaried(AudioClip sound, double volume, double speed, double velX)  {


        if (System.currentTimeMillis() - lastInteraction > Math.max(((1 / (Math.abs(velX)))) * (interval*6.66), interval)) {
            SoundLoader.playSound(sound, volume, 0, speed);
            lastInteraction = System.currentTimeMillis();
            System.out.println("huh");


        }
    }

}
