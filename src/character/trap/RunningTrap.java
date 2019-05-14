package character.trap;

import character.Actor;
import character.Floor;
import frame.scene.Scene;
import util.ResourcesManager;

public class RunningTrap implements Trap {
    private final String[] imagePaths =
            {"floor/RunningFloor_1.png", "floor/RunningFloor_2.png", "floor/RunningFloor_3.png", "floor/RunningFloor_4.png"};
    private boolean rotateDirection; // 轉動的方向 順時針or逆時針
    // 不同轉動方向，不同的選圖模式
    private final int[] CHOOSING_MODE_CLOCKWISE = {0, 1, 2, 3};
    private final int[] CHOOSING_MODE_COUNTERCLOCKWISE = {3, 2, 1, 0};
    private float rotateSpeed; // 本身的轉速 影響人物增減的速度

    public RunningTrap(){
        // 轉動的方向
        if ((int)(Math.random()*100) % 2 == 0){
            rotateDirection = true; // 順時針
        }else {
            rotateDirection = false; // 逆時針
        }
        // 預設為轉速2
        this.rotateSpeed = 2;
    }

    public RunningTrap(float rotateSpeed){
        this();
        // 也可以自行設定轉速
        this.rotateSpeed = rotateSpeed;
    }

    @Override
    public void setFloorState(Floor floor) {
        // 設定圖片
        for (int i = 0; i < imagePaths.length; i++) {
            floor.getFloorImages().add(ResourcesManager.getInstance().getImage(imagePaths[i]));
        }
        // 不同轉動方向，不同的選圖模式
        if(rotateDirection){ // 順時針
            floor.setChoosingImagesMode(CHOOSING_MODE_CLOCKWISE);
        }else { // 逆時針
            floor.setChoosingImagesMode(CHOOSING_MODE_COUNTERCLOCKWISE);
        }
        // 設定基礎圖寬高
        floor.setDrawWidth(floor.getFloorImages().get(0).getWidth());
        floor.setDrawHeight(floor.getFloorImages().get(0).getHeight());
        // 繪製動畫延遲
        floor.setDrawingDelay(3);
    }

    @Override
    public void execute(Actor player, Floor floor, Scene scene) {
        if (floor.isTriggered()){ // 觸發
            System.out.println(player.getSpeedX());
            if (rotateDirection){ // 順時針
                if (player.getSpeedX() < 1){
                    player.setX((int) (player.getX() + rotateSpeed));
                }
                if (player.getDirection() == Actor.MOVE_RIGHT){
                    if (player.getSpeedX() != player.getMaxSpeed()){
                        player.setSpeedX(player.getSpeedX() + player.getAcceleration() + rotateSpeed);
                    }else {
                        player.setSpeedX(player.getMaxSpeed() + rotateSpeed);
                    }
                }else if (player.getDirection() == Actor.MOVE_LEFT){
                    if (player.getSpeedX() != rotateSpeed){
                        player.setSpeedX(player.getSpeedX() + (rotateSpeed - player.getAcceleration()));
                    }else {
                        player.setSpeedX(rotateSpeed);
                    }
                }
            }else { // 逆時針
                if (player.getSpeedX() > -1){
                    player.setX((int) (player.getX() - rotateSpeed));
                }
                if (player.getDirection() == Actor.MOVE_RIGHT){
                    if (player.getSpeedX() != -rotateSpeed){
                        player.setSpeedX(player.getSpeedX() - (rotateSpeed - player.getAcceleration()));
                    }else {
                        player.setSpeedX(-rotateSpeed);
                    }
                }else if (player.getDirection() == Actor.MOVE_LEFT){
                    if (player.getSpeedX() != -player.getMaxSpeed()){
                        player.setSpeedX(player.getSpeedX() - player.getAcceleration() - rotateSpeed);
                    }else {
                        player.setSpeedX(-player.getMaxSpeed() - rotateSpeed);
                    }
                }
            }
        }
    }
}
