package character;

import character.food.Food;
import frame.scene.GameScene;
import frame.scene.Scene;
import util.ResourcesManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Actor extends AnimationGameObject{
    public static final int MOVE_DOWN = 0;
    public static final int MOVE_RIGHT = 1;
    public static final int MOVE_UP = 2;
    public static final int MOVE_LEFT = 3;
    private static final int[] MOVING_PATTERN = {0, 1, 2, 3, 2, 1}; // 走路模式

    // 胖瘦圖片
    private static final int HUNGER_LIMIT = 80;
    private static BufferedImage imageFat = ResourcesManager.getInstance().getImage("actor/Actor1.png");
    private static BufferedImage imageSlim = ResourcesManager.getInstance().getImage("actor/skeletona.png");

    // 身材速度上限
    private static final int MAX_SPEED_FAT = 10;
    private static final int MAX_SPEED_SLIM = 15;
    // 身材持續加速度
    private static final float ACCELERATION_FAT = 0.3f;
    private static final float ACCELERATION_SLIM = 0.5f;
    // 變換方向初速度
    private static int CHANGE_DIRECTION_INITIAL_SPEED = 3;

    // 角色現在狀態
    private boolean state; // 胖瘦狀態 true:肥 false:瘦
    private boolean isOn; // 當前是否有在階梯上
    private boolean canJump; // 可以跳
    private boolean invincible; // 無敵時間
    private int direction; // 角色方向
    private boolean dieState; // 死亡狀態

    // 每經過100次刷新，讓飢餓值上升
    private int hungerDelayCount, hungerDelay = 20;
    private int hunger; // 飢餓程度

    // delay
    private int stayDelayCount, stayDelay;
    private int invincibleDelayCount, invicibleDelay = 10;
    private int jumpCount = 20;

    public Actor(int x, int y, int drawWidth, int drawHeight){
        super(x, y, drawWidth, drawHeight);
        this.direction = MOVE_RIGHT;
        this.hunger = 0;
        this.state = true;
        this.canJump = true;
        this.image = imageFat;
    }

    public Actor(int x, int y, int drawWidth, int drawHeight, int imageWidth, int imageHeight, String imageName) {
        super(x, y, drawWidth, drawHeight, imageWidth, imageHeight, imageName);
        this.direction = MOVE_RIGHT;
        this.hunger = 0;
        this.state = true;
        this.canJump = true;
    }

    // getter and setter
    public int getDirection(){
        return this.direction;
    }
    public int getHunger() {
        return hunger;
    }
    public void setHunger(int hunger) {
        this.hunger = hunger;
    }
    public boolean isDie(){
        return this.dieState;
    }
    public void die(){
        this.direction = MOVE_DOWN;
        this.dieState = true;
    }
    public boolean canJump() {
        return canJump;
    }
    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }
    public float getAcceleration(){
        if (state){
            return ACCELERATION_FAT;
        }
        return ACCELERATION_SLIM;
    }
    public int getMaxSpeed(){
        if (state){
            return MAX_SPEED_FAT;
        }
        return MAX_SPEED_SLIM;
    }

    public void reset(){
        this.speedY = 0;
        this.state = true;
        this.dieState = false;
        this.hunger = 0;
        this.x = 250;
        this.y = 100;
    }

    public void jump(){
        int jumpSpeed = 14;
        speedY -= jumpSpeed;
        this.canJump = false;
    }

    @Override
    public void update(){
        // 每經過一定延遲，增加飢餓值
        if (!isOn){
            speedY += Scene.GRAVITY;
        }
        checkMaxSpeed();
        x += speedX;
        y += speedY;
        checkHunger();
        setBoundary();
        if (invincibleDelayCount++ == invicibleDelay){
            invincibleDelayCount = 0;
            invincible = false;
        }
    }

    private void checkMaxSpeed() {
        // 確認速度上限
        if (state){
            if (speedX >= MAX_SPEED_FAT){
                speedX = MAX_SPEED_FAT;
            }
            if (speedX <= -MAX_SPEED_FAT){
                speedX = -MAX_SPEED_FAT;
            }
        }else {
            if (speedX >= MAX_SPEED_SLIM){
                speedX = MAX_SPEED_SLIM;
            }
            if (speedX <= -MAX_SPEED_SLIM){
                speedX = -MAX_SPEED_SLIM;
            }
        }
    }

    public void hunger() {
        if (hungerDelayCount++ == hungerDelay){
            if (this.hunger + 5 >= 100){
                this.hunger = 100;
                this.die();
            }else {
                this.hunger += 5; // 延遲到了，增加飢餓值
            }
            hungerDelayCount = 0;
        }
    }

    // 原地踏步
    public void stay(){
        stayDelay = 8; // 設定原地踏步延遲機制
        if(stayDelayCount++ % stayDelay == 0){
            this.imageOffsetX = MOVING_PATTERN[choosingImagesCounter % MOVING_PATTERN.length];
            this.choosingImagesCounter = choosingImagesCounter % MOVING_PATTERN.length;
            this.choosingImagesCounter++;
        }
    }

    public void changeDir(int direction){
        // 轉向後設定速度初值
        if (this.direction != direction){
            switch (direction){
                case MOVE_RIGHT:
                    imageOffsetY = 2;
                    speedX = CHANGE_DIRECTION_INITIAL_SPEED;
//                    speedX = 0;
                    break;
                case MOVE_LEFT:
                    imageOffsetY = 1;
                    speedX = -CHANGE_DIRECTION_INITIAL_SPEED;
//                    speedX = 0;
                    break;
                case MOVE_UP:
                    imageOffsetY = 3;
                    break;
                case MOVE_DOWN:
                    imageOffsetY = 0;
                    break;
            }
        }
        // 變換方向
        this.direction = direction;
        // 持續按壓 加速度
        if (state){
            switch (direction){
                case MOVE_RIGHT:
                    speedX += ACCELERATION_FAT;
                    break;
                case MOVE_LEFT:
                    speedX -= ACCELERATION_FAT;
                    break;
            }
        }else {
            switch (direction){
                case MOVE_RIGHT:
                    speedX += ACCELERATION_SLIM;
                    break;
                case MOVE_LEFT:
                    speedX -= ACCELERATION_SLIM;
                    break;
            }
        }
    }

    // 飢餓值達到一定程度，切換狀態
    private void checkHunger(){
        if (hunger > HUNGER_LIMIT){
            this.image = imageSlim;
            this.state = false;
            if (direction == MOVE_RIGHT){
                this.speedX = this.speedX + 0.2f;
            }else {
                this.speedX = this.speedX - 0.2f;
            }
        }else {
            this.state = true;
            this.image = imageFat;
        }
    }

    // 碰到天花板
    public void touchRoof(){
        if (!this.invincible){
            this.speedX = 0;
            this.speedY = 0;
            this.direction = MOVE_DOWN;
            this.invincible = true;
            this.y += 30;
            this.setBoundary();
            if (this.hunger + 20 >= 100){
                this.hunger = 100;
                this.die();
            }else {
                this.hunger += 20;
            }
        }
    }

    public boolean checkOnObject(GameObject gameObject){
        // 於物體上
        if(this.bottom + speedY > gameObject.top){
            y = gameObject.top - drawHeight;
            speedY = gameObject.speedY;
            isOn = true;
            return true;
        }
        isOn = false;
        return false;
    }

    public boolean checkOnFloor(Floor floor){
        // 確認已完全低於此階梯
        // 確認完全走出階梯範圍
        if(this.left > floor.right || this.right < floor.left || this.bottom > floor.bottom){
            isOn = false;
            return false;
        }
        // 於階梯上
        if(this.bottom + speedY > floor.top){
            y = floor.top - drawHeight; // 需修改
            speedY = floor.speedY;
            isOn = true;
            // 人物去碰觸地板，將地板狀態設為被接觸，並由地板觸發機關
            floor.isBeenTouched(this);
            // 吃食物機制
            if (eat(floor.getFood())){
                floor.setFood(null); // 吃完，食物設回null
            }
            return true;
        }
        isOn = false;
        return false;
    }

    // 人物吃
    private boolean eat(Food food){
        if (food != null){
            if (food.left > this.left && food.right < this.right && this.top < food.top && this.bottom == food.bottom){
                if (this.hunger - food.getHeal() <= 0){
                    this.hunger = 0;
                }else {
                    this.hunger -= food.getHeal();
                }
                food.eaten(); // 狀態設置為被吃掉
                return true;
            }
        }
        return false;
    }

    // 人物做動作
    public int dance(){
        return direction;
    }


    @Override
    public void paint(Graphics g){
        // 人物飢餓值達到一定程度
        // 切換角色圖、速度上升等
        if (state){ // 小胖狀態
            this.drawWidth = this.drawHeight = 32;
            g.drawImage(image, x, y, x + drawWidth, y + drawHeight,
                    direction*4* drawWidth + drawWidth*imageOffsetX, imageOffsetY,
                    direction*4* drawWidth + drawWidth*imageOffsetX + drawWidth, imageOffsetY + drawHeight, null);
        }else { // 骷髏狀態
            this.drawWidth = 32;
            this.drawHeight = 64;
            g.drawImage(image, x, y, x + drawWidth, y + drawHeight,
                    drawWidth*imageOffsetX,imageOffsetY*drawHeight, drawWidth*imageOffsetX + drawWidth, imageOffsetY*drawHeight + drawHeight, null);
        }
    }
}
