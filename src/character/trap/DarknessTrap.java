package character.trap;

import character.Actor;
import character.Floor;
import frame.scene.Scene;
import util.ResourcesManager;

import java.awt.*;

public class DarknessTrap implements Trap{
    private static final String[] imagePaths = {"floor/DarknessFloor.png"};
    private static final int[] choosingImagesMode = {0};
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
        // 以玩家為中心，一定半徑內光亮，其餘黑暗
        Point center = player.getCenterPoint();

    }
}
