package character.trap;

import character.Actor;
import character.Floor;
import frame.scene.Scene;
import util.ResourcesManager;

public class FlippingTrap implements Trap {
    private static final String[] imagePaths = {"floor/FlippingFloor_1.png", "floor/FlippingFloor_2.png"};
    private static final int[] choosingImagesMode = {0, 1};

    @Override
    public void setFloorState(Floor floor) {
        for (int i = 0; i < imagePaths.length; i++) {
            floor.getFloorImages().add(ResourcesManager.getInstance().getImage(imagePaths[i]));
        }
        // 設定基礎圖寬高
        floor.setDrawWidth(floor.getFloorImages().get(0).getWidth());
        floor.setDrawHeight(floor.getFloorImages().get(0).getHeight());
        // 設定選圖模式
        floor.setChoosingImagesMode(choosingImagesMode);
        // 繪製動畫延遲
        floor.setDrawingDelay(20);
    }

    @Override
    public void execute(Actor player, Floor floor, Scene scene) {
        // 踩到翻轉，翻後掉落

    }
}
