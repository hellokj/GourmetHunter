
package character.trap;

import character.Actor;
import character.Floor;
import character.Flash;
import frame.scene.Scene;
import util.ResourcesManager;

public class FlashTrap implements Trap{
    private final String[] imagePaths = {"floor/FlashFloor.png"};
    private final int[] choosingImagesMode = {0};
    public static Flash flash;
    public static boolean flashstate;

    public void setFloorState(Floor floor){
        floor.setImage(ResourcesManager.getInstance().getImage("floor/FlashFloor.png"));
        for (int i = 0; i < imagePaths.length; i++) {
            floor.getFloorImages().add(ResourcesManager.getInstance().getImage(imagePaths[i]));
        }
        flashstate = false;
        // 設定基礎圖寬高
        floor.setDrawWidth(floor.getFloorImages().get(0).getWidth());
        floor.setDrawHeight(floor.getFloorImages().get(0).getHeight());
        // 設定選圖模式
        floor.setChoosingImagesMode(choosingImagesMode);
        // 繪製動畫延遲
        floor.setDrawingDelay(20);
    }
    public void execute(Actor player, Floor floor, Scene scene){
        flash = new Flash(floor.getX()-220,floor.getY()-250,500,500);
        if (floor.isTriggered()){

            flashstate = true;
        }
        
    }
    public static Flash getFlash(){
        return flash;
    }
    public static boolean getFlashState(){
        return flashstate;
    }
    public static void setFlashState(boolean boo){
         flashstate = boo;
    }
}
