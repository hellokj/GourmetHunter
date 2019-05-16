package frame.scene;

import character.*;
import character.Button;
import character.food.Food;
import character.trap.FlashTrap;
import character.trap.TrapGenerator;
import frame.MainPanel;
import util.ResourcesManager;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class InfinityGameScene extends Scene {
    private GameObject background_0, background_1, roof;
    private GameObject hungerCount, hungerBack;
    private int hungerValue;
    private AnimationGameObject fire_left, fire_right;
    private Actor player;
    private ArrayList<Floor> floors;
    private FloorGenerator fg;
    private TrapGenerator tg;

    // 名稱儲存
    private String name; // 儲存名稱

    // 選單相關
    private boolean isCalled;
    private boolean isPause;
    private GameObject cursor; // 光標
    private Button chooser;
    private Button button_resume, button_menu, button_new_game; // 三個按鈕

    // 顯示板
    private GameObject hungerLabel;
    private GameObject record;

    // 遊戲狀態
//    private int key; // 鍵盤輸入值
    private int count; // 死亡跳起計數器
    private boolean isOver;
    private int layer; // 地下階層
    private Food eatenFood;

    // 印出文字相關
    private boolean showLayer, showHeal;
    private int msgWidth, msgAscent;
    private FontMetrics fm;
    private int layerDrawingCount, healDrawingCount; // 文字顯示時間

    //  排行榜
    private String[] leaderBoardData = MainPanel.leaderBoard;
    private PlayerInfo[] playerInfos;
    private boolean isOnBoard;
    private int rank;

    private int flashCount; //閃光延遲

    // 人物操控
    private boolean up = false, down = false, left = false, right = false;

    // 音效
    private AudioClip bgm;

    public InfinityGameScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
//        BGM_INFINITY.loop();
        bgm = ResourcesManager.getInstance().getSound("sound/InfinityMode.au");
        bgm.loop();
        // 場景物件
        setSceneObject();
        roof = new GameObject(0, 0, 500, 64, 500, 64,"background/Roof.png");
//        player = new Actor(250, 200, 32, 32, 32, 32, "actor/Actor2.png");
        player = new Actor(250, 200, 32, 32, 32, 32,MainPanel.P1);
        // 顯示板
        hungerLabel = new GameObject(28,8, 64, 32,64, 32, "background/HungerLabel.png");
        // 飢餓值
        hungerBack = new GameObject(96, 16, 100, 16,5, 5, "background/Hunger.png");
        hungerCount = new GameObject(96, 16, 0, 16, 5, 5, "background/HungerCount.png");
        // 初始10塊階梯
        fg = new FloorGenerator();
        tg = new TrapGenerator();
        floors = new ArrayList<>();
        floors.add(new Floor(player.getModX() - (64 - 32), 200 + 32, tg.genSpecificTrap(0))); // 初始站立
        for (int i = 0; i < 14; i++) {
            floors.add(fg.genFloor(floors, floors.get(i), layer));
        }
        isCalled = false;
        isPause = false;
        isOver = false;
        showLayer = false;
        layer = 0; // 從0層 開始
        name = "";
        // 讀取進來的排行榜資料
        rank = -1; // 初始排行設定值
        isOnBoard = false;
        playerInfos = new PlayerInfo[5];
    }

    private void setSceneObject() {
        background_0 = new GameObject(0, 0, 500, 700, 500, 700,"background/circus2.png");
        background_1 = new GameObject(0, 700, 500, 700, 500, 700,"background/circus2.png");
        background_0.setBoundary();
        background_1.setBoundary();
        fire_left = new AnimationGameObject(0, (int) (background_0.getModY() + background_0.getDrawHeight()*MainPanel.RATIO /2), 30, 30, 64, 64,"background/Fire.png");
        fire_right = new AnimationGameObject(470, (int) (background_1.getModY() + background_1.getDrawHeight()*MainPanel.RATIO /2), 30, 30, 64, 64,"background/Fire.png");
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
                        if (!isOver){
                            if (!isPause){
                                right = true;
                            }
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (!isOver){
                            if (!isPause){
                                left = true;
                            }
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (!isOver){
                            if (!isPause){
                                up = true;
                            }else {
                                if (!(cursor.getY() - 150 < button_resume.getY())){
                                    cursor.setY(cursor.getY() - 150);
                                }
                            }
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (!isOver){
                            if (!isPause){
                                down = true;
                            }else {
                                if (!(cursor.getY() + 150 > button_menu.getY() + button_menu.getDrawHeight())){
                                    cursor.setY(cursor.getY() + 150);
                                }
                            }
                        }
                        break;
                    case KeyEvent.VK_R:
                        if (!isOver){
                            player.reset();
                        }
//                        reset();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        if (!isOver){
                            if (isPause){
                                resume();
                                isCalled = false;
                            }else {
                                pause();
                                menu();
                            }
                        }
                        break;
                    case KeyEvent.VK_SPACE:
                        if (isPause && !isOver){
                            chooser = checkCursorPosition();
                            if (chooser == button_resume){
                                button_resume.setImageOffsetX(1);
                            }
                            if (chooser == button_menu){
                                button_menu.setImageOffsetX(1);
                            }
                            if (chooser == button_new_game){
                                button_new_game.setImageOffsetX(1);
                            }
                        }
                        break;
                    case 0x08:
                        if (isOver){
                            System.out.println("change");
                            if (name.length() > 0){
//                                name[--nameCount] = 0;
                                name = name.substring(0, name.length() - 1);
                            }
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        if (isOver && name.length() > 0){
                            try {
                                writeBackLeaderBoard();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            bgm.stop();
                            VICTORY.loop();
                            gsChangeListener.changeScene(MainPanel.LEADER_BOARD_SCENE);
                        }
                }
                if (isOver){
                    if (name.length() < 8){
                        char input = (char)KeyEvent.getExtendedKeyCodeForChar(key);
                        if ((input >= 48 && input <= 57) || (input >= 65 && input <= 90) || (input >= 97 && input <= 122)){
//                            name[nameCount++] = input;
                            name += String.valueOf(input);
                        }
                    }
                }
                key = e.getKeyCode();
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
                }else {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE){
                        if (chooser == button_resume){
                            button_resume.setImageOffsetX(0);
                            resume();
                            BUTTON_CLICK.play();
                        }
                        if (chooser == button_new_game){
                            button_new_game.setImageOffsetX(0);
                            BUTTON_CLICK.play();
                            reset();
                        }
                        if (chooser == button_menu){
                            button_menu.setImageOffsetX(0);
                            BUTTON_CLICK.play();
                            bgm.stop();
                            gsChangeListener.changeScene(MainPanel.MENU_SCENE);
                        }
                        isCalled = false;
                    }
                }
                key = -1;
            }
        };
    }

    @Override
    public void logicEvent() throws IOException {
        if (!player.isDie()){ // 還沒死亡的狀態
            if (!isPause){
                if (isDark){
                    if (darkDelay++ == 120){
                        isDark = false;
                        darkDelay = 0;
                    }
                }
                MainPanel.checkLeftRightBoundary(player);
                changeDirection();
                int floorAmount = checkSceneFloorAmount();
                hungerValue = player.getHunger();
                if (floorAmount < 15 && floors.size() < 20){
                    for (int i = 0; i < 15 - floorAmount; i++) {
                        // 傳入現在層數，生成器將依此更新生成機率
                        floors.add(fg.genFloor(floors, findLast(), layer));
                    }
                }
                // 逆向摩擦力
                friction(player);

                for (int i = 0; i < floors.size(); i++) {
                    player.checkOnFloor(floors.get(i), this);
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
                        break;
                    }
                }
                if (checkTopBoundary(player)){
                    player.touchRoof();
                }else {
                    player.stay();
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
                if ((right || left) && !player.isStop()){
                    player.acceleration();
                }
                player.update();
                // 掉落死亡 or 餓死後落下
                if (player.getModY() + player.getDrawHeight() * MainPanel.RATIO > MainPanel.CURRENT_WINDOW.height){
                    player.die();
                }
                // 背景刷新
                updateBackgroundImage();
            }
        }else {
            if (!isPause){
                // 死亡跳起後落下
                if (count++ < 20){
                    player.setSpeedX(0);
                    player.setY(player.getY()-5);
                }else {
                    player.setSpeedX(0);
                    player.update();
                    // 完全落下後切場景
                    if (player.getModY() + player.getDrawHeight() * MainPanel.RATIO > MainPanel.CURRENT_WINDOW.height){
                        isOver = true;
                        isOnBoard = checkCurrentScoreOnBoard();
                        if (isOnBoard){
                            playerInfos[rank] = new PlayerInfo(String.valueOf(name), player.getScore());
                        }else {
                            bgm.stop();
                            gsChangeListener.changeScene(MainPanel.GAME_OVER_SCENE);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void paint(Graphics g, MainPanel mainPanel) {
        Graphics2D g2d = (Graphics2D)g;

//        if (isDark){
//            background_0.paint(g, mainPanel);
//            background_1.paint(g, mainPanel);
//            fire_left.paint(g, mainPanel);
//            fire_right.paint(g, mainPanel);
//            roof.paint(g, mainPanel);
//            hungerLabel.paint(g, mainPanel);
//            hungerBack.paint(g, mainPanel);
//            hungerCount.paint(g, mainPanel);
//
//            for (Floor floor : floors) {
//                floor.paint(g, mainPanel);
//            }
//
//            g2d.drawImage(darkness, 0, (int) (48 * MainPanel.RATIO), (int) (500 * MainPanel.RATIO), (int) (700 * MainPanel.RATIO), 0, 0, 1024, 768, null);
//            player.paint(g, mainPanel);
//
//            // 印出吃到食物的回覆值
//            g2d.setFont(MainPanel.ENGLISH_FONT.deriveFont(15.0f*MainPanel.RATIO));
//            g2d.setColor(Color.GREEN);
//            String healMsg = "";
//            if (showHeal){
//                if (++healDrawingCount <= 50){
//                    healMsg = "+ "+ eatenFood.getHeal();
//                }else {
//                    showHeal = false;
//                    healDrawingCount = 0;
//                }
//            }
//            fm = g2d.getFontMetrics();
//            msgWidth = fm.stringWidth(healMsg);
//            msgAscent = fm.getAscent();
//            g2d.drawString(healMsg, player.getModX() - (msgWidth*MainPanel.RATIO - player.getDrawWidth()*MainPanel.RATIO)/ 2, player.getModY());
//
//            // 印出現在總體成績
//            g2d.setFont(MainPanel.ENGLISH_FONT.deriveFont(20.0f*MainPanel.RATIO));
//            g2d.setColor(Color.WHITE);
//            String scoreMsg = "Score : " + player.getScore();
//            msgWidth = fm.stringWidth(scoreMsg);
//            msgAscent = fm.getAscent();
//            g2d.drawString(scoreMsg, 220*MainPanel.RATIO + msgWidth/3, 30*MainPanel.RATIO);
//
//            // 印出飢餓值
//            Font engFont = MainPanel.ENGLISH_FONT.deriveFont(16.0f*MainPanel.RATIO);
//            Font chiFont = MainPanel.CHINESE_FONT.deriveFont(36.0f*MainPanel.RATIO);
//            g2d.setFont(engFont);
//            g2d.setColor(Color.WHITE);
//            g2d.drawString(String.valueOf(hungerValue), (96 + 112)*MainPanel.RATIO, 30*MainPanel.RATIO);
//            g2d.setFont(chiFont);
//            fm = g2d.getFontMetrics();
//
//            //閃光開始
//            if(FlashTrap.getFlashState()){
//                flashCount++;
//            }//閃光持續
//            if(flashCount <15 && flashCount >0){
//                FlashTrap.getFlash().setCounter(flashCount -1);
//                //System.out.println("**"+flashCount);
//                FlashTrap.getFlash().paint(g, mainPanel);
//            }//閃光結束
//            else if(flashCount >=15){
//                FlashTrap.setFlashState(false);
//                flashCount = 0;
//            }
//
//            // 印出地下層數
//            String msg = "";
//            if (showLayer){
//                if (++layerDrawingCount <= 80){
//                    msg = "地下 " + layer + " 層";
//                }else {
//                    msg = "";
//                    showLayer = false;
//                    layerDrawingCount = 0;
//                }
//            }
//            msgWidth = fm.stringWidth(msg);
//            msgAscent = fm.getAscent();
//            g2d.drawString(msg, 250*MainPanel.RATIO - msgWidth/2, 350*MainPanel.RATIO);
//            g2d.setFont(chiFont.deriveFont(16.0f*MainPanel.RATIO));
//            g2d.drawString("地下 " + layer + " 層", 385*MainPanel.RATIO, 30*MainPanel.RATIO);
//
//            // 印出選單
//            if (isCalled){
//                button_menu.paint(g, mainPanel);
//                button_resume.paint(g, mainPanel);
//                button_new_game.paint(g, mainPanel);
//                cursor.paint(g, mainPanel);
//            }
//            // 彈出輸入名字視窗
//            if (isOver && isOnBoard){
//                int drawWidth = 300, drawHeight = 200;
//                if (record == null){
//                    record = new GameObject((int) (MainPanel.CURRENT_WINDOW.width/2 - drawWidth*MainPanel.RATIO /2), (int) (MainPanel.CURRENT_WINDOW.height/2*MainPanel.RATIO - drawHeight*MainPanel.RATIO /2), (int) (drawWidth*MainPanel.RATIO), (int) (drawHeight*MainPanel.RATIO), 300, 200, "background/Record.png");
//                }
//                // 印出名字視窗中的字
//                record.paint(g, mainPanel);
//                g2d.setFont(chiFont.deriveFont(25.0f*MainPanel.RATIO));
//                msg = String.valueOf(name);
//                int msgWidth = fm.stringWidth(msg);
//                System.out.println(msgWidth);
//                g2d.setColor(Color.BLACK);
////            g2d.drawString(msg, (int)(MainPanel.CURRENT_WINDOW.width/2 - msgWidth*MainPanel.RATIO/2), (int)(MainPanel.CURRENT_WINDOW.height/2*MainPanel.RATIO));
//                g2d.drawString(msg, MainPanel.CURRENT_WINDOW.width/2 - msgWidth / 2 + 18*MainPanel.RATIO, record.getModY() + 150*MainPanel.RATIO);
//            }
//
//            g2d.setClip(new Ellipse2D.Float(player.getCenterPoint().x - 75 * MainPanel.RATIO, player.getCenterPoint().y - 75 * MainPanel.RATIO, 150 * MainPanel.RATIO, 150 * MainPanel.RATIO));
//        }

        background_0.paint(g, mainPanel);
        background_1.paint(g, mainPanel);
        fire_left.paint(g, mainPanel);
        fire_right.paint(g, mainPanel);

        for (Floor floor : floors) {
            floor.paint(g, mainPanel);
        }

        if (isDark){
            g.drawImage(blanket, (int) ((player.getX() + 16 - 575)*MainPanel.RATIO), (int) ((player.getY() + 16 - 775)*MainPanel.RATIO), (int) ((player.getX() + 16 - 575 + 1150)*MainPanel.RATIO), (int) ((player.getY() + 16 - 775 + 1550)*MainPanel.RATIO),0,0, 1150, 1550, null);
        }

        roof.paint(g, mainPanel);
        hungerLabel.paint(g, mainPanel);
        hungerBack.paint(g, mainPanel);
        hungerCount.paint(g, mainPanel);

        player.paint(g, mainPanel);

        // 印出吃到食物的回覆值
        g2d.setFont(MainPanel.ENGLISH_FONT.deriveFont(15.0f*MainPanel.RATIO));
        g2d.setColor(Color.GREEN);
        String healMsg = "";
        if (showHeal){
            if (++healDrawingCount <= 50){
                healMsg = "+ "+ eatenFood.getHeal();
            }else {
                showHeal = false;
                healDrawingCount = 0;
            }
        }
        fm = g2d.getFontMetrics();
        msgWidth = fm.stringWidth(healMsg);
        msgAscent = fm.getAscent();
        g2d.drawString(healMsg, player.getModX() - (msgWidth*MainPanel.RATIO - player.getDrawWidth()*MainPanel.RATIO)/ 2, player.getModY());

        // 印出現在總體成績
        g2d.setFont(MainPanel.ENGLISH_FONT.deriveFont(20.0f*MainPanel.RATIO));
        g2d.setColor(Color.WHITE);
        String scoreMsg = "Score : " + player.getScore();
        msgWidth = fm.stringWidth(scoreMsg);
        msgAscent = fm.getAscent();
        g2d.drawString(scoreMsg, 220*MainPanel.RATIO + msgWidth/3, 30*MainPanel.RATIO);

        // 印出飢餓值
        Font engFont = MainPanel.ENGLISH_FONT.deriveFont(16.0f*MainPanel.RATIO);
        Font chiFont = MainPanel.CHINESE_FONT.deriveFont(36.0f*MainPanel.RATIO);
        g2d.setFont(engFont);
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.valueOf(hungerValue), (96 + 112)*MainPanel.RATIO, 30*MainPanel.RATIO);
        g2d.setFont(chiFont);
        fm = g2d.getFontMetrics();

        //閃光開始
        if(FlashTrap.getFlashState()){
            flashCount++;
        }//閃光持續
        if(flashCount <15 && flashCount >0){
            FlashTrap.getFlash().setCounter(flashCount -1);
            //System.out.println("**"+flashCount);
            FlashTrap.getFlash().paint(g, mainPanel);
        }//閃光結束
        else if(flashCount >=15){
            FlashTrap.setFlashState(false);
            flashCount = 0;
        }

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
        g2d.drawString(msg, 250*MainPanel.RATIO - msgWidth/2, 350*MainPanel.RATIO);
        g2d.setFont(chiFont.deriveFont(16.0f*MainPanel.RATIO));
        g2d.drawString("地下 " + layer + " 層", 385*MainPanel.RATIO, 30*MainPanel.RATIO);

        // 印出選單
        if (isCalled){
            button_menu.paint(g, mainPanel);
            button_resume.paint(g, mainPanel);
            button_new_game.paint(g, mainPanel);
            cursor.paint(g, mainPanel);
        }
        // 彈出輸入名字視窗
        if (isOver && isOnBoard){
            int drawWidth = 300, drawHeight = 200;
            if (record == null){
                record = new GameObject((int) (MainPanel.CURRENT_WINDOW.width/2 - drawWidth*MainPanel.RATIO /2), (int) (MainPanel.CURRENT_WINDOW.height/2*MainPanel.RATIO - drawHeight*MainPanel.RATIO /2), (int) (drawWidth*MainPanel.RATIO), (int) (drawHeight*MainPanel.RATIO), 300, 200, "background/Record.png");
            }
            // 印出名字視窗中的字
            record.paint(g, mainPanel);
            g2d.setFont(chiFont.deriveFont(25.0f*MainPanel.RATIO));
            msg = String.valueOf(name);
            int msgWidth = fm.stringWidth(msg);
            System.out.println(msgWidth);
            g2d.setColor(Color.BLACK);
//            g2d.drawString(msg, (int)(MainPanel.CURRENT_WINDOW.width/2 - msgWidth*MainPanel.RATIO/2), (int)(MainPanel.CURRENT_WINDOW.height/2*MainPanel.RATIO));
            g2d.drawString(msg, MainPanel.CURRENT_WINDOW.width/2 - msgWidth / 2 + 18*MainPanel.RATIO, record.getModY() + 150*MainPanel.RATIO);
        }
    }

    // 比天花板高就消失
    private boolean checkTopBoundary(GameObject gameObject){
        return gameObject.getTop() <= this.roof.getModY() + this.roof.getDrawHeight()*MainPanel.RATIO;
    }

    // 確認畫面中階梯數量
    private int checkSceneFloorAmount(){
        int count = 0;
        for (int i = 0; i < floors.size(); i++) {
            Floor current = floors.get(i);
            if (current.getModY() > 0 && current.getModY() + current.getDrawHeight() * MainPanel.RATIO < MainPanel.CURRENT_WINDOW.height){
                count++;
            }
        }
        return count;
    }

    // 更新背景圖
    private void updateBackgroundImage(){
        int background_rising_speed = 5;
        if (background_0.getModY() + background_0.getDrawHeight() * MainPanel.RATIO <= 10){
//            background_0 = new GameObject(0, 700, 500, 700, 500, 700,"background/circus2.png");
            background_0.setY(background_1.getY() + background_1.getDrawHeight());
            layer++;
            showLayer = true;
        }
        if (background_1.getModY() + background_1.getDrawHeight() * MainPanel.RATIO <= 10){
//            background_1 = new GameObject(0, 700, 500, 700, 500, 700,"background/circus2.png");
            background_1.setY(background_0.getY() + background_0.getDrawHeight());
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
        if (cursorCenterPoint.y < button_resume.getModY() + button_resume.getDrawHeight()*MainPanel.RATIO && cursorCenterPoint.y > button_resume.getModY()){
            return button_resume;
        }
        if (cursorCenterPoint.y < button_new_game.getModY() + button_new_game.getDrawHeight()*MainPanel.RATIO && cursorCenterPoint.y > button_new_game.getModY()){
            return button_new_game;
        }
        if (cursorCenterPoint.y < button_menu.getModY() + button_menu.getDrawHeight()*MainPanel.RATIO && cursorCenterPoint.y > button_menu.getModY()){
            return button_menu;
        }
        return null;
    }

    // 火把持續生成
    private void continueGeneration(GameObject gameObject){
        if (gameObject.getModY() + gameObject.getDrawHeight() * MainPanel.RATIO < 0){
            gameObject.setY(MainPanel.CURRENT_WINDOW.height);
        }
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

    // 確認當次成績是否上榜
    private boolean checkCurrentScoreOnBoard(){
        // 讀資料，加入至每筆上榜玩家資訊
        for (int i = 0; i < leaderBoardData.length; i++) {
            String[] eachRow = leaderBoardData[i].split(",");
            playerInfos[i] = new PlayerInfo(eachRow[0], Integer.parseInt(eachRow[1]));
        }
        for (int i = playerInfos.length - 1; i > 0; i--) {
            if (playerInfos[i].getScore() < this.player.getScore()){
                rank = i;
            }
        }
        if (rank == -1){
            return false;
        }else {
            for (int i = playerInfos.length - 1; i > rank; i--) {
                PlayerInfo tmp = playerInfos[i-1];
                playerInfos[i] = new PlayerInfo(tmp.getName(), tmp.getScore());
            }
            return true;
        }
    }

    private void writeBackLeaderBoard() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(MainPanel.LEADER_BOARD_FILE_PATH));
        for (int i = 0; i < 5; i++) {
            String result = playerInfos[i].toString();
            bw.write(result + "\n");
        }
        bw.close();
    }
}

