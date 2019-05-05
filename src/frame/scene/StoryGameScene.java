package frame.scene;

import character.*;
import character.Button;
import character.trap.TrapGenerator;
import frame.GameFrame;
import frame.MainPanel;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class StoryGameScene extends Scene {
    private GameObject background_0, background_1, background_end, roof;
    private GameObject hungerCount, hungerBack;
    private int hungerValue;
    private GameObject endingFloor;
    private AnimationGameObject endingGate;
    private Actor player;
    private ArrayList<Floor> floors;
    private FloorGenerator floorGenerator;

    // 選單相關
    private boolean isCalled;
    private boolean isPause;
    private GameObject cursor; // 光標
    private Button button_resume, button_menu, button_new_game; // 三個按鈕

    // 顯示板
    private GameObject hungerLabel, timeLabel;

    private int time;
    private int minute, second; // 印出時間
    private String colon; // 冒號
    private int timeCount; // 時間刷新delay

    private boolean isOver; // 結束

    private int key; // 鍵盤輸入值
    private int count; // 死亡跳起計數器

    private boolean up = false, down = false, left = false, right = false;

    public StoryGameScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        floorGenerator = new FloorGenerator();
        // 場景物件
        setSceneObject();
        roof = new GameObject(0, 0, 500, 64, "background/Roof_new.png");
        endingFloor = new GameObject(0, 1200+20, 500, 32, "floor/EndingFloor.png");
        endingGate = new AnimationGameObject(300, endingFloor.getTop() - 64, 64, 64, 64, 64, "background/Door.png");
        player = new Actor(250, 200, 32, 32, 32, 32, "actor/Actor1.png");
        // 顯示板
        hungerLabel = new GameObject(28,8, 64, 32,64, 32, "background/HungerLabel.png");
        timeLabel = new GameObject(300 ,8,64, 32,64, 32, "background/TimeLabel.png");
        // 飢餓值
        hungerBack = new GameObject(96, 16, 100, 16, "background/Hunger.png");
        hungerCount = new GameObject(96, 16, 0, 16, "background/HungerCount.png");
        // 時間相關
        time = 20;
        colon = " : ";
        // 初始10塊階梯
        floors = new ArrayList<>();
        floors.add(new Floor(player.getX() - (64 - 32), 200 + 32, TrapGenerator.getInstance().genSpecificTrap(0))); // 初始站立
        for (int i = 0; i < 9; i++) {
            floors.add(floorGenerator.genFloor(floors.get(i), 0));
        }
        isOver = false;
        isCalled = false;
        isPause = false;
    }

    private void setSceneObject() {
        background_0 = new GameObject(0, -22, 500, 700, "background/EgyptBackground_0.png");
        background_1 = new GameObject(0, -22 + 700, 500, 700, "background/EgyptBackground_0.png");
        background_end = new GameObject(0, -22 + 700, 500, 700, "background/EgyptBackground_1.png");
    }

    @Override
    public KeyListener genKeyListener() {

        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                key = e.getKeyCode();
                switch (e.getKeyCode()){
                    // p1 controller
                    case KeyEvent.VK_RIGHT:
                        if (!isPause){
                            right = true;
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (!isPause){
                            left = true;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (!isPause){
                            up = true;
                        }else {
                            if (!(cursor.getY() - 150 < button_resume.getY())){
                                cursor.setY(cursor.getY() - 150);
                            }
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (!isPause){
                            down = true;
                        }else {
                            if (!(cursor.getY() + 150 > button_menu.getBottom())){
                                cursor.setY(cursor.getY() + 150);
                            }
                        }
                        break;
                    case KeyEvent.VK_R:
                        player.reset();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        if (isPause){
                            resume();
                            isCalled = false;
                        }else {
                            pause();
                            menu();
                        }
                        break;
                    case KeyEvent.VK_SPACE:
                        if (isPause){
                            Button chooser = checkCursorPosition();
                            if (chooser == button_resume){
                                resume();
                                isCalled = false;
                            }
                            if (chooser == button_menu){
                                gsChangeListener.changeScene(MainPanel.MENU_SCENE);
                                isCalled = false;
                            }
                            if (chooser == button_new_game){
                                reset();
                                isCalled = false;
                            }
                        }
                }
            }

            @Override
            public  void keyReleased(KeyEvent e){
                if (!isPause){
                    switch (e.getKeyCode()){
                        case KeyEvent.VK_RIGHT:
                            right = false;
                            break;
                        case KeyEvent.VK_LEFT:
                            left = false;
                            break;
                        case KeyEvent.VK_UP:
                            up = false;
                            break;
                        case KeyEvent.VK_DOWN:
                            down = false;
                            break;
                    }
                }
            }
        };
    }

    @Override
    public void logicEvent() {
        if (!isPause){
            if (!player.isDie()){ // 還沒死亡的狀態
                MainPanel.checkLeftRightBoundary(player);
                changeDirection();
                int floorAmount = checkSceneFloorAmount();
                hungerValue = player.getHunger();
                if (floorAmount < 10 && time >= 5 && floors.size() < 15){
                    for (int i = 0; i < 10 - floorAmount; i++) {
                        floors.add(floorGenerator.genFloor(findLast(), 0));
                    }
                }
                // 逆向摩擦力
                friction(player);
                if ((right || left) && !player.isStop()){
                    player.acceleration();
                }
                if (checkTopBoundary(player)){
                    player.touchRoof();
                }else {
                    player.stay();
                }
                for (int i = 0; i < floors.size(); i++) {
                    player.checkOnFloor(floors.get(i));
                    // 吃食物機制
                    if (player.eat(floors.get(i).getFood())){
                        floors.get(i).setFood(null); // 吃完，食物設回null
                    }
                    floors.get(i).stay();
                    if (checkTopBoundary(floors.get(i))){
                        floors.remove(i);
                    }
                }

                // 人物飢餓
//            player.hunger();
                // 繪製現在飢餓值
                hungerCount.setDrawWidth(player.getHunger());
                // 每次都要更新此次座標
                for (Floor floor : floors) {
                    floor.update();
                }
                player.update();
                // 掉落死亡 or 餓死後落下
                if (player.getBottom() > GameFrame.FRAME_HEIGHT){
                    player.die();
                }
                // 時間刷新
                updateTime();
                if (time < 5){
                    isOver = true;
                    endingGate.setBoundary();
                }
                if (isOver){
                    if (player.checkOnObject(endingFloor)){
                        player.stay();
                        player.setSpeedY(0);
                    }
                }
                endingFloor.setBoundary();
            }else {
                // 死亡跳起後落下
                if (count++ < 20){
                    player.setSpeedX(0);
                    player.setY(player.getY()-5);
                }else {
                    player.setSpeedX(0);
                    player.update();
                    // 完全落下後切場景
                    if (player.getBottom() > GameFrame.FRAME_HEIGHT){
                        gsChangeListener.changeScene(MainPanel.GAME_OVER_SCENE);
                    }
                }
            }
            if (background_end.getBottom() > GameFrame.FRAME_HEIGHT){
                updateBackgroundImage();
            }
            if (endingGate != null){
                endingGate.setBoundary();
                if (endingGate.checkCollision(player)){
                    endingGate.playAnimation();
                    if (key == KeyEvent.VK_UP){
                        gsChangeListener.changeScene(MainPanel.END_SCENE);
                    }
                }
            }
        }

    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        background_0.paint(g);
        background_1.paint(g);
        background_end.paint(g);
        roof.paint(g);
        hungerLabel.paint(g);
        timeLabel.paint(g);
        hungerBack.paint(g);
        hungerCount.paint(g);
        for (Floor floor : floors) {
            floor.paint(g);
        }
        if (isOver){
            endingFloor.paint(g);
            endingGate.paint(g);
        }
        player.paint(g);

        if (isCalled){
            button_menu.paint(g);
            button_resume.paint(g);
            button_new_game.paint(g);
            cursor.paint(g);
        }

        // 印出時間
        Font font = g.getFont().deriveFont(16.0f);
        g.setFont(font);
        g.setColor(Color.RED);
        FontMetrics fm = g.getFontMetrics();
        int msgWidth = fm.stringWidth(String.valueOf(minute));
        int msgAscent = fm.getAscent();
        g.drawString(String.valueOf(minute), 384, 30);
        g.drawString(colon, 384 + msgWidth, 30);
        g.drawString(String.valueOf(second), 384 + 2*msgWidth, 30);
        g.drawString(String.valueOf(hungerValue), 96 + 112 + 10, 30);
    }

    // 比天花板高就消失
    private boolean checkTopBoundary(GameObject gameObject){
        return gameObject.getTop() <= this.roof.getBottom();
    }

    // 確認畫面中階梯數量
    private int checkSceneFloorAmount(){
        int count = 0;
        for (int i = 0; i < floors.size(); i++) {
            Floor current = floors.get(i);
            if (current.getTop() > 0 && current.getBottom() < GameFrame.FRAME_HEIGHT){
                count++;
            }
        }
        return count;
    }

    // 更新背景圖
    private void updateBackgroundImage(){
        if (background_0.getBottom() < 0){
            background_0 = new GameObject(0, 678, 500, 700, "background/EgyptBackground_0.png");
        }
        if (background_1.getBottom() < 0){
            background_1 = new GameObject(0, 678, 500, 700, "background/EgyptBackground_0.png");
        }
        background_0.setY(background_0.getY() - 3);
        background_1.setY(background_1.getY() - 3);
        if (time <= 6){
            background_end.setY(background_end.getY() - 3);
        }
        background_0.setBoundary();
        background_1.setBoundary();
        background_end.setBoundary();
        if (time <= 5){
            endingFloor.setY(endingFloor.getY() - 3);
            endingGate.setY(endingGate.getY() - 3);
        }
    }

    // 更新時間
    private void updateTime(){
        if (time < 0){
            return;
        }
        minute = time / 60;
        second = time % 60;
        if(++timeCount % 40 == 0){
            time -= 1;
        }
    }

    // 找到最後一塊
    private Floor findLast(){
        return floors.get(floors.size() - 1);
    }

    // 重新開始遊戲
    private void reset(){
        gsChangeListener.changeScene(MainPanel.STORY_GAME_SCENE);
    }

    // 跳出選單
    private void menu(){
        isCalled = true;
        if (button_resume == null){
            button_resume = new Button(175, 150, 150, 100, 150, 100, "button/Button_Resume.png");
        }
        if (button_new_game == null){
            button_new_game = new Button(175, 300, 150, 100, 150, 100,"button/Button_NewGame.png");
        }
        if (button_menu == null){
            button_menu = new Button(175, 450, 150, 100, 150, 100, "button/Button_Menu.png");
        }
        if (cursor == null){
            cursor = new GameObject(100, 150 + 25, 50, 50, 168, 140, "background/Cursor.png");
        }
    }

    private void pause(){
        isPause = true;
    }

    private void resume(){
        isPause = false;
    }

    private Button checkCursorPosition(){
        Point cursorCenterPoint = cursor.getCenterPoint();
        if (cursorCenterPoint.y < button_resume.getBottom() && cursorCenterPoint.y > button_resume.getTop()){
            return button_resume;
        }
        if (cursorCenterPoint.y < button_new_game.getBottom() && cursorCenterPoint.y > button_new_game.getTop()){
            return button_new_game;
        }
        if (cursorCenterPoint.y < button_menu.getBottom() && cursorCenterPoint.y > button_menu.getTop()){
            return button_menu;
        }
        return null;
    }

    private void changeDirection(){
        if (!right && !left && !up && down){
            player.changeDir(Actor.MOVE_DOWN);
        }else if (!right && !left && up && !down){
            player.changeDir(Actor.MOVE_UP);
        }else if (!right && left && !up && !down){
            player.changeDir(Actor.MOVE_LEFT);
        }else if (right && !left && !up && !down){
            player.changeDir(Actor.MOVE_RIGHT);
        }
    }
}
