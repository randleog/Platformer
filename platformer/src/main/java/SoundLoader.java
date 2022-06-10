import java.io.File;
import java.util.ArrayList;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class SoundLoader {
    private static final int MAX_SOUNDS = 15;

    public static AudioClip[] sfx = new AudioClip[MAX_SOUNDS];
    public static Integer[] volumes = new Integer[MAX_SOUNDS];

    private static int currentSound = 0;


    public static MediaPlayer music;

    private static final String path = "res/sounds/";

    public static final String buttonPress = path + "button_press.wav";
    public static final String scroll = path + "scroll.wav";
    public static final Media music1 = getSound("music1.mp3");





    public static void playMusic(Media media, double volume, double balance) {
        volume = volume / (100.0 / Settings.get("music volume"));
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(volume);
        mediaPlayer.setBalance(balance);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.play();

        if (music == null) {
            music = mediaPlayer;
        } else {
            music.stop();
            music.dispose();
            music = mediaPlayer;
        }


    }


    public static void adjustMusicVolume() {

        if (!(music == null)) {
            music.setVolume(0.100 * (Settings.get("music volume") / 100.00));
        }

    }


    public static double getRandomSpeed() {
        return Main.random.nextDouble(0.8, 1.2);
    }


    public static void playButtonPress() {


        playSound(SoundLoader.buttonPress, 1, 0, SoundLoader.getRandomSpeed() * 0.7);
    }

    public static void playScroll() {
        playSound(SoundLoader.scroll, 1, 0, SoundLoader.getRandomSpeed() * 0.7);
    }





    public static void playSound(String media, double volume, double balance, double speed) {
        volume = volume/(100.0/Settings.get("volume"));
        if (!(sfx[currentSound] == null)) {
            sfx[currentSound].stop();

            if (sfx[currentSound].getSource().contains(media)) {

                sfx[currentSound].setVolume(volume);
                sfx[currentSound].setRate(speed);
                sfx[currentSound].setBalance(balance);
                sfx[currentSound].play();
                return;
            }

        }



        AudioClip mediaPlayer = new AudioClip(new File(media).toURI().toString());
        mediaPlayer.setVolume(volume);
        mediaPlayer.setRate(speed);

        mediaPlayer.setBalance(balance);



        mediaPlayer.play();



        sfx[currentSound]=mediaPlayer;



        currentSound++;
        if (currentSound >= MAX_SOUNDS) {
            currentSound =0;
        }

    }





    public static Media getSound(String sound) {


        Media music = new Media(new File(path + sound).toURI().toString());
        return music;


    }


}
