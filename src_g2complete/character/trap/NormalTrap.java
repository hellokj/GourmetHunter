package character.trap;

import character.Actor;
import character.Floor;
import character.GameObject;
import character.food.Food;
import frame.scene.Scene;
import util.ResourcesManager;

import java.awt.image.BufferedImage;

public class NormalTrap implements Trap {
    private static String imagePath = "floor/Floor.png";
    private static BufferedImage image = ResourcesManager.getInstance().getImage(imagePath);
    private static final int[] choosingImagesMode = {0};

    private Food food; // 地板上的食物

    public NormalTrap(){

    }

    @Override
    public void setFloorState(Floor floor) {
        floor.getFloorImages().add(image);
        floor.setChoosingImagesMode(choosingImagesMode);
        floor.setDrawWidth(image.getWidth());
        floor.setDrawHeight(image.getHeight());
        // 機率食物生成，只在普通地板有機會生成食物
        int random = (int)(Math.random()*100);
        // 生成食物機率
        int genFoodRate = 60;
        if (random < genFoodRate){
            food = new Food(floor, ((int)(Math.random()*100)%3), "food/farm_product"+ ((int)(Math.random()*32)+1) +".png");
        }
//        food = new Food(((int)((Math.random())*33)), floor);
        if (food != null){
            floor.setFood(food);
        }
    }

    @Override
    public void execute(Actor player, Floor floor, Scene scene) {
        // do nothing
    }
}
