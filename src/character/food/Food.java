package character.food;

import character.Floor;
import character.GameObject;
import frame.MainPanel;
import util.PainterManager;

import java.awt.*;

public class Food extends GameObject {
    private Floor floor; // 在的那塊地板
    private boolean isEaten; // 是否被吃
    private int heal; // 回復的飢餓值
    private int offset; // 在階梯上位置

    public Food(Floor floor, int offset, String name){
        super(floor.getX() + 20*offset, floor.getY() - 20, 20, 20, 64, 64, name);
        this.offset = offset; // 出現在階梯的第幾個位置上
        this.isEaten = false;
        this.floor = floor;
        this.heal = (int)(Math.random()*9+1);
    }

    @Override
    public void update(){
        this.x = floor.getX() + 20 * offset;
        this.y = floor.getY() - 20;
        this.setBoundary();
    }

    public void eaten() {
        isEaten = true;
    }

    public boolean isEaten() {
        return isEaten;
    }

    public int getHeal() {
        return heal;
    }

    @Override
    public void paint(Graphics g, MainPanel mainPanel){
        Graphics2D g2d = PainterManager.g2d(g);
        modX = (int) (x * MainPanel.RATIO);
        modY = (int) (y * MainPanel.RATIO);
        g2d.drawImage(image, modX, modY,  modX + (int)(drawWidth*MainPanel.RATIO),  modY + (int)(drawHeight*MainPanel.RATIO), 0, 0,imageWidth, imageHeight, null);
    }

}
