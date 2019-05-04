package frame.scene;

import character.*;
import character.Button;
import character.food.Food;
import character.trap.TrapGenerator;
import frame.GameFrame;
import frame.MainPanel;
import util.TextManager;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class InfinityGameScene extends Scene {
    private GameObject background_0, background_1, roof;
    private GameObject hungerCount, hungerBack;
    private int hungerValue;
    private AnimationGameObject fire_left, fire_right;
    private Actor player;
    private ArrayList<Floor> floors;

    // 選單相關
    private boolean isCalled;
    private boolean isPause;
    private GameObject cursor; // 光標
    private Button button_resume, button_menu, button_new_game; // 三個按鈕

    // 顯示板
    private GameObject hungerLabel;

    private int key; // 鍵盤輸入值
    private int count; // 死亡跳起計數器
    private int layer; // 地下階層
    private Food eatenFood;
    // 印出文字相關
    private boolean showLayer, showHeal;
    private int msgWidth, msgAscent;
    private FontMetrics fm;
    private int layerDrawingCount, healDrawingCount; // 文字顯示時間

    public InfinityGameScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        // 場景物件
        setSceneObject();
        roof = new GameObject(0, 0, 500, 64, "background/Roof_new.png");
        player = new Actor(250, 200, 32, 32, 32, 32, "actor/Actor2.png");
        // 顯示板
        hungerLabel = new GameObject(28,8, 64, 32,64, 32, "background/HungerLabel.png");
        // 飢餓值
        hungerBack = new GameObject(96, 16, 100, 16, "background/Hunger.png");
        hungerCount = new GameObject(96, 16, 0, 16, "background/HungerCount.png");
        // 初始10塊階梯
        floors = new ArrayList<>();
        floors.add(new Floor(player.getX() - (64 - 32), 200 + 32, TrapGenerator.getInstance().genSpecificTrap(0))); // 初始站立
        for (int i = 0; i < 9; i++) {
            floors.add(FloorGenerator.getInstance().genFloor(floors.get(i), 0));
        }
        isCalled = false;
        isPause = false;
        showLayer = false;
        layer = 0; // 從0層 開始
    }

    private void setSceneObject() {
        background_0 = new GameObject(0, -22, 500, 700, "background/EndBackground.png");
        background_1 = new GameObject(0, -22 + 700, 500, 700, "background/EndBackground.png");
        background_0.setBoundary();
        background_1.setBoundary();
        fire_left = new AnimationGameObject(0, background_0.getBottom()/2, 30, 30, 64, 64,"background/Fire.png");
        fire_right = new AnimationGameObject(470, background_0.getBottom(), 30, 30, 64, 64,"background/Fire.png");
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
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (!isPause){
                            player.changeDir(Actor.MOVE_LEFT);
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
                        player.reset();
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
                if (key == e.getKeyCode()){
                    key = -1;
                }
            }
        };
    }

    @Override
    public void logicEvent() {
        if (!isPause){
            if (!player.isDie()){ // 還沒死亡的狀態
                MainPanel.checkLeftRightBoundary(player);
                if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_LEFT){
                    player.acceleration();
                }
                int floorAmount = checkSceneFloorAmount();
                hungerValue = player.getHunger();
                if (floorAmount < 10 && floors.size() < 15){
                    for (int i = 0; i < 10 - floorAmount; i++) {
                        // 傳入現在層數，生成器將依此更新生成機率
                        floors.add(FloorGenerator.getInstance().genFloor(findLast(), layer));
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
                    // 吃食物機制
                    if (player.eat(floors.get(i).getFood())){
                        eatenFood = floors.get(i).getFood();
                        if (eatenFood != null){
                            showHeal = true;
                        }
                        floors.get(i).setFood(null); // 吃完，食物設回null
                    }
                    floors.get(i).stay();
                    if (checkTopBoundary(floors.get(i))){
                        floors.remove(i);
                    }
                }
                // 火把
                fire_left.stay();
                fire_right.stay();
                // 人物飢餓
                player.hunger();
                // 繪製現在飢餓值
                hungerCount.setDrawWidth(player.getHunger());
                // 每次都要更新此次座標
                for (Floor floor : floors) {
                    floor.update();
                }
                player.update();
                // 掉落死亡 or 餓死後落下
                if (player.getBottom() > GameFrame.FRAME_HEIGHT){
//                    player.die();
                }
                // 背景刷新
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
        System.out.println(player.isDie());
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        background_0.paint(g);
        background_1.paint(g);
        fire_left.paint(g);
        fire_right.paint(g);
        roof.paint(g);
        hungerLabel.paint(g);
        hungerBack.paint(g);
        hungerCount.paint(g);
        for (Floor floor : floors) {
            floor.paint(g);
        }

        player.paint(g);

        // 印出吃到食物的回覆值
        g.setFont(TextManager.ENGLISH_FONT.deriveFont(15.0f));
        g.setColor(Color.GREEN);
        String healMsg = "";
        if (showHeal){
            if (++healDrawingCount <= 50){
                healMsg = "+ "+ eatenFood.getHeal();
            }else {
                showHeal = false;
                healDrawingCount = 0;
            }
        }
        fm = g.getFontMetrics();
        msgWidth = fm.stringWidth(healMsg);
        msgAscent = fm.getAscent();
        g.drawString(healMsg, player.getX() - (msgWidth - player.getDrawWidth())/ 2, player.getY());

        // 印出現在總體成績
        g.setFont(TextManager.ENGLISH_FONT.deriveFont(20.0f));
        g.setColor(Color.WHITE);
        String scoreMsg = "Score : " + player.getScore();
        msgWidth = fm.stringWidth(scoreMsg);
        msgAscent = fm.getAscent();
        g.drawString(scoreMsg, 220 + msgWidth/3, 30);

        // 印出飢餓值
        Font engFont = TextManager.ENGLISH_FONT.deriveFont(16.0f);
        Font chiFont = TextManager.CHINESE_FONT.deriveFont(36.0f);
        g.setFont(engFont);
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(hungerValue), 96 + 112, 30);
        g.setFont(chiFont);
        fm = g.getFontMetrics();

        // 印出地下層數
        String msg = "";
        if (showLayer){
            if (++layerDrawingCount <= 80){
                msg = "地下 " + layer + " 層";
            }else {
                msg = "";
                showLayer = false;
                layerDrawingCount = 0;
            }
        }
        msgWidth = fm.stringWidth(msg);
        msgAscent = fm.getAscent();
        g.drawString(msg, 250 - msgWidth/2, 350);
        g.setFont(chiFont.deriveFont(16.0f));
        g.drawString("地下 " + layer + " 層", 365, 30);

        // 印出選單
        if (isCalled){
            button_menu.paint(g);
            button_resume.paint(g);
            button_new_game.paint(g);
            cursor.paint(g);
        }
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
        int background_rising_speed = 5;
        if (background_0.getBottom() < 0){
            background_0 = new GameObject(0, 678, 500, 700, "background/EndBackground.png");
            layer++;
            showLayer = true;
        }
        if (background_1.getBottom() < 0){
            background_1 = new GameObject(0, 678, 500, 700, "background/EndBackground.png");
        }
        background_0.setY(background_0.getY() - background_rising_speed);
        background_1.setY(background_1.getY() - background_rising_speed);
        background_0.setBoundary();
        background_1.setBoundary();

        // 火把
        fire_left.setY(fire_left.getY() - background_rising_speed);
        fire_right.setY(fire_right.getY() - background_rising_speed);
        fire_left.setBoundary();
        fire_right.setBoundary();
        continueGeneration(fire_left);
        continueGeneration(fire_right);
    }

    // 找到最後一塊
    private Floor findLast(){
        return floors.get(floors.size() - 1);
    }

    // 重新開始遊戲
    private void reset(){
        gsChangeListener.changeScene(MainPanel.INFINITY_GAME_SCENE);
    }

    // 跳出選單
    private void menu(){
        isCalled = true;
        button_resume = new Button(175, 150, 150, 100, 150, 100, "button/Button_Resume.png");
        button_new_game = new Button(175, 300, 150, 100, 150, 100,"button/Button_NewGame.png");
        button_menu = new Button(175, 450, 150, 100, 150, 100, "button/Button_Menu.png");
        cursor = new GameObject(100, 150 + 25, 50, 50, 168, 140, "background/Cursor.png");
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

    // 火把持續生成
    private void continueGeneration(GameObject gameObject){
        if (gameObject.getBottom() < 0){
            gameObject.setY(GameFrame.FRAME_HEIGHT);
        }
    }


}

