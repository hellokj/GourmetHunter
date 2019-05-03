package character.food;

import character.Floor;
import character.GameObject;
import util.ResourcesManager;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Food extends GameObject {
    private Floor floor; // 在的那塊地板
    private boolean isEaten; // 是否被吃
    private int heal; // 回復的飢餓值
    private int offset; // 在階梯上位置

    public Food(Floor floor, int offset, BufferedImage image){
        super(floor.getX() + 20*offset, floor.getY() - 20, 20, 20);
        this.offset = offset;
        this.isEaten = false;
        this.floor = floor;
        this.heal = (int)(Math.random()*100);
        this.image = image;
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

}
