import java.io.File;
import java.util.ArrayList;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;



public class SoundLoader {
    private static final int MAX_SOUNDS = 5;

    public static MediaPlayer[] sfx = new MediaPlayer[MAX_SOUNDS];
    public static MediaPlayer music;

    private static final String path = "res\\sounds\\";

    public static final Media buttonPress = getSound("button_press.mp3");
    public static final Media music1 = getSound("music1.mp3");




    public static void playMusic(Media media, double volume, double balance) {
        volume = volume/(100.0/Settings.get("music volume"));
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(false);
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
            music.setVolume(0.100*(Settings.get("music volume")/100.00));
        }

    }

    public static void adjustSfxVolume() {


        for (int i = 0; i < sfx.length ; i++) {
            if (!(sfx[i] == null)) {
                sfx[i].setVolume(Settings.get("volume")/100.00);
            }
        }

    }

        public static void playSound(Media media, double volume, double balance) {


        volume = volume/(100.0/Settings.get("volume"));

        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(false);
        mediaPlayer.setVolume(volume);
        mediaPlayer.setBalance(balance);
        mediaPlayer.play();

        boolean hasAssigned = false;

        double leastTimeLeft = 10000;
        int pos = 0;

        for (int i = 0; i < sfx.length ; i++) {

            if (!hasAssigned) {
                if (sfx[i] == null) {

                    sfx[i] = mediaPlayer;
                    return;
                }

                if (sfx[i].getCurrentTime().toSeconds() >= sfx[i].getTotalDuration().toSeconds()) {
                    sfx[i].stop();
                    sfx[i].dispose();
                    sfx[i] = mediaPlayer;
                    hasAssigned = true;
                } else {
                    if (sfx[i].getTotalDuration().toSeconds() - sfx[i].getCurrentTime().toSeconds() < leastTimeLeft) {
                        leastTimeLeft = sfx[i].getTotalDuration().toSeconds() - sfx[i].getCurrentTime().toSeconds();
                        pos = i;
                    }
                }
            }
        }

        if (!hasAssigned) {
            System.out.println("no space for sound left, replacing space: " + pos);
            sfx[pos] = mediaPlayer;
        }

    }





    public static Media getSound(String sound) {




        Media music = new Media(new File(path + sound).toURI().toString());
        return music;


    }





}
