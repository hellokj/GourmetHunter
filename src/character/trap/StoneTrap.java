package character.trap;

import character.Actor;
import character.Floor;
import util.ResourcesManager;

public class StoneTrap implements Trap {
    public static final int generationRate = 60;

//    private static final String[] imagePaths = {"floor/StoneFloor_0.png", "floor/StoneFloor_1.png"};
    private static final String[] imagePaths = {"floor/StoneFloor.png"};
//    private static final int[] choosingImagesMode = {0, 1};
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


    private int executeDelayCount, executeDelay = 50;
    @Override
    public void execute(Actor player, Floor floor) {
        if (++executeDelayCount % executeDelay == 0){
            // 扣血機制
            player.setHunger(player.getHunger() + 3);
        }
    }
}
