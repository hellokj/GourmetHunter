package util;

import sun.applet.AppletAudioClip;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResourcesManager{
    // 圖片、字型、音效資源
    private Map<String, BufferedImage> images;
    private Map<String, AudioClip> sounds;
    private Map<String, Font> fonts;

    private static ResourcesManager resourcesManager;
    private static String PRESET_PATH = "src/resource/"; // 預設檔案路徑前綴

    public static ResourcesManager getInstance(){
        if (resourcesManager == null){
            resourcesManager = new ResourcesManager();
        }
        return resourcesManager;
    }

    private ResourcesManager(){
        images = new HashMap<>();
        fonts = new HashMap<>();
        sounds = new HashMap<>();
    }

    public BufferedImage getImage(String path){
        if (!findImageExist(path)){
            return addImage(path);
        }
        return images.get(path);
    }

    private BufferedImage addImage(String path){
        try {
            BufferedImage image = ImageIO.read(new File(PRESET_PATH + path));
            images.put(path, image);
            return image;
        } catch (IIOException e){
            System.out.println(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            System.out.println(path + " not found.");
        }
        return null;
    }

    private boolean findImageExist(String path){
        return images.containsKey(path);
    }

    public AudioClip getSound(String path){
        if (!findSoundExist(path)){
            return addSound(path);
        }
        return sounds.get(path);
    }

    private AudioClip addSound(String path){
        AudioClip ac = Applet.newAudioClip(getClass().getResource("/resource/" + path));
        sounds.put(path, ac);
        return ac;
    }

    private boolean findSoundExist(String path){
        return sounds.containsKey(path);
    }

    public Font getFont(String path){
        if (!findFontExist(path)){
            return addFont(path);
        }
        return fonts.get(path);
    }

    private Font addFont(String path){
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("src/resource/font/" + path));
            fonts.put(path, font);
        }catch (Exception e){
            System.out.println("something wrong");
        }
        return font;
    }

    private boolean findFontExist(String path){
        return fonts.containsKey(path);
    }

    private class TextManager{
        private Font loadFont(String fontName){
            Font font = null;
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, new File("src/resource/font/" + fontName));
            }catch (Exception e){
                System.out.println("something wrong");
            }
            return font;
        }
    }
}
