/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package character;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import frame.MainPanel;
import util.PainterManager;
import util.ResourcesManager;

public class Flash extends GameObject{
    private ArrayList<BufferedImage> flashImages; // 統一圖片回傳類型(處理動畫的機制，取代GameObject只存一張圖)
    
// 依照回傳的選圖模式來印出不同圖片
    private int[] choosingImagesMode = {0, 0, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 4, 4};
    
    private int drawingDelayCount, drawingDelay; // 繪製動畫延遲
    
    public Flash(int x, int y, int drawWidth, int drawHeight){
        super(x, y, drawWidth, drawHeight);
        this.flashImages = new ArrayList<>();
        for(int i=1;i<6;i++){
            String s = "background/flash"+i+".png";
            this.flashImages.add(ResourcesManager.getInstance().getImage(s));
        }
    }
    public void setCounter(int count){
        this.choosingImagesCounter = count;
    }

    @Override
    public void paint(Graphics g, MainPanel mainPanel){
        Graphics2D g2d = PainterManager.g2d(g);
        modX = (int) (x * MainPanel.RATIO);
        modY = (int) (y * MainPanel.RATIO);
        g2d.drawImage(flashImages.get(choosingImagesMode[choosingImagesCounter]), modX, modY , (int)(modX + drawWidth* MainPanel.RATIO), (int)(modY + drawHeight* MainPanel.RATIO), 0, 0, flashImages.get(0).getWidth(), flashImages.get(0).getHeight(), null);
        choosingImagesCounter++;
        if(choosingImagesCounter==14){
            choosingImagesCounter = 0;
        }
    }
}
