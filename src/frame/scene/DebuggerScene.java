package frame.scene;

import character.*;
import character.Button;
import character.food.Food;
import character.trap.FlashTrap;
import character.trap.FragmentTrap;
import character.trap.TrapGenerator;
import frame.MainPanel;
import util.ResourcesManager;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DebuggerScene extends Scene {
    public DebuggerScene debuggerScene;
    private GameObject background_0, background_1, roof;
    private GameObject hungerCount, hungerBack;
    private int hungerValue;
    private AnimationGameObject fire_left, fire_right;
    private Actor player;
    private ArrayList<Floor> floors;

    // 名稱儲存
    private String name; // 儲存名稱

    // 選單相關
    private boolean isCalled;
    private boolean isPause;
    private GameObject cursor; // 光標
    private Button button_resume, button_menu, button_new_game; // 三個按鈕

    // 顯示板
    private GameObject hungerLabel;
    private GameObject record;

    // 遊戲狀態
    private int key; // 鍵盤輸入值
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
    private static String[] leaderBoardData = MainPanel.leaderBoard;
    private PlayerInfo[] playerInfos;
    private boolean isOnBoard;
    private int rank;

    private int flashCount; //閃光延遲

    // 人物操控
    private boolean up = false, down = false, left = false, right = false;

    public DebuggerScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        debuggerScene = this;
        // 場景物件
        setSceneObject();
        roof = new GameObject(0, 0, 500, 64, 500, 64,"background/Roof_new.png");
        player = new Actor(250, 200, 32, 32, 32, 32, "actor/Actor2.png");
        // 顯示板
        hungerLabel = new GameObject(28,8, 64, 32,64, 32, "background/HungerLabel.png");
        // 飢餓值
        hungerBack = new GameObject(96, 16, 100, 16,5, 5, "background/Hunger.png");
        hungerCount = new GameObject(96, 16, 0, 16, 5, 5, "background/HungerCount.png");
        // 初始10塊階梯
        floors = new ArrayList<>();
        floors.add(new Floor(player.getModX() - (64 - 32), 200 + 32, TrapGenerator.getInstance().genSpecificTrap(TrapGenerator.TRAP_NORMAL))); // 初始站立
        floors.add(new Floor(player.getModX() - (64 - 32) + 64, 200 + 32, TrapGenerator.getInstance().genSpecificTrap(TrapGenerator.TRAP_NORMAL))); // 初始站立
        floors.add(new Floor(player.getModX() - (64 - 32) + 64 + 64, 200 + 32, TrapGenerator.getInstance().genSpecificTrap(TrapGenerator.TRAP_NORMAL))); // 初始站立
//        floors.add(new Floor(player.getModX() - 96, 200 + 32, TrapGenerator.getInstance().genSpecificTrap(TrapGenerator.TRAP_DARKNESS))); // 初始站立
        Floor dFloor = new Floor(player.getModX() - 96, 400 + 32, TrapGenerator.getInstance().genSpecificTrap(TrapGenerator.TRAP_DARKNESS));
        dFloor.setSpeedY(-1);
        floors.add(dFloor); // 初始站立
        floors.add(new Floor(player.getModX() - 96 - 64, 200 + 32, TrapGenerator.getInstance().genSpecificTrap(TrapGenerator.TRAP_FLIPPING))); // 初始站立
        floors.add(new Floor(player.getModX() - 96 - 64 - 64, 200 + 32, TrapGenerator.getInstance().genSpecificTrap(TrapGenerator.TRAP_RUNNING))); // 初始站立
//        for (int i = 0; i < 9; i++) {
//            floors.add(FloorGenerator.getInstance().genFloor(floors, floors.get(i), 0));
//        }
        isCalled = false;
        isPause = false;
        isOver = false;
        isDark = false;
        showLayer = false;
        layer = 0; // 從0層 開始
        name = "";
        // 讀取進來的排行榜資料
        rank = -1; // 初始排行設定值
        isOnBoard = false;
        playerInfos = new PlayerInfo[5];
    }

    private void setSceneObject() {
        background_0 = new GameObject(0, -22, 500, 700, 1024, 768,"background/EndBackground.png");
        background_1 = new GameObject(0, -22 + 700, 500, 700, 1024, 768,"background/EndBackground.png");
//        background_0.setBoundary();
//        background_1.setBoundary();
        fire_left = new AnimationGameObject(0, (int) (background_0.getModY() + background_0.getDrawHeight()*MainPanel.ratio/2), 30, 30, 64, 64,"background/Fire.png");
        fire_right = new AnimationGameObject(470, (int) (background_1.getModY() + background_1.getDrawHeight()*MainPanel.ratio/2), 30, 30, 64, 64,"background/Fire.png");
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
                                if (!(cursor.getY() - 150*MainPanel.ratio < button_resume.getY())){
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
                                if (!(cursor.getY() + 150*MainPanel.ratio > button_menu.getY() + button_menu.getDrawHeight())){
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
                        break;
                    case 0x08:
                        if (isOver){
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
    public void logicEvent() throws IOException {
        if (!player.isDie()){ // 還沒死亡的狀態
            if (!isPause){
                if (isDark){
                    if (darkDelay++ == 80){
                        isDark = false;
                        darkDelay = 0;
                    }
                }
                MainPanel.checkLeftRightBoundary(player);
                changeDirection();
                int floorAmount = checkSceneFloorAmount();
                checkFragmentFloorAmount();
                hungerValue = player.getHunger();
                if (floorAmount < 10 && floors.size() < 20){
                    for (int i = 0; i < 10 - floorAmount; i++) {
                        // 傳入現在層數，生成器將依此更新生成機率
                        floors.add(FloorGenerator.getInstance().genFloor(floors, findLast(), layer));
                    }
                }
//                System.out.println(player.isOn());

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
                    }
//                    if (floors.get(i).getTrapFunction() instanceof FragmentTrap && floors.get(i).isTriggered()){
//                        if (floors.get(i).getRemoveDelayCount() >= 60){
//                            floors.remove(i);
//                        }
//                    }
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
//                player.hunger();
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
                if (player.getModY() + player.getDrawHeight() * MainPanel.ratio > MainPanel.window.height){
                    player.die();
                }
                // 背景刷新
//                updateBackgroundImage();
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
                    if (player.getModY() + player.getDrawHeight() * MainPanel.ratio > MainPanel.window.height){
                        isOver = true;
                        isOnBoard = checkCurrentScoreOnBoard();
                        if (isOnBoard){
                            playerInfos[rank] = new PlayerInfo(String.valueOf(name), player.getScore());
                        }else {
                            gsChangeListener.changeScene(MainPanel.GAME_OVER_SCENE);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void paint(Graphics g, MainPanel mainPanel) {
        Graphics2D g2d = (Graphics2D)g.create();
        background_0.paint(g2d, mainPanel);
        background_1.paint(g2d, mainPanel);
        if (isDark){
            g2d.drawImage(darkness, 0, 0, 500, 700, 0, 0, 1024, 768, null);
        }
        roof.paint(g2d, mainPanel);
        hungerLabel.paint(g2d, mainPanel);
        hungerBack.paint(g2d, mainPanel);
        hungerCount.paint(g2d, mainPanel);
        if (isDark){
            g2d.setClip(new Ellipse2D.Float(player.getCenterPoint().x - 75, player.getCenterPoint().y - 75, 150, 150));
        }
        fire_left.paint(g2d, mainPanel);
        fire_right.paint(g2d, mainPanel);
        for (Floor floor : floors) {
            floor.paint(g2d, mainPanel);
        }

        player.paint(g2d, mainPanel);


        // 印出吃到食物的回覆值
        g2d.setFont(MainPanel.ENGLISH_FONT.deriveFont(15.0f*MainPanel.ratio));
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
        g2d.drawString(healMsg, player.getModX() - (msgWidth*MainPanel.ratio - player.getDrawWidth()*MainPanel.ratio)/ 2, player.getModY());

        // 印出現在總體成績
        g2d.setFont(MainPanel.ENGLISH_FONT.deriveFont(20.0f*MainPanel.ratio));
        g2d.setColor(Color.WHITE);
        String scoreMsg = "Score : " + player.getScore();
        msgWidth = fm.stringWidth(scoreMsg);
        msgAscent = fm.getAscent();
        g2d.drawString(scoreMsg, 220*MainPanel.ratio + msgWidth/3, 30*MainPanel.ratio);

        // 印出飢餓值
        Font engFont = MainPanel.ENGLISH_FONT.deriveFont(16.0f*MainPanel.ratio);
        Font chiFont = MainPanel.CHINESE_FONT.deriveFont(36.0f*MainPanel.ratio);
        g2d.setFont(engFont);
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.valueOf(hungerValue), (96 + 112)*MainPanel.ratio, 30*MainPanel.ratio);
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
        g2d.drawString(msg, 250*MainPanel.ratio - msgWidth/2, 350*MainPanel.ratio);
        g2d.setFont(chiFont.deriveFont(16.0f*MainPanel.ratio));
        g2d.drawString("地下 " + layer + " 層", 365*MainPanel.ratio, 30*MainPanel.ratio);

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
                record = new GameObject((int) (MainPanel.window.width/2 - drawWidth*MainPanel.ratio/2), (int) (MainPanel.window.height/2*MainPanel.ratio - drawHeight*MainPanel.ratio/2), (int) (drawWidth*MainPanel.ratio), (int) (drawHeight*MainPanel.ratio), 300, 200, "background/Record.png");
            }
            // 印出名字視窗中的字
            record.paint(g, mainPanel);
            g2d.setFont(chiFont.deriveFont(25.0f*MainPanel.ratio));
            msg = String.valueOf(name);
            int msgWidth = fm.stringWidth(msg);
            g2d.setColor(Color.BLACK);
//            g2d.drawString(msg, (int)(MainPanel.window.width/2 - msgWidth*MainPanel.ratio/2), (int)(MainPanel.window.height/2*MainPanel.ratio));
            g2d.drawString(msg, MainPanel.window.width/2 - msgWidth / 2 + 18*MainPanel.ratio, record.getModY() + 150*MainPanel.ratio);

            if (isDark){
                g2d.clip(new Rectangle2D.Float(0, 0, 500, 65));
            }
        }
    }

    // 比天花板高就消失
    private boolean checkTopBoundary(GameObject gameObject){
        return gameObject.getTop() <= this.roof.getModY() + this.roof.getDrawHeight()*MainPanel.ratio;
    }

    // 確認畫面中階梯數量
    private int checkSceneFloorAmount(){
        int count = 0;
        for (int i = 0; i < floors.size(); i++) {
            Floor current = floors.get(i);
            if (current.getModY() > 0 && current.getModY() + current.getDrawHeight() * MainPanel.ratio < MainPanel.window.height){
                count++;
            }
        }
        return count;
    }

    // 確認生成出的破碎地板數量
    private int checkFragmentFloorAmount(){
        int count = 0;
        for (int i = 0; i < floors.size(); i++){
            Floor current = floors.get(i);
            if (current.getTrapFunction() instanceof FragmentTrap){
                count++;
            }
        }
        return count;
    }

    // 更新背景圖
    private void updateBackgroundImage(){
        int background_rising_speed = 5;
        if (background_0.getModY() + background_0.getDrawHeight() * MainPanel.ratio < 0){
            background_0 = new GameObject(0, 678, 500, 700, 1024, 768,"background/EndBackground.png");
            layer++;
            showLayer = true;
        }
        if (background_1.getModY() + background_1.getDrawHeight() * MainPanel.ratio < 0){
            background_1 = new GameObject(0, 678, 500, 700, 1024, 768,"background/EndBackground.png");
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
        gsChangeListener.changeScene(MainPanel.DEBUGGER_SCENE);
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
        if (cursorCenterPoint.y < button_resume.getModY() + button_resume.getDrawHeight()*MainPanel.ratio && cursorCenterPoint.y > button_resume.getModY()){
            return button_resume;
        }
        if (cursorCenterPoint.y < button_new_game.getModY() + button_new_game.getDrawHeight()*MainPanel.ratio && cursorCenterPoint.y > button_new_game.getModY()){
            return button_new_game;
        }
        if (cursorCenterPoint.y < button_menu.getModY() + button_menu.getDrawHeight()*MainPanel.ratio && cursorCenterPoint.y > button_menu.getModY()){
            return button_menu;
        }
        return null;
    }

    // 火把持續生成
    private void continueGeneration(GameObject gameObject){
        if (gameObject.getModY() + gameObject.getDrawHeight() * MainPanel.ratio < 0){
            gameObject.setY(MainPanel.window.height);
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
        for (int i = playerInfos.length - 1; i >= 0; i--) {
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

    public void setDark(boolean dark) {
        isDark = dark;
    }

    public ArrayList<Floor> getFloors() {
        return floors;
    }
}

