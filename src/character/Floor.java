package character;


import character.food.Food;
import character.trap.FlippingTrap;
import character.trap.SpringTrap;
import character.trap.Trap;
import frame.MainPanel;
import frame.scene.Scene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Floor extends GameObject {
    private ArrayList<BufferedImage> floorImages; // 統一圖片回傳類型(處理地板有動畫的機制，取代GameObject只存一張圖)
    private Food food;

    // 跳舞移除
    private boolean isCompleted; // 被輸入完成

    // 依照回傳的選圖模式來印出不同圖片
    private int[] choosingImagesMode;

    private Trap trapFunction; // 機關
    private boolean isTriggered; // 角色在階梯上，觸發機關

    private int drawingDelayCount, drawingDelay; // 繪製動畫延遲

    public Floor(int x, int y, Trap trapFunction){
        super(x, y);
        this.speedY = -1f;
//        this.speedY = -(float)(Math.random()*3);
        this.trapFunction = trapFunction;
        this.floorImages = new ArrayList<>();
        // 傳入陷阱類型，設定自己的狀態
        this.trapFunction.setFloorState(this);
        this.setBoundary();
    }


    // getter and setter
    public ArrayList<BufferedImage> getFloorImages(){
        return this.floorImages;
    }
    public void setChoosingImagesMode(int[] choosingImagesMode){
        this.choosingImagesMode = choosingImagesMode;
    }
    public void setDrawingDelay(int drawingDelay) {
        this.drawingDelay = drawingDelay;
    }
    public void setFood(Food food) {
        this.food = food;
    }
    public Food getFood(){
        return this.food;
    }
    public Trap getTrapFunction(){
        return this.trapFunction;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // 未被觸發的動畫選圖
    // 某些地板被觸發才播動畫，可能不適用此方法
    public void stay(){
        if (drawingDelayCount++ == drawingDelay){
            // 超爛做法(只針對特定畫圖模式更動)
            if (this.trapFunction instanceof SpringTrap){
                if(choosingImagesCounter == 6){
                    this.choosingImagesMode = SpringTrap.CHOOSING_IMAGES_MODE_BASE;
                }
            }
            if (this.trapFunction instanceof FlippingTrap){
                if (choosingImagesCounter == 2){
                    this.choosingImagesMode = FlippingTrap.CHOOSING_IMAGES_MODE_BASE;
                }
            }
            this.choosingImagesCounter = choosingImagesCounter % choosingImagesMode.length;
            this.choosingImagesCounter++;
            drawingDelayCount = 0;
        }
    }

    public void isBeenTouched(Actor player, Scene scene){
        setTriggered(true);
        executeTrap(player, scene);
    }

    public boolean isTriggered(){
        return isTriggered;
    }
    public void setTriggered(boolean state){
        this.isTriggered = state;
    }


    // 發動機關
    public void executeTrap(Actor player, Scene scene){
        if (isTriggered){
            this.trapFunction.execute(player, this, scene);
            // 觸發狀態設回未觸發
            isTriggered = false;
        }
    }

    @Override
    public void update(){
        // 如果身上有食物，將食物座標更新
        if (food != null){
            food.update();
        }
        y += speedY;
        // 將吃掉的食物移除
        if (food != null){
            if (food.isEaten()){
                food = null;
            }
        }
        setBoundary();
    }

    @Override
    public void paint(Graphics g, MainPanel mainPanel){
        modX = (int) (x * MainPanel.RATIO);
        modY = (int) (y * MainPanel.RATIO);
        // 加入不同地板動畫後，畫圖模式(測試)
        if (food != null){
            try {
                food.paint(g, mainPanel);
            }catch (NullPointerException e){
            }
        }
//        g2d.setColor(Color.YELLOW);
//        g2d.drawRect(modX-1, modY-1, (int)(drawWidth* MainPanel.RATIO + 1), (int)(drawHeight* MainPanel.RATIO +1));
        try {
            //  有機會畫到還未重設的畫圖模式 會報錯
            g.drawImage(floorImages.get(choosingImagesMode[choosingImagesCounter-1]), modX, (int)(modY - floorImages.get(choosingImagesMode[choosingImagesCounter-1]).getHeight()* MainPanel.RATIO + drawHeight* MainPanel.RATIO), (int)(modX + drawWidth* MainPanel.RATIO), modY + (int)(drawHeight* MainPanel.RATIO), 0, 0, floorImages.get(choosingImagesMode[choosingImagesCounter-1]).getWidth(), floorImages.get(choosingImagesMode[choosingImagesCounter-1]).getHeight(), null);
        }catch (ArrayIndexOutOfBoundsException e){
//            g.drawImage(floorImages.get(choosingImagesMode[0]), modX, (int)(modY - (floorImages.get(choosingImagesMode[0]).getHeight()* MainPanel.RATIO + drawHeight* MainPanel.RATIO)), (int)(modX + drawWidth* MainPanel.RATIO), (int)(modY + drawHeight* MainPanel.RATIO), 0, 0, floorImages.get(choosingImagesMode[0]).getWidth(), floorImages.get(choosingImagesMode[0]).getHeight(), null);
        }
    }

    public BufferedImage getCurrentDrawingImage(){
        return floorImages.get(choosingImagesMode[choosingImagesCounter-1]);
    }

}
