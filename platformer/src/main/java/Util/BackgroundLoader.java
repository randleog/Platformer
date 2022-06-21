package Util;

import Map.Background;
import Map.Map;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class BackgroundLoader {


    public static Background loadBackground(String name) {
        File file = new File("res\\repeatingBackgrounds\\" + name + ".txt");


        Background background = new Background();

        if (file.exists()) {
            try {
                Scanner reader = new Scanner(file);


                while (reader.hasNextLine()) {
                    String layer = reader.nextLine();
                    background.addLayer(ImageLoader.loadImage(layer.split(" ")[0]+ ".png", 1920, 1080), Double.parseDouble(layer.split(" ")[1]));

                }

                reader.close();



            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return background;
    }



}
