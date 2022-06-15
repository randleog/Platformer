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

    public static final AudioClip buttonPress = new AudioClip(new File(path + "button_press.wav").toURI().toString());
    public static final AudioClip scroll = new AudioClip(new File(path + "scroll.wav").toURI().toString());
    public static final AudioClip fall = new AudioClip(new File(path + "fall.wav").toURI().toString());
    public static final AudioClip slime = new AudioClip(new File(path + "slime.wav").toURI().toString());
    public static final AudioClip stone = new AudioClip(new File(path + "stone.wav").toURI().toString());
    public static final AudioClip suck = new AudioClip(new File(path + "suck.wav").toURI().toString());

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
            music.setVolume( ((Settings.get("music volume") *1.000/ 100)));


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

    public static void playFall() {
        playSound(SoundLoader.fall, 1, 0, SoundLoader.getRandomSpeed() * 0.7);
    }



    public static void playSound(AudioClip mediaPlayer, double volume, double balance, double speed) {
        volume = volume/(100.0/Settings.get("volume"));

        mediaPlayer.setVolume(volume);
        mediaPlayer.setRate(speed);

        mediaPlayer.setBalance(balance);
        mediaPlayer.play();


    }





    public static Media getSound(String sound) {


        Media music = new Media(new File(path + sound).toURI().toString());
        return music;


    }


}
