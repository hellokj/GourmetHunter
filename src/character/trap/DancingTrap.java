package character.trap;

import character.Actor;
import character.Floor;
import frame.scene.Scene;
import util.PainterManager;
import util.ResourcesManager;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class DancingTrap implements Trap {
    private static final String[] imagePaths =
            // 0 : down, 1 : right, 2 : up, 3 : left
            {"floor/DancingFloor_0.png", "floor/DancingFloor_1.png", "floor/DancingFloor_2.png", "floor/DancingFloor_3.png"};
    private static final String imagePath = "floor/Brick.png";
    private static final int[] choosingImageMode = {0};

    private ArrayList<BufferedImage> images;
    private ArrayList<String> imagesPaths;
    private BufferedImage image;
    private ArrayList<Integer> directions;
    private int count = 0;

    public DancingTrap(){
        // 隨機生成不同順序的跳舞機
        images = new ArrayList<>();
        imagesPaths = new ArrayList<>();
        imagesPaths.addAll(Arrays.asList(imagePaths)); // 為了不動到原圖片路徑
        directions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int random = (int)(Math.random()*imagesPaths.size());
            images.add(ResourcesManager.getInstance().getImage(imagesPaths.get(random)));
            directions.add(imagesPaths.get(random).charAt(19) - 48);
            imagesPaths.remove(random);
        }
        image = PainterManager.mergeImages(images);
    }

    @Override
    public void setFloorState(Floor floor) {
        // 加入圖片
        floor.getFloorImages().add(image);
        floor.setDrawWidth(image.getWidth());
        floor.setDrawHeight(image.getHeight());
        floor.setChoosingImagesMode(choosingImageMode);
    }

    @Override
    public void execute(Actor player, Floor floor, Scene scene) {
        // 先記錄角色原始狀態
        // 將角色設為不能移動
        // 輸入完成後再回復原始狀態
        float initialSpeedX = player.getSpeedX();
        player.stop();
//        player.setSpeedX(0);
//        player.setX(player.getX());
        for (int i = 0; i < directions.size(); i++) {
            if (player.dance() == directions.get(0)){
                Scene.CORRECT.play();
                directions.remove(0);
                floor.getFloorImages().remove(0); // 把原圖清掉
                // 製作新圖
                images.set(count++, ResourcesManager.getInstance().getImage(imagePath));
                floor.getFloorImages().add(PainterManager.mergeImages(images)); // 塞新圖回去
            }else {
//                Scene.ERROR.play();
            }
        }
        if (directions.size() == 0){
            player.setStop(false);
            player.setSpeedX(initialSpeedX);
            floor.setCompleted(true);

        }
    }
}
