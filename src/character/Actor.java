package character;

import character.food.Food;
import frame.MainPanel;
import frame.scene.Scene;
import util.ResourcesManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Actor extends AnimationGameObject{
    public static final int MOVE_DOWN = 0;
    public static final int MOVE_RIGHT = 1;
    public static final int MOVE_UP = 2;
    public static final int MOVE_LEFT = 3;
    private static final int[] MOVING_PATTERN = {0, 1, 2, 3, 2, 1}; // 走路模式

    // 胖瘦圖片
    private final int HUNGER_LIMIT = 70;
    private BufferedImage imageFat, imageSlim = ResourcesManager.getInstance().getImage("actor/skeletona.png");

    // 身材速度上限
    private final int MAX_SPEED_FAT = 8;
    private final int MAX_SPEED_SLIM = 10;
    // 身材持續加速度
    private final float ACCELERATION_FAT = 1f;
    private final float ACCELERATION_SLIM = 1.2f;
    // 變換方向初速度
    private final int CHANGE_DIRECTION_INITIAL_SPEED = 2;

    // 角色現在狀態
    private boolean state; // 胖瘦狀態 true:肥 false:瘦
    private boolean isOn; // 當前是否有在階梯上
    private boolean isStop;
    private boolean canJump; // 可以跳
    private boolean invincible; // 無敵時間
    private int direction; // 角色方向
    private boolean dieState; // 死亡狀態
    private boolean isEating; // 吃到東西

    // 每經過100次刷新，讓飢餓值上升
    private int hungerDelayCount, hungerDelay = 120;
    private int hunger; // 飢餓程度
    private int score; // 統計總共吃了多少食物

    // delay
    private int stayDelayCount, stayDelay;
    private int invincibleDelayCount, invincibleDelay = 40;
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
        this.direction = MOVE_DOWN;
        this.imageFat = ResourcesManager.getInstance().getImage(imageName);
        this.hunger = 0;
        this.state = true;
        this.canJump = true;
        this.isEating = false;
        this.isStop = false;
        this.dieState = false;
        setBoundary();
        this.speedX = 0;
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
        Scene.DIE.play();
        this.direction = MOVE_DOWN;
        this.dieState = true;
    }

    public void setImageFat(BufferedImage imageFat) {
        this.imageFat = imageFat;
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
    public int getScore(){
        return this.score;
    }
    public boolean isEating(){
        return isEating;
    }
    public boolean isStop(){
        return this.isStop;
    }
    public void setStop(boolean state){
        this.isStop = state;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public void stop(){
        this.isStop = true;
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
        Scene.JUMP.play();
        int jumpSpeed = 18;
        speedY -= jumpSpeed;
        this.canJump = false;
    }

    @Override
    public void update(){
        // 每經過一定延遲，增加飢餓值
        if (!isOn){
            speedY += Scene.GRAVITY;
        }
        if (!isStop){
            checkMaxSpeed();
            x += speedX;
            y += speedY;
            checkHunger();
            setBoundary();
            if (invincibleDelayCount++ == invincibleDelay){
                invincibleDelayCount = 0;
                invincible = false;
            }
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
        // 每3秒飢餓 + 5
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
//        switch (direction){
//            case MOVE_RIGHT:
//                imageOffsetY = 2;
//                break;
//            case MOVE_LEFT:
//                imageOffsetY = 1;
//                break;
//            case MOVE_UP:
//                imageOffsetY = 3;
//                break;
//            case MOVE_DOWN:
//                imageOffsetY = 0;
//                break;
//        }
//        this.direction = direction;
        // 轉向後設定速度初值
        if (this.direction != direction){
            switch (direction){
                case MOVE_RIGHT:
                    imageOffsetY = 2;
                    speedX = CHANGE_DIRECTION_INITIAL_SPEED;
                    speedX = 0;
                    break;
                case MOVE_LEFT:
                    imageOffsetY = 1;
                    speedX = -CHANGE_DIRECTION_INITIAL_SPEED;
                    speedX = 0;
                    break;
                case MOVE_UP:
                    imageOffsetY = 3;
                    break;
                case MOVE_DOWN:
                    imageOffsetY = 0;
                    break;
            }
//             變換方向
            this.direction = direction;
        }
    }

    public void acceleration() {
        // 持續按壓 加速度
        if (state){
            switch (this.direction){
                case MOVE_RIGHT:
                    if (speedX + ACCELERATION_FAT >= MAX_SPEED_FAT){
                        speedX = MAX_SPEED_FAT;
                    }else {
                        speedX += ACCELERATION_FAT;
                    }
                    break;
                case MOVE_LEFT:
                    if (speedX - ACCELERATION_FAT <= -MAX_SPEED_FAT){
                        speedX = -MAX_SPEED_FAT;
                    }else {
                        speedX -= ACCELERATION_FAT;
                    }
                    break;
            }
        }else {
            switch (this.direction){
                case MOVE_RIGHT:
                    if (speedX + ACCELERATION_SLIM >= MAX_SPEED_SLIM){
                        speedX = MAX_SPEED_SLIM;
                    }else {
                        speedX += ACCELERATION_SLIM;
                    }
                    break;
                case MOVE_LEFT:
                    if (speedX - ACCELERATION_SLIM <= -MAX_SPEED_SLIM){
                        speedX = -MAX_SPEED_SLIM;
                    }else {
                        speedX -= ACCELERATION_SLIM;
                    }
                    break;
            }
        }
    }

    // 飢餓值達到一定程度，切換狀態
    private void checkHunger(){
        if (hunger > HUNGER_LIMIT){
            this.image = imageSlim;
            this.state = false;
//            if (direction == MOVE_RIGHT){
//                this.speedX = this.speedX + 0.2f;
//            }else {
//                this.speedX = this.speedX - 0.2f;
//            }
        }else {
            this.state = true;
            this.image = imageFat;
        }
    }

    // 碰到天花板
    public void touchRoof(){
        if (!this.invincible){
            this.isStop = false;
            this.speedX = 0;
            this.speedY = 0;
            this.direction = MOVE_DOWN;
            this.invincible = true;
            this.y += 30;
            this.setBoundary();
            if (this.hunger + 10 >= 100){
                this.hunger = 100;
                this.die();
            }else {
                this.hunger += 10;
            }
        }
    }

    public boolean checkCollision(Actor target){
//        int nextTop = (int) (this.getTop() - this.speedY),
//                nextBottom = (int) (this.getBottom() + this.speedY),
//                nextLeft = (int) (this.getLeft() - this.speedX),
//                nextRight = (int) (this.getRight() + this.speedX);
//
//        return ((target.right < target.left || target.right > nextLeft) &&
//                (target.bottom < target.top || target.bottom > nextTop) &&
//                (nextRight < nextLeft || nextRight > target.left) &&
//                (nextBottom < target.top || nextBottom > target.top));
        int nextTop = this.getTop(), nextBottom = this.getBottom(), nextLeft = this.getLeft(), nextRight = this.getRight();
        boolean isCollision = false;
        switch (this.getDirection()){
            case MOVE_UP:
                nextTop = (int) (this.getTop() - this.getSpeedY());
                nextBottom = (int) (this.getBottom() - this.getSpeedY());
                break;
            case MOVE_DOWN:
                nextBottom = (int) (this.getBottom() + this.getSpeedY());
                nextTop = (int) (this.getTop() + this.getSpeedY());
                break;
            case MOVE_LEFT:
                nextLeft = (int) (this.getLeft() - this.getSpeedX());
                nextRight = (int) (this.getRight() - this.getSpeedX());
                break;
            case MOVE_RIGHT:
                nextRight = (int) (this.getRight() + this.getSpeedX());
                nextLeft = (int) (this.getLeft() + this.getSpeedX());
                break;
        }
        if(nextTop < target.getBottom()){
            if(nextBottom > target.getTop()){
                if(nextLeft < target.getRight()){
                    if (nextRight > target.getLeft()){
                        isCollision = true;
                    }
                }
            }
        }
        return isCollision;
    }
//
    // 回傳碰撞到的方向
    public int checkCollisionDir(Actor target){
        int nextTop = (int) (this.getTop() - this.speedY),
                nextBottom = (int) (this.getBottom() + this.speedY),
                nextLeft = (int) (this.getLeft() - this.speedX),
                nextRight = (int) (this.getRight() + this.speedX);

        int collisionDir = -1;
        if (checkCollision(target)){
//            回傳自己跟目標撞到的方向
            if (nextBottom > target.y && this.y < target.y){ // 從頭上踩的情況
                return MOVE_DOWN;
            }
            if (nextTop < target.y + target.getDrawHeight() && this.y + this.drawHeight > target.y + target.getDrawHeight()){ // 從下方撞
                return MOVE_UP;
            }
            if (nextRight > target.x && this.x < target.x){ // 從左邊撞
                return MOVE_RIGHT;
            }
            if (nextLeft < target.x + target.getDrawWidth() && this.x + this.drawWidth > target.x + target.getDrawWidth()){ // 從右邊撞
                return MOVE_LEFT;
            }
        }
        return collisionDir; // 回傳-1表示兩者沒接觸
    }

    public boolean checkOnObject(GameObject gameObject){
        // 於物體上
        if(this.modX > gameObject.modX + gameObject.drawWidth * MainPanel.RATIO ||
                this.modX + this.drawWidth * MainPanel.RATIO < gameObject.modX ||
                this.modY + this.drawHeight * MainPanel.RATIO > gameObject.modY + gameObject.drawHeight * MainPanel.RATIO){
            isOn = false;
            return false;
        }
        if(this.modY + this.drawHeight * MainPanel.RATIO + speedY > gameObject.modY && this.speedY >= 0){
            y = gameObject.y - drawHeight + 1; // 需修改
            speedY = gameObject.speedY;
            return true;
        }
        isOn = false;
        return false;
//        if(this.y + (int)(drawHeight*MainPanel.RATIO) + speedY > gameObject.y){
//            y = gameObject.y - drawHeight + 1;
//            speedY = gameObject.speedY;
//            isOn = true;
//            return true;
//        }
//        isOn = false;
//        return false;
    }

    public boolean checkOnFloor(Floor floor, Scene scene){
        // 確認已完全低於此階梯
        // 確認完全走出階梯範圍
        if(this.modX > floor.modX + floor.drawWidth * MainPanel.RATIO ||
                this.modX + this.drawWidth * MainPanel.RATIO < floor.modX ||
                this.modY + this.drawHeight * MainPanel.RATIO > floor.modY + floor.drawHeight * MainPanel.RATIO){
            isOn = false;
            return false;
        }
        // 於階梯上
        if(this.modY + this.drawHeight * MainPanel.RATIO + speedY > floor.modY && this.speedY >= 0){
            y = floor.y - drawHeight + 1; // 需修改
            speedY = floor.speedY;
            // 人物去碰觸地板，將地板狀態設為被接觸，並由地板觸發機關
            floor.isBeenTouched(this, scene);
            return true;
        }
        isOn = false;
        return false;
    }

    // 人物吃
    public boolean eat(Food food){
        if (food != null){
            if (food.left > this.left && food.right < this.right && this.top < food.top && this.bottom > food.top){
                if (this.hunger - food.getHeal() <= 0){
                    this.hunger = 0;
                }else {
                    this.hunger -= food.getHeal();
                }
                score += food.getHeal() * 10;
                food.eaten(); // 狀態設置為被吃掉
                Scene.HEAL.play();
                return true;
            }
        }
        return false;
    }

    // 人物做動作
    public int dance(Scene scene){
        switch (scene.key){
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                return MOVE_RIGHT;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                return MOVE_LEFT;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                return MOVE_UP;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                return MOVE_DOWN;
        }
        return -1;
    }


    @Override
    public void paint(Graphics g, MainPanel mainPanel){
        modX = (int) (x * MainPanel.RATIO);
        modY = (int) (y * MainPanel.RATIO);
        // 人物飢餓值達到一定程度
        // 切換角色圖、速度上升等
        if (state){ // 小胖狀態
            this.drawWidth = this.drawHeight = 32;
            this.imageWidth = this.imageHeight = 32;
            setBoundary();
            g.drawImage(image, modX, modY, modX + (int)(drawWidth*MainPanel.RATIO), modY + (int)(drawHeight*MainPanel.RATIO),
                    direction*4* imageWidth + imageWidth*imageOffsetX, imageOffsetY,
                    direction*4* imageWidth + imageWidth*imageOffsetX + imageWidth,imageOffsetY + imageHeight, null);
//            g2d.setColor(Color.WHITE);
//            g2d.drawRect(modX-1, modY-1, (int)(drawWidth*MainPanel.RATIO + 1), (int)(drawHeight*MainPanel.RATIO +1));
        }else { // 骷髏狀態
            this.drawWidth = 32;
            this.drawHeight = 64;
            this.imageWidth = 32;
            this.imageHeight = 64;
            setBoundary();
            int actualWidth = 24, actualHeight = 48;
            g.drawImage(image, modX, modY, modX + (int)(drawWidth*MainPanel.RATIO), modY + (int)(drawHeight*MainPanel.RATIO),
                    (imageWidth*imageOffsetX),imageOffsetY*imageHeight, imageWidth*imageOffsetX + imageWidth, imageOffsetY*imageHeight + imageHeight, null);
//            g2d.setColor(Color.WHITE);
//            g2d.drawRect(modX-1, modY-1, (int)(drawWidth*MainPanel.RATIO + 1), (int)(drawHeight*MainPanel.RATIO +1));
//            g2d.setColor(Color.RED);
//            g2d.drawRect(modX + 4 - 1, modY + 16 - 1, actualWidth, actualHeight);
        }
    }
}
