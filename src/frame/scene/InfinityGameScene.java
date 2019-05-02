package frame.scene;

import character.*;
import character.Button;
import character.trap.TrapGenerator;
import frame.GameFrame;
import frame.MainPanel;
import sun.applet.Main;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

public class InfinityGameScene extends Scene {
    private GameObject background_0, background_1, roof;
    private GameObject hungerCount, hungerBack;
    private int hungerValue;
    private AnimationGameObject fire_left0, fire_right0, fire_left1, fire_right1;
    private Actor player;
    private ArrayList<Floor> floors;
    private FloorGenerator floorGenerator;
    private ArrayList<AnimationGameObject> fires_left, fires_right;

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

    public InfinityGameScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        floorGenerator = new FloorGenerator();
        // 場景物件
        setSceneObject();
        roof = new GameObject(0, 0, 500, 64, "background/Roof_new.png");
        player = new Actor(250, 200, 32, 32);
        // 顯示板
        hungerLabel = new GameObject(28,8, 64, 32,64, 32, "background/HungerLabel.png");
        timeLabel = new GameObject(300 ,8,64, 32,64, 32, "background/TimeLabel.png");
        // 飢餓值
        hungerBack = new GameObject(96, 16, 112, 16, "background/Hunger.png");
        hungerCount = new GameObject(96, 16, 112, 16, "background/HungerCount.png");
        // 時間相關
        time = 20;
        colon = " : ";
        // 初始10塊階梯
        floors = new ArrayList<>();
        floors.add(new Floor(player.getX() - (64 - 32), 200 + 32, TrapGenerator.getInstance().genSpecificTrap(0))); // 初始站立
        for (int i = 0; i < 9; i++) {
            floors.add(floorGenerator.genFloor(floors.get(i)));
        }
        isOver = false;
        isCalled = false;
        isPause = false;
    }

    private void setSceneObject() {
        background_0 = new GameObject(0, -22, 500, 700, "background/EgyptBackground_0.png");
        background_1 = new GameObject(0, -22 + 700, 500, 700, "background/EgyptBackground_0.png");
//        fire_left0 = new AnimationGameObject(0, 0, 30, 30, 64, 64,"background/Fire.png");
//        fire_right0 = new AnimationGameObject(470, 300, 30, 30, 64, 64,"background/Fire.png");
//        fire_left1 = new AnimationGameObject(0, 600, 30, 30, 64, 64,"background/Fire.png");
//        fire_right1 = new AnimationGameObject(470, 900, 30, 30, 64, 64,"background/Fire.png");
//
//        fires_left = new ArrayList<>();
//        fires_right = new ArrayList<>();
//        fires_left.add(fire_left0);
//        fires_right.add(fire_right0);
//        fires_left.add(fire_left1);
//        fires_right.add(fire_right1);
    }

    @Override
    public KeyListener genKeyListener() {

        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                key = e.getKeyCode();
                switch (key){
                    case KeyEvent.VK_RIGHT:
                        if (!isPause){
                            player.changeDir(Actor.MOVE_RIGHT);
                            player.setSpeedX(player.getSpeedX()+3);
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (!isPause){
                            player.changeDir(Actor.MOVE_LEFT);
                            player.setSpeedX(player.getSpeedX()-3);
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (!isPause){
                            player.changeDir(Actor.MOVE_UP);
                        }else {
                            if (!(cursor.getY() - 150 < button_resume.getY())){
                                cursor.setY(cursor.getY() - 150);
                            }
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (!isPause){
                            player.changeDir(Actor.MOVE_DOWN);
                        }else {
                            if (!(cursor.getY() + 150 > button_menu.getBottom())){
                                cursor.setY(cursor.getY() + 150);
                            }
                        }
                        break;
                    case KeyEvent.VK_R:
//                        player.reset();
//                        reset();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        if (isPause){
                            resume();
                            isCalled = false;
                        }else {
                            pause();
//                        gsChangeListener.changeScene(MainPanel.MENU_SCENE);
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
//                player.setSpeedX(0);
            }
        };
    }

    @Override
    public void logicEvent() {
        if (!isPause){
            if (!player.isDie()){ // 還沒死亡的狀態
                MainPanel.checkLeftRightBoundary(player);
                int floorAmount = checkSceneFloorAmount();
                hungerValue = player.getHunger();
                if (floorAmount < 10 && time >= 5 && floors.size() < 15){
                    for (int i = 0; i < 10 - floorAmount; i++) {
                        floors.add(floorGenerator.genFloor(findLast()));
                    }
                }
                // 逆向摩擦力
                friction(player);
                if (checkTopBoundary(player)){
                    player.touchRoof();
                }else {
                    player.stay();
                }
                for (int i = 0; i < floors.size(); i++) {
                    player.checkOnFloor(floors.get(i));
                    floors.get(i).stay();
                    if (checkTopBoundary(floors.get(i))){
                        floors.remove(i);
                    }
                }
                // 火把
//            for (AnimationGameObject fire : fires_right) {
//                fire.stay();
//                if (checkTopBoundary(fire)){
//                    fire.setY(1500);
//                }
//            }
//
//            for (int i = 0; i < fires_right.size(); i++) {
//                if (time <= 5 && checkTopBoundary(fires_right.get(i))){
//                    fires_right.remove(i);
//                }
//            }
//
//            for (AnimationGameObject fire : fires_left) {
//                fire.stay();
//                if (checkTopBoundary(fire)){
//                    fire.setY(1200);
//                }
//            }
//
//            for (int i = 0; i < fires_left.size(); i++) {
//                if (time <= 5 && checkTopBoundary(fires_left.get(i))){
//                    fires_left.remove(i);
//                }
//            }

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
                updateBackgroundImage();
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
        }

    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        background_0.paint(g);
        background_1.paint(g);
        roof.paint(g);
        hungerLabel.paint(g);
        timeLabel.paint(g);
        hungerBack.paint(g);
        hungerCount.paint(g);
        for (Floor floor : floors) {
            floor.paint(g);
        }

        // 火把
//        for (AnimationGameObject fire : fires_left){
//            fire.paint(g);
//        }
//        for (AnimationGameObject fire : fires_right){
//            fire.paint(g);
//        }
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

        // 火把
//        for (AnimationGameObject fire : fires_left) {
//            fire.setY(fire.getY() - 3);
//            fire.setBoundary();
//        }
//        for (AnimationGameObject fire : fires_right) {
//            fire.setY(fire.getY() - 3);
//            fire.setBoundary();
//        }
        background_0.setBoundary();
        background_1.setBoundary();
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
        gsChangeListener.changeScene(MainPanel.GAME_SCENE);
    }

    // 跳出選單
    private void menu(){
        isCalled = true;
        button_resume = new Button(175, 150, 150, 100, "button/Button_Resume.png");
        button_new_game = new Button(175, 300, 150, 100, "button/Button_NewGame.png");
        button_menu = new Button(175, 450, 150, 100, "button/Button_Menu.png");
        cursor = new GameObject(100, 150 + 25, 50, 50, 168, 140, "background/Cursor.png");
    }

    private void chooseFunction(){
        if (((cursor.getTop() + cursor.getBottom()) / 2) < 0) {
            return;
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
}
