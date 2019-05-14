package character;

import frame.MainPanel;
import util.PainterManager;

import java.awt.*;

public class AnimationGameObject extends GameObject implements Cloneable{
    private int stayDelayCount, stayDelay;
    private int[] movingPattern = {0, 1, 2, 3};
    private int state;

    public AnimationGameObject(int x, int y, int drawWidth, int drawHeight, int imageWidth, int imageHeight, String imageName){
        super(x, y, drawWidth, drawHeight, imageWidth, imageHeight, imageName);
        this.state = 0;
    }

    public AnimationGameObject(int x, int y, int drawWidth, int drawHeight){
        super(x, y, drawWidth, drawHeight);
        this.state = 0;
    }

    public void setMovingPattern(int[] movingPattern) {
        this.movingPattern = movingPattern;
    }

    public void setStayDelay(int stayDelay) {
        this.stayDelay = stayDelay;
    }

    public int getStayDelay() {
        return stayDelay;
    }

    public int getStayDelayCount() {
        return stayDelayCount;
    }

    // 原地踏步
    public void stay(){
        stayDelay = 8; // 設定原地踏步延遲機制
        if(stayDelayCount++ % stayDelay == 0){
            this.imageOffsetX = movingPattern[choosingImagesCounter % movingPattern.length];
            this.choosingImagesCounter = choosingImagesCounter % movingPattern.length;
            this.choosingImagesCounter++;
        }
    }

    public void playAnimation(){
        if (state < movingPattern.length){
            imageOffsetX = movingPattern[state++];
        }else {
            imageOffsetX = 3;
        }
    }

    @Override
    public void paint(Graphics g, MainPanel mainPanel){
        Graphics2D g2d = PainterManager.g2d(g);
        modX = (int) (x * MainPanel.RATIO);
        modY = (int) (y * MainPanel.RATIO);
        g2d.drawImage(image, modX, modY, modX + (int)(drawWidth* MainPanel.RATIO), modY + (int)(drawHeight* MainPanel.RATIO),
                imageWidth*imageOffsetX, imageOffsetY,
                imageWidth*imageOffsetX + imageWidth, imageOffsetY + imageHeight, null);
    }

    @Override
    public AnimationGameObject clone(){
        return this;
    }
}
