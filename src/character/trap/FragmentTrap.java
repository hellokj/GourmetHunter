package character.trap;

import character.Actor;
import character.Floor;
import frame.scene.DebuggerScene;
import frame.scene.Scene;
import util.ResourcesManager;

public class FragmentTrap implements Trap {
    private final String[] imagePaths = {"floor/Brick.png"};
    public static final int[] CHOOSING_IMAGES_MODE_BASE = {0};

    private int executeDelayCount;

    @Override
    public void setFloorState(Floor floor) {
        for (int i = 0; i < imagePaths.length; i++) {
            floor.getFloorImages().add(ResourcesManager.getInstance().getImage(imagePaths[i]));
        }
        // 設定基礎圖寬高
        floor.setDrawWidth(floor.getFloorImages().get(0).getWidth());
        floor.setDrawHeight(floor.getFloorImages().get(0).getHeight());
        // 設定選圖模式
        floor.setChoosingImagesMode(CHOOSING_IMAGES_MODE_BASE);
        // 繪製動畫延遲
//        floor.setDrawingDelay(20);
        executeDelayCount = 0;
    }

    @Override
    public void execute(Actor player, Floor floor, Scene scene) {
        // 判定角色狀態or站立時間
        // 判定碰觸到的是哪塊
        // 從那塊開始掉落->再來是那塊的兩側->最後一塊掉落
        if (floor.isTriggered()){
            if (++executeDelayCount % 20 == 0){
                floor.setSpeedY(2);
            }
        }
    }
}
