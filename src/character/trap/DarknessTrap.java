package character.trap;

import character.Actor;
import character.Floor;
import frame.scene.Scene;
import util.ResourcesManager;

public class DarknessTrap implements Trap{
    private final String[] imagePaths = {"floor/BlackFloor.png"};
    private final int[] choosingImagesMode = {0};
    private int executeDelayCount;
    @Override
    public void setFloorState(Floor floor) {
        for (int i = 0; i < imagePaths.length; i++) {
            floor.getFloorImages().add(ResourcesManager.getInstance().getImage(imagePaths[i]));
        }
//        floor.setSpeedY(-1);
        // 設定基礎圖寬高
        floor.setDrawWidth(floor.getFloorImages().get(0).getWidth());
        floor.setDrawHeight(floor.getFloorImages().get(0).getHeight());
        // 設定選圖模式
        floor.setChoosingImagesMode(choosingImagesMode);
        // 繪製動畫延遲
//        floor.setDrawingDelay(20);
        executeDelayCount = 0;
    }

    @Override
    public void execute(Actor player, Floor floor, Scene scene) {
        // 以玩家為中心，一定半徑內光亮，其餘黑暗
        player.setOn(true);
        scene.darkDelay = 0;
        scene.touchedPlayer = player;
        scene.setIsDark(true);
    }
}
