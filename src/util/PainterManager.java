package util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PainterManager {
    // 處理繪製圖片相關
    private Graphics g;

    private static PainterManager painterManager;

    public static PainterManager getInstance(Graphics g){
        if (painterManager == null){
            painterManager = new PainterManager(g);
        }
        return painterManager;
    }

    public PainterManager(Graphics g){
        this.g = g;
    }

    // 將傳入圖合併成一張回傳
    public static BufferedImage mergeImages(ArrayList<BufferedImage> images){
        ArrayList<int[]> imagesArrays = new ArrayList<>();
        BufferedImage merged = null;
        for (int i = 0; i < images.size(); i++) {
            BufferedImage image = images.get(i);
            int[] imageArray = new int[image.getWidth()*image.getHeight()];
            imageArray = image.getRGB(0, 0, image.getWidth(), image.getHeight(), imageArray, 0, image.getWidth());
            imagesArrays.add(imageArray);
        }
        merged = new BufferedImage(images.get(0).getWidth()*4, images.get(0).getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < imagesArrays.size(); i++) {
            merged.setRGB(i * images.get(0).getWidth(), 0, images.get(0).getWidth(), images.get(0).getHeight(), imagesArrays.get(i), 0, images.get(0).getWidth());
        }
        return merged;
    }
}
