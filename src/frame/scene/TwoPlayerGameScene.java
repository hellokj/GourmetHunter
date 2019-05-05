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

public class TwoPlayerGameScene extends Scene {
    private GameObject background_0, background_1, roof;
    private GameObject hungerCount1, hungerBack1, hungerCount2, hungerBack2;
    private int hungerValue1, hungerValue2;
    private AnimationGameObject fire_left, fire_right;
    private Actor player1, player2;
    private ArrayList<Floor> floors;

    // 選單相關
    private boolean isCalled;
    private boolean isPause;
    private GameObject cursor; // 光標
    private Button button_resume, button_menu, button_new_game; // 三個按鈕

    // 顯示板
    private GameObject hungerLabel1, hungerLabel2;

    private int key; // 鍵盤輸入值
    private int count1, count2; // 死亡跳起計數器
    private int layer; // 地下階層
    private Food eatenFood1, eatenFood2;
    // 印出文字相關
    private boolean showLayer, showHeal1, showHeal2;
    private int msgWidth, msgAscent;
    private FontMetrics fm;
    private int layerDrawingCount, healDrawingCount1, healDrawingCount2; // 文字顯示時間
    // timer延遲調整
    private boolean up_p1 = false, down_p1 = false, left_p1 = false, right_p1 = false;
    private boolean up_p2 = false, down_p2 = false, left_p2 = false, right_p2 = false;
    // timer test delay
    private int delayCount;

