package util;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResourcesManager {
    // 圖片、字型、音效資源
    //    private BufferedImage[] images;
//    private String[] paths;
    private Map<String, BufferedImage> images;
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
//        for (int i = 1; i <= 33; i++) {
//            addImage("food/farm_product"+ i +".png");
//        }
    }

    public BufferedImage getImage(String path){
        if (!findExist(path)){
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

    private boolean findExist(String path){
        return images.containsKey(path);
    }
}
