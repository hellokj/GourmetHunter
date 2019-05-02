package character;

import java.awt.*;

public class AnimationGameObject extends GameObject implements Cloneable{
    private int stayDelayCount, stayDelay;
    private int[] MOVING_PATTERN = {0, 1, 2, 3};
    private int state;

    public AnimationGameObject(int x, int y, int drawWidth, int drawHeight, int imageWidth, int imageHeight, String imageName){
        super(x, y, drawWidth, drawHeight, imageWidth, imageHeight, imageName);
        this.state = 0;
    }

    public AnimationGameObject(int x, int y, int drawWidth, int drawHeight){
        super(x, y, drawWidth, drawHeight);
        this.state = 0;
    }

    // 原地踏步
    public void stay(){
        stayDelay = 8; // 設定原地踏步延遲機制
        if(stayDelayCount++ % stayDelay == 0){
            this.imageOffsetX = MOVING_PATTERN[choosingImagesCounter % MOVING_PATTERN.length];
            this.choosingImagesCounter = choosingImagesCounter % MOVING_PATTERN.length;
            this.choosingImagesCounter++;
        }
    }

    public void playAnimation(){
        if (state < MOVING_PATTERN.length){
            imageOffsetX = MOVING_PATTERN[state++];
        }else {
            imageOffsetX = 3;
        }
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(image, x, y, x + drawWidth, y + drawHeight,
                imageWidth*imageOffsetX, imageOffsetY,
                imageWidth*imageOffsetX + imageWidth, imageOffsetY + imageHeight, null);
    }

    @Override
    public AnimationGameObject clone(){
        return this;
    }
}
