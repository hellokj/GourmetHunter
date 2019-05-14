package character.trap;

import character.Actor;
import character.Floor;
import frame.scene.Scene;
import util.ResourcesManager;

public class StoneTrap implements Trap {
    private final String[] imagePaths = {"floor/StoneFloor.png"};
    private static final int[] choosingImagesMode = {0 ,0};

    @Override
    public void setFloorState(Floor floor) {
        floor.setImage(ResourcesManager.getInstance().getImage("floor/StoneFloor.png"));
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


    private int executeDelayCount, executeDelay = 50;
    @Override
    public void execute(Actor player, Floor floor, Scene scene) {
        if (++executeDelayCount % executeDelay == 0){
            // 扣血機制
            player.setHunger(player.getHunger() + 3);
        }
    }
}
