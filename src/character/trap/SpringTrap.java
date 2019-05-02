package character.trap;

import character.Actor;
import character.Floor;
import util.ResourcesManager;

public class SpringTrap implements Trap {
    public static final int generationRate = 25;

    private static final String[] imagePaths =
            {"floor/SpringFloor_0.png", "floor/SpringFloor_1.png", "floor/SpringFloor_2.png", "floor/SpringFloor_3.png"};
    private static final int[] CHOOSING_IMAGES_MODE = {1, 2, 3, 3, 3, 2};
    public static final int[] CHOOSING_IMAGES_MODE_BASE = {1};

    @Override
    public void setFloorState(Floor floor) {
        for (int i = 0; i < imagePaths.length; i++) {
            floor.getFloorImages().add(ResourcesManager.getInstance().getImage(imagePaths[i]));
        }
        // 設定基礎圖寬高
        floor.setDrawWidth(ResourcesManager.getInstance().getImage(imagePaths[0]).getWidth());
        floor.setDrawHeight(ResourcesManager.getInstance().getImage(imagePaths[0]).getHeight());
        // 設定選圖模式
        floor.setChoosingImagesMode(CHOOSING_IMAGES_MODE_BASE);
        // 預計用來設定未被觸發時的狀態圖(尚需修改)
        // 還未能只在碰觸時修改
        floor.setImage(ResourcesManager.getInstance().getImage(imagePaths[0]));
        // 繪製動畫延遲
        floor.setDrawingDelay(6);
    }

    @Override
    public void execute(Actor player, Floor floor) {
        // 彈飛初速度
        int bounceSpeed = -15;
        if (floor.isTriggered()){
            floor.setChoosingImagesMode(CHOOSING_IMAGES_MODE);
            // 彈簧機制
            // 依照現在畫的圖的高度做偏移
            player.setY(floor.getY() + floor.getDrawHeight() - floor.getCurrentDrawingImage().getHeight() - player.getDrawHeight());
            player.setSpeedY(bounceSpeed);
        }
    }
}