    public TwoPlayerGameScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        // 場景物件
        setSceneObject();
        roof = new GameObject(0, 0, 500, 64, "background/Roof_new.png");
        player1 = new Actor(240, 200, 32, 32, 32, 32, "actor/Actor2.png");
        player2 = new Actor(282, 200, 32, 32, 32, 32, "actor/Actor4.png");
        // 顯示板
        hungerLabel1 = new GameObject(28,8, 64, 32,64, 32, "background/HungerLabel.png");
        hungerLabel2 = new GameObject(28,8, 64, 32,64, 32, "background/HungerLabel.png");
        // 飢餓值
        hungerBack1 = new GameObject(96, 16, 100, 16, "background/Hunger.png");
        hungerCount1 = new GameObject(96, 16, 0, 16, "background/HungerCount.png");
        hungerBack2 = new GameObject(296, 16, 100, 16, "background/Hunger.png");
        hungerCount2 = new GameObject(296, 16, 0, 16, "background/HungerCount.png");
        // 初始10塊階梯
        floors = new ArrayList<>();
        floors.add(new Floor(player1.getX(), 200 + 32, TrapGenerator.getInstance().genSpecificTrap(0))); // 初始站立階梯
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
                switch (e.getKeyCode()){
                    // p1 controller
                    case KeyEvent.VK_RIGHT:
                        if (!isPause){
                            right_p1 = true;
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (!isPause){
                            left_p1 = true;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (!isPause){
                            up_p1 = true;
                        }else {
                            if (!(cursor.getY() - 150 < button_resume.getY())){
                                cursor.setY(cursor.getY() - 150);
                            }
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (!isPause){
                            down_p1 = true;
                        }else {
                            if (!(cursor.getY() + 150 > button_menu.getBottom())){
                                cursor.setY(cursor.getY() + 150);
                            }
                        }
                        break;
                    // p2 controller
                    case KeyEvent.VK_W:
                        if (!isPause){
                            up_p2 = true;
                        }else {
                            if (!(cursor.getY() - 150 < button_resume.getY())){
                                cursor.setY(cursor.getY() - 150);
                            }
                        }
                        break;
                    case KeyEvent.VK_A:
                        if (!isPause){
                            left_p2 = true;
                        }
                        break;
                    case KeyEvent.VK_S:
                        if (!isPause){
                            down_p2 = true;
                        }else {
                            if (!(cursor.getY() + 150 > button_menu.getBottom())){
                                cursor.setY(cursor.getY() + 150);
                            }
                        }
                        break;
                    case KeyEvent.VK_D:
                        if (!isPause){
                            right_p2 = true;
                        }
                        break;
                    case KeyEvent.VK_R:
//                        player1.reset();
//                        player2.reset();
                        reset();
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
                switch (e.getKeyCode()){
                    // p1 controller
                    case KeyEvent.VK_RIGHT:
                        right_p1 = false;
                        break;
                    case KeyEvent.VK_LEFT:
                        left_p1 = false;
                        break;
                    case KeyEvent.VK_UP:
                        up_p1 = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        down_p1 = false;
                        break;
                    // p2 controller
                    case KeyEvent.VK_W:
                        up_p2 = false;
                        break;
                    case KeyEvent.VK_A:
                        left_p2 = false;
                        break;
                    case KeyEvent.VK_S:
                        down_p2 = false;
                        break;
                    case KeyEvent.VK_D:
                        right_p2 = false;
                        break;
                }
            }
        };
    }

    @Override
    public void logicEvent() {
        if (!isPause){
            if (!player1.isDie() && !player2.isDie()){ // 還沒死亡的狀態
                MainPanel.checkLeftRightBoundary(player1);
                MainPanel.checkLeftRightBoundary(player2);
                changeDirection();
                int floorAmount = checkSceneFloorAmount();
                hungerValue1 = player1.getHunger();
                hungerValue2 = player2.getHunger();
                if (floorAmount < 10 && floors.size() < 15){
                    for (int i = 0; i < 10 - floorAmount; i++) {
                        // 傳入現在層數，生成器將依此更新生成機率
                        floors.add(FloorGenerator.getInstance().genFloor(findLast(), layer));
                    }
                }
                // 逆向摩擦力
                friction(player1);
                friction(player2);

                if ((right_p1 || left_p1) && !player1.isStop()){
                    player1.acceleration();
                }
                if ((right_p2 || left_p2) && !player2.isStop()){
                    player2.acceleration();
                }

                for (int i = 0; i < floors.size(); i++) {
                    player1.checkOnFloor(floors.get(i));
                    player2.checkOnFloor(floors.get(i));
                    // 吃食物機制
                    if (player1.eat(floors.get(i).getFood())){
                        eatenFood1 = floors.get(i).getFood();
                        if (eatenFood1 != null){
                            showHeal1 = true;
                        }
                        floors.get(i).setFood(null); // 吃完，食物設回null
                    }
                    if (player2.eat(floors.get(i).getFood())){
                        eatenFood2 = floors.get(i).getFood();
                        if (eatenFood2 != null){
                            showHeal2 = true;
                        }
                        floors.get(i).setFood(null); // 吃完，食物設回null
                    }
                    floors.get(i).stay();
                    if (checkTopBoundary(floors.get(i))){
                        floors.remove(i);
                    }
                }
                if (checkTopBoundary(player1)){
                    player1.touchRoof();
                }else {
                    player1.stay();
                }
                if (checkTopBoundary(player2)){
                    player2.touchRoof();
                }else {
                    player2.stay();
                }
                // 火把
                fire_left.stay();
                fire_right.stay();
                // 人物飢餓
//                player1.hunger();
//                player2.hunger();
                // 繪製現在飢餓值
                hungerCount1.setDrawWidth(player1.getHunger());
                hungerCount2.setDrawWidth(player2.getHunger());
                // 每次都要更新此次座標
                for (Floor floor : floors) {
                    floor.update();
                }
                // 兩人碰撞機制
//                twoPlayer_v1();
//                twoPlayer_v2();
                twoPlayer_v3();
                player1.update();
                player2.update();
                // 掉落死亡 or 餓死後落下
                if (player1.getBottom() > GameFrame.FRAME_HEIGHT){
                    player1.die();
                }
                if (player2.getBottom() > GameFrame.FRAME_HEIGHT){
                    player2.die();
                }
                // 背景刷新
                updateBackgroundImage();
            }else {
                if (player1.isDie()){
                    // 死亡跳起後落下
                    if (count1++ < 20){
                        player1.setSpeedX(0);
                        player1.setY(player1.getY()-5);
                    }else {
                        player1.setSpeedX(0);
                        player1.update();
                        // 完全落下後切場景
                        if (player1.getBottom() > GameFrame.FRAME_HEIGHT){
//                            gsChangeListener.changeScene(MainPanel.GAME_OVER_SCENE);
                        }
                    }
                }
                if (player2.isDie()){
                    if (count2++ < 20){
                        player2.setSpeedX(0);
                        player2.setY(player2.getY()-5);
                    }else {
                        player2.setSpeedX(0);
                        player2.update();
                        // 完全落下後切場景
                        if (player2.getBottom() > GameFrame.FRAME_HEIGHT){
//                            gsChangeListener.changeScene(MainPanel.GAME_OVER_SCENE);
                        }
                    }
                }
            }
        }
    }

    private void twoPlayer_v3() {
        if (player1.checkCollision(player2) && player2.checkCollision(player1)){
//                    player1.setSpeedX(0);
//                    player2.setSpeedX(0);
            player1.stop();
            player2.stop();
            System.out.println(up_p1 + " " + down_p1 + " " + left_p1 + " " + right_p1);
            System.out.println(up_p2 + " " + down_p2 + " " + left_p2 + " " + right_p2);
            collisionMechanism_3(player1, player2);
//                    collisionMechanism_3(player2, player1);
            player1.setSpeedX(0);
            player2.setSpeedX(0);
        }else {
            player1.setStop(false);
            player2.setStop(false);
        }
    }

    private void twoPlayer_v2() {
        collisionMechanism_2(player1, player2);
        collisionMechanism_2(player2, player1);
    }

    private void twoPlayer_v1() {
        collisionMechanism_1(player1, player2);
        collisionMechanism_1(player2, player1);
    }

    private void collisionMechanism_3(Actor player1, Actor player2){
        int collisionDirP1 = player1.checkCollisionDir(player2);
        int collisionDirP2 = player2.checkCollisionDir(player1);
        System.out.println(collisionDirP1 + "," + collisionDirP2);
        int initialPosition1 = player1.getX();
        int initialPosition2 = player2.getX();
        if (collisionDirP1 == Actor.MOVE_DOWN){
            player1.setY(player2.getY() - player1.getDrawHeight());
            player1.setSpeedY(-5);
            return;
        }
        if (collisionDirP1 == Actor.MOVE_UP){
            player2.setY(player1.getY() - player2.getDrawHeight());
            player2.setSpeedY(-5);
            return;
        }
        if (collisionDirP1 == Actor.MOVE_RIGHT && collisionDirP2 == Actor.MOVE_LEFT){
            player1.stop();
            player2.stop();
            player1.setX(initialPosition2 - player1.getDrawWidth());
            player2.setX(initialPosition2);
            System.out.println("???");
//            return;
        }
        if (collisionDirP1 == Actor.MOVE_LEFT && collisionDirP2 == Actor.MOVE_RIGHT){
            player2.stop();
            player2.setX(initialPosition1 - player2.getDrawWidth());
            player1.stop();
            player1.setX(initialPosition1);
//            return;
        }
    }

    private void collisionMechanism_2(Actor player1, Actor player2){
        int collisionDirP1 = player1.checkCollisionDir(player2);
        int collisionDirP2 = player2.checkCollisionDir(player1);
        System.out.println(collisionDirP1 + "," + collisionDirP2);
        float speedMain = player1.getSpeedX();
        float speedTarget = player2.getSpeedX();
        int speedDifference = (int) (Math.abs(speedMain) - Math.abs(speedTarget));
        if (collisionDirP1 == 0 && collisionDirP2 == 0){
            // no way
            return;
        }
        if (collisionDirP1 == 0 && collisionDirP2 == 1){
            player1.setY(player2.getY() - player1.getDrawHeight());
            player1.setSpeedY(-5);
            return;
        }
        if (collisionDirP1 == 0 && collisionDirP2 == 2){
            player1.setY(player2.getY() - player1.getDrawHeight());
            player1.setSpeedY(-5);
            return;
        }
        if (collisionDirP1 == 0 && collisionDirP2 == 3){
            player1.setY(player2.getY() - player1.getDrawHeight());
            player1.setSpeedY(-5);
            return;
        }
        if (collisionDirP1 == 1 && collisionDirP2 == 0){
            player2.setY(player1.getY() - player2.getDrawHeight());
            player2.setSpeedY(-5);
            return;
        }
        if (collisionDirP1 == 1 && collisionDirP2 == 1){
            // no way
            return;
        }
        if (collisionDirP1 == 1 && collisionDirP2 == 2){
            // p2從下方撞
            player1.setY(player2.getY() - player1.getDrawHeight());
            player1.setSpeedY(-5);
        }
        if (collisionDirP1 == 1 && collisionDirP2 == 3){
            if (speedDifference > 0){ // main 速率較大
                if (speedTarget > 0){
                    player2.setSpeedX(speedMain);
                    return;
                }
                if (speedTarget < 0){
                    player1.setSpeedX(speedDifference);
                    player2.setSpeedX(speedDifference);
                    return;
                }
            }
            if (speedDifference < 0){ // target 速率較大
                if (speedMain > 0){
                    player2.setSpeedX(-speedDifference);
                    player1.setSpeedX(-speedDifference);
                    return;
                }
                if (speedMain < 0){
                    player1.setSpeedX(speedTarget);
                    return;
                }
            }
        }
        if (collisionDirP1 == 2 && collisionDirP2 == 0){
            player2.setY(player1.getY() - player2.getDrawHeight());
            player2.setSpeedY(-5);
        }
        if (collisionDirP1 == 2 && collisionDirP2 == 1){
            // p1從下方撞
            player2.setY(player1.getY() - player2.getDrawHeight());
            player2.setSpeedY(-5);
        }
        if (collisionDirP1 == 2 && collisionDirP2 == 2){
            // no way
        }
        if (collisionDirP1 == 2 && collisionDirP2 == 3){
            // p1從下方撞
            player2.setY(player1.getY() - player2.getDrawHeight());
            player2.setSpeedY(-5);
        }
        if (collisionDirP1 == 3 && collisionDirP2 == 0){
            player2.setY(player1.getY() - player2.getDrawHeight());
            player2.setSpeedY(-5);
        }
        if (collisionDirP1 == 3 && collisionDirP2 == 1){
            if (speedDifference > 0){ // main 速率較大
                if (speedTarget < 0){
                    player2.setSpeedX(speedMain);
                    return;
                }
                if (speedTarget > 0){
                    player1.setSpeedX(-speedDifference);
                    player2.setSpeedX(-speedDifference);
                    return;
                }
            }
            if (speedDifference < 0){ // target 速率較大
                if (speedMain > 0){
                    player1.setSpeedX(speedTarget);
                    return;
                }
                if (speedMain < 0){
                    player2.setSpeedX(speedDifference);
                    player1.setSpeedX(speedDifference);
                    return;
                }
            }
        }
        if (collisionDirP1 == 3 && collisionDirP2 == 2){
            // p2從下方撞
            player1.setY(player2.getY() - player1.getDrawHeight());
            player1.setSpeedY(-5);
        }
        if (collisionDirP1 == 3 && collisionDirP2 == 3){
            // no way
        }
    }

    private void collisionMechanism_1(Actor main, Actor target) {
        int collisionDirP1 = main.checkCollisionDir(target);
        int collisionDirP2 = target.checkCollisionDir(main);
        System.out.println(collisionDirP1 + "," + collisionDirP2);
        float speedMain = main.getSpeedX();
        float speedTarget = target.getSpeedX();
        int speedDifference = (int) (Math.abs(speedMain) - Math.abs(speedTarget));
//        System.out.println(collisionDirP1 + "," + collisionDirP2);
        // 兩方都確認確實有碰撞到
        if (collisionDirP1 == Actor.MOVE_DOWN){
            main.setY(target.getY() - main.getDrawHeight());
            main.setSpeedY(-5);
            return;
        }
        if (collisionDirP2 == Actor.MOVE_DOWN){
            target.setY(main.getY() - target.getDrawHeight());
            target.setSpeedY(-5);
            return;
        }
        if (collisionDirP1 == Actor.MOVE_LEFT){
            if (speedDifference > 0){ // main 速率較大
                if (speedTarget < 0){
                    target.setSpeedX(speedMain);
                    target.changeDir(Actor.MOVE_RIGHT);
                    return;
                }
                if (speedTarget > 0){
                    main.setSpeedX(-speedDifference);
                    target.setSpeedX(-speedDifference);
                    target.changeDir(Actor.MOVE_LEFT);
                    return;
                }
            }
            if (speedDifference < 0){ // target 速率較大
                if (speedMain > 0){
                    main.setSpeedX(speedTarget);
                    return;
                }
                if (speedMain < 0){
                    target.setSpeedX(speedDifference);
                    main.setSpeedX(speedDifference);
                    main.changeDir(Actor.MOVE_RIGHT);
                    return;
                }
            }
        }
        if (collisionDirP1 == Actor.MOVE_RIGHT){
            if (speedDifference > 0){ // main 速率較大
                if (speedTarget > 0){
                    target.setSpeedX(speedMain);
                    return;
                }
                if (speedTarget < 0){
                    main.setSpeedX(speedDifference);
                    target.setSpeedX(speedDifference);
                    target.changeDir(Actor.MOVE_RIGHT);
                    return;
                }
            }
            if (speedDifference < 0){ // target 速率較大
                if (speedMain > 0){
                    target.setSpeedX(-speedDifference);
                    main.setSpeedX(-speedDifference);
                    main.changeDir(Actor.MOVE_LEFT);
                    return;
                }
                if (speedMain < 0){
                    main.setSpeedX(speedTarget);
                    return;
                }
            }
        }
        if (collisionDirP2 == Actor.MOVE_RIGHT){
            if (speedDifference > 0){
                if (speedTarget > 0){
                    main.setSpeedX(-speedDifference);
                    target.setSpeedX(-speedDifference);
                    target.changeDir(Actor.MOVE_LEFT);
                    return;
                }
                if (speedTarget < 0){
                    target.setSpeedX(speedMain);
                    return;
                }
            }
            if (speedDifference < 0){
                if (speedMain > 0){
                    main.setSpeedX(speedTarget);
                    return;
                }
                if (speedMain < 0){
                    target.setSpeedX(speedDifference);
                    main.setSpeedX(speedDifference);
                    main.changeDir(Actor.MOVE_RIGHT);
                    return;
                }
            }
        }
        if (collisionDirP2 == Actor.MOVE_LEFT){
            if (speedDifference > 0){
                if (speedTarget > 0){
                    target.setSpeedX(speedMain);
                    return;
                }
                if (speedTarget < 0){
                    main.setSpeedX(speedDifference);
                    target.setSpeedX(speedDifference);
                    target.changeDir(Actor.MOVE_RIGHT);
                    return;
                }
            }
            if (speedDifference < 0){
                if (speedMain > 0){
                    target.setSpeedX(-speedDifference);
                    main.setSpeedX(-speedDifference);
                    main.changeDir(Actor.MOVE_LEFT);
                    return;
                }
                if (speedMain < 0){
                    main.setSpeedX(speedTarget);
                    return;
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        background_0.paint(g);
        background_1.paint(g);
        fire_left.paint(g);
        fire_right.paint(g);
        roof.paint(g);
        hungerLabel1.paint(g);
        hungerBack1.paint(g);
        hungerCount1.paint(g);
        hungerLabel2.paint(g);
        hungerBack2.paint(g);
        hungerCount2.paint(g);
        for (Floor floor : floors) {
            floor.paint(g);
        }

        player1.paint(g);
        player2.paint(g);
        // 畫出 p1, p2
        g.setFont(TextManager.ENGLISH_FONT.deriveFont(16.0f));
        g.setColor(Color.RED);
        String pointer1 = "1P";
        fm = g.getFontMetrics();
        msgWidth = fm.stringWidth(pointer1);
        msgAscent = fm.getAscent();
        g.drawString(pointer1, player1.getX() - (msgWidth - player1.getDrawWidth())/ 2 ,player1.getY());
        String pointer2 = "2P";
        fm = g.getFontMetrics();
        msgWidth = fm.stringWidth(pointer2);
        msgAscent = fm.getAscent();
        g.drawString(pointer2, player2.getX() - (msgWidth - player2.getDrawWidth())/ 2, player2.getY());

        // 印出吃到食物的回覆值
        g.setFont(TextManager.ENGLISH_FONT.deriveFont(15.0f));
        g.setColor(Color.GREEN);
        String healMsg1 = "";
        String healMsg2 = "";
        if (showHeal1){
            if (++healDrawingCount1 <= 50){
                healMsg1 = "+ "+ eatenFood1.getHeal();
            }else {
                showHeal1 = false;
                healDrawingCount1 = 0;
            }
        }
        if (showHeal2){
            if (++healDrawingCount2 <= 50){
                healMsg2 = "+ "+ eatenFood2.getHeal();
            }else {
                showHeal2 = false;
                healDrawingCount2 = 0;
            }
        }
        fm = g.getFontMetrics();
        msgWidth = fm.stringWidth(healMsg1);
        msgAscent = fm.getAscent();
        g.drawString(healMsg1, player1.getX() - (msgWidth - player1.getDrawWidth())/ 2, player1.getY());
        g.drawString(healMsg2, player2.getX() - (msgWidth - player2.getDrawWidth())/ 2, player2.getY());

        // 印出飢餓值
        Font engFont = TextManager.ENGLISH_FONT.deriveFont(16.0f);
        Font chiFont = TextManager.CHINESE_FONT.deriveFont(36.0f);
        g.setFont(engFont);
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(hungerValue1), 96 + 112, 30);
        g.drawString(String.valueOf(hungerValue2), 96 + 112 + 200, 30);
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
        gsChangeListener.changeScene(MainPanel.TWO_PLAYER_GAME_SCENE);
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

    // 火把持續生成
    private void continueGeneration(GameObject gameObject){
        if (gameObject.getBottom() < 0){
            gameObject.setY(GameFrame.FRAME_HEIGHT);
        }
    }

    private void changeDirection(){
        if (!right_p1 && !left_p1 && !up_p1 && down_p1){
            player1.changeDir(Actor.MOVE_DOWN);
        }else if (!right_p1 && !left_p1 && up_p1 && !down_p1){
            player1.changeDir(Actor.MOVE_UP);
        }else if (!right_p1 && left_p1 && !up_p1 && !down_p1){
            player1.changeDir(Actor.MOVE_LEFT);
        }else if (right_p1 && !left_p1 && !up_p1 && !down_p1){
            player1.changeDir(Actor.MOVE_RIGHT);
        }
        if (!right_p2 && !left_p2 && !up_p2 && down_p2){
            player2.changeDir(Actor.MOVE_DOWN);
        }else if (!right_p2 && !left_p2 && up_p2 && !down_p2){
            player2.changeDir(Actor.MOVE_UP);
        }else if (!right_p2 && left_p2 && !up_p2 && !down_p2){
            player2.changeDir(Actor.MOVE_LEFT);
        }else if (right_p2 && !left_p2 && !up_p2 && !down_p2){
            player2.changeDir(Actor.MOVE_RIGHT);
        }
    }
}

