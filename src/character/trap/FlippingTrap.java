package character.trap;

import character.Actor;
import character.Floor;
import frame.scene.Scene;
import util.ResourcesManager;

public class FlippingTrap implements Trap {
    private final String[] imagePaths = {"floor/FlippingFloor_1.png", "floor/FlippingFloor_2.png"};
    private static final int[] CHOOSING_IMAGES_MODE = {0, 1};
    public static final int[] CHOOSING_IMAGES_MODE_BASE = {0};

    private int executeDelayCount;

    @Override
    public void setFloorState(Floor floor) {
        floor.setImage(ResourcesManager.getInstance().getImage("floor/FlippingFloor_1.png"));
        for (int i = 0; i < imagePaths.length; i++) {
            floor.getFloorImages().add(ResourcesManager.getInstance().getImage(imagePaths[i]));
        }
        // 設定基礎圖寬高
        floor.setDrawWidth(floor.getFloorImages().get(0).getWidth());
        floor.setDrawHeight(floor.getFloorImages().get(0).getHeight());
        // 設定選圖模式
        floor.setChoosingImagesMode(CHOOSING_IMAGES_MODE_BASE);
        // 繪製動畫延遲
        floor.setDrawingDelay(20);
        executeDelayCount = 0;
    }

    @Override
    public void execute(Actor player, Floor floor, Scene scene) {
        // 飢餓值小於40，踩到翻轉，翻後掉落
        if (floor.isTriggered()){
            floor.setChoosingImagesMode(CHOOSING_IMAGES_MODE);
            if (++executeDelayCount % 20 == 0){
                if (player.getHunger() < 40){
                    player.setY(player.getY()+30);
                }
            }
        }else {
            executeDelayCount = 0;
        }
    }
}
