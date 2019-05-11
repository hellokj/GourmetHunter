package frame.scene;

import character.*;
import character.Button;
import character.food.Food;
import character.trap.FlashTrap;
import character.trap.TrapGenerator;
import frame.GameFrame;
import frame.MainPanel;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
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
    private Button chooser;
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

    private int flashCount; //閃光延遲

    // show winner
    private Button restart, menu;
    private boolean p1_win, p2_win;
    private boolean isPlayingSound; // 判斷是否還在執行播放聲音
    private GameObject winnerBoard1, winnerBoard2;

    public TwoPlayerGameScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        BGM_TWO_PLAYER.loop();
        // 場景物件
        setSceneObject();
        roof = new GameObject(0, 0, 500, 64, 500, 64, "background/Roof.png");
        player1 = new Actor(240, 200, 32, 32, 32, 32, MainPanel.player1);
        player2 = new Actor(282, 200, 32, 32, 32, 32, MainPanel.player2);
        // 顯示板
        hungerLabel1 = new GameObject(28,8, 64, 32,64, 32, "background/HungerLabel.png");
        hungerLabel2 = new GameObject(278,8, 64, 32,64, 32, "background/HungerLabel.png");
        winnerBoard1 = new GameObject(250 - 150, 250, 300, 200, 300, 200, "background/Player1Win_1.png");
        winnerBoard2 = new GameObject(250 - 150, 250, 300, 200, 300, 200, "background/Player2Win_1.png");
        restart = new Button(117, 435 - 48, 72, 48, 150, 100, "button/Restart.png");
        menu = new Button(311, 435 - 48, 72, 48, 150, 100, "button/Button_Menu_R.png");
        // 飢餓值
        hungerBack1 = new GameObject(96, 16, 100, 16,5, 5, "background/Hunger.png");
        hungerCount1 = new GameObject(96, 16, 0, 16, 5, 5, "background/HungerCount.png");
        hungerBack2 = new GameObject(346, 16, 100, 16, 5, 5, "background/Hunger.png");
        hungerCount2 = new GameObject(346, 16, 0, 16, 5, 5, "background/HungerCount.png");
        // 初始10塊階梯
        floors = new ArrayList<>();
        floors.add(new Floor(player1.getX(), 200 + 32, TrapGenerator.getInstance().genSpecificTrap(0))); // 初始站立階梯
        for (int i = 0; i < 14; i++) {
            floors.add(FloorGenerator.getInstance().genFloor(floors, floors.get(i), 0));
        }
        isCalled = false;
        isPause = false;
        isPlayingSound = false;
        showLayer = false;
        p1_win = p2_win = false;
        layer = 0; // 從0層 開始
    }

    private void setSceneObject() {
        background_0 = new GameObject(0, 0, 500, 700, 500, 700, "background/Jungle2.png");
        background_1 = new GameObject(0, 700, 500, 700, 500, 700, "background/Jungle2.png");
        background_0.setBoundary();
        background_1.setBoundary();
        fire_left = new AnimationGameObject(0, (int) (background_0.getModY() + background_0.getDrawHeight()*MainPanel.ratio/2), 30, 30, 64, 64,"background/Fire.png");
        fire_right = new AnimationGameObject(470, (int) (background_0.getModY() + background_0.getDrawHeight()*MainPanel.ratio/2), 30, 30, 64, 64,"background/Fire.png");
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
                        if (p1_win || p2_win){
                            VICTORY.stop();
                            isPlayingSound = false;
                            gsChangeListener.changeScene(MainPanel.MENU_SCENE);
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (!isPause){
                            left_p1 = true;
                        }
                        if (p1_win || p2_win){
                            VICTORY.stop();
                            isPlayingSound = false;
                            reset();
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
                            if (!(cursor.getY() + 150 > button_menu.getY() + button_menu.getDrawHeight())){
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
                            if (!(cursor.getY() + 150 > button_menu.getY() + button_menu.getDrawHeight())){
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
                }
            }

            @Override
            public  void keyReleased(KeyEvent e){
                if (!isPause){
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
                            BGM_INFINITY.stop();
                            gsChangeListener.changeScene(MainPanel.MENU_SCENE);
                        }
                        isCalled = false;
                    }
                }
            }
        };
    }

    @Override
    public void logicEvent() {
        if (!isPause){
            if (!player1.isDie() && !player2.isDie()){ // 還沒死亡的狀態
                if (isDark){
                    if (darkDelay++ == 120){
                        isDark = false;
                        darkDelay = 0;
                    }
                }
                MainPanel.checkLeftRightBoundary(player1);
                MainPanel.checkLeftRightBoundary(player2);
                changeDirection();
                int floorAmount = checkSceneFloorAmount();
                hungerValue1 = player1.getHunger();
                hungerValue2 = player2.getHunger();
                if (floorAmount < 15 && floors.size() < 20){
                    for (int i = 0; i < 15 - floorAmount; i++) {
                        // 傳入現在層數，生成器將依此更新生成機率
                        floors.add(FloorGenerator.getInstance().genFloor(floors, findLast(), layer));
                    }
                }
                // 逆向摩擦力
                friction(player1);
                friction(player2);

                for (int i = 0; i < floors.size(); i++) {
                    player1.checkOnFloor(floors.get(i), this);
                    player2.checkOnFloor(floors.get(i), this);
                    // 吃食物機制
                    if (player1.eat(floors.get(i).getFood())){
                        Scene.HEAL.play();
                        eatenFood1 = floors.get(i).getFood();
                        if (eatenFood1 != null){
                            showHeal1 = true;
                        }
                        floors.get(i).setFood(null); // 吃完，食物設回null
                    }
                    if (player2.eat(floors.get(i).getFood())){
                        Scene.HEAL.play();
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
                player1.hunger();
                player2.hunger();
                // 繪製現在飢餓值
                hungerCount1.setDrawWidth(player1.getHunger());
                hungerCount2.setDrawWidth(player2.getHunger());
                // 每次都要更新此次座標
                for (Floor floor : floors) {
                    floor.update();
                }
                if ((right_p1 || left_p1) && !player1.isStop()){
                    player1.acceleration();
                }
                if ((right_p2 || left_p2) && !player2.isStop()){
                    player2.acceleration();
                }
                // 兩人碰撞機制
//                twoPlayer_v1();
//                twoPlayer_v2();
                twoPlayer_v3();
                player1.update();
                player2.update();
                // 掉落死亡 or 餓死後落下
                if (player1.getModY() + player1.getDrawHeight() * MainPanel.ratio > MainPanel.window.height){
                    player1.die();
                }
                if (player2.getModY() + player2.getDrawHeight() * MainPanel.ratio > MainPanel.window.height){
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
                        if (player1.getModY() + player1.getDrawHeight()*MainPanel.ratio > MainPanel.window.height){
                            BGM_TWO_PLAYER.stop();
                            if (!isPlayingSound){
                                VICTORY.loop();
                                isPlayingSound = true;
                            }
                            p1_win = false;
                            p2_win = true;
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
                        if (player2.getModY() + player2.getDrawHeight() * MainPanel.ratio > MainPanel.window.height){
                            BGM_TWO_PLAYER.stop();
                            if (!isPlayingSound){
                                VICTORY.loop();
                                isPlayingSound = true;
                            }
                            p2_win = false;
                            p1_win = true;
//                            gsChangeListener.changeScene(MainPanel.GAME_OVER_SCENE);
                        }
                    }
                }
            }
        }
    }

    private void twoPlayer_v3() {
        collisionMechanism_3(player1, player2);
        collisionMechanism_3(player2, player1);
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
        if (collisionDirP1 != -1 || collisionDirP2 != -1){
            BUMP.play();
        }
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
//            player1.stop();
//            player2.stop();
            player1.setX(initialPosition2 - player1.getDrawWidth());
            player2.setX(initialPosition2);
            System.out.println("???");
//            return;
        }
        if (collisionDirP1 == Actor.MOVE_LEFT && collisionDirP2 == Actor.MOVE_RIGHT){
//            player2.stop();
            player2.setX(initialPosition1 - player2.getDrawWidth());
//            player1.stop();
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
    public void paint(Graphics g, MainPanel mainPanel) {
        Graphics2D g2d = (Graphics2D)g;
        Graphics2D g2d2 = (Graphics2D)g.create();
        if (isDark){
            background_0.paint(g2d, mainPanel);
            background_1.paint(g2d, mainPanel);
            fire_left.paint(g2d, mainPanel);
            fire_right.paint(g2d, mainPanel);
            roof.paint(g2d, mainPanel);
            hungerLabel1.paint(g2d, mainPanel);
            hungerBack1.paint(g2d, mainPanel);
            hungerCount1.paint(g2d, mainPanel);
            hungerLabel2.paint(g2d, mainPanel);
            hungerBack2.paint(g2d, mainPanel);
            hungerCount2.paint(g2d, mainPanel);

            for (Floor floor : floors) {
                floor.paint(g, mainPanel);
            }

            g2d.drawImage(darkness, 0, (int) (48 * MainPanel.ratio), (int) (500 * MainPanel.ratio), (int) (700 * MainPanel.ratio), 0, 0, 1024, 768, null);

            player1.paint(g2d, mainPanel);
            player2.paint(g2d, mainPanel);
            // 畫出 p1, p2 指示
            g2d.setFont(MainPanel.ENGLISH_FONT.deriveFont(16.0f*MainPanel.ratio));
            g2d.setColor(Color.RED);
            String pointer1 = "1P";
            fm = g2d.getFontMetrics();
            msgWidth = fm.stringWidth(pointer1);
            msgAscent = fm.getAscent();
            g2d.drawString(pointer1, player1.getModX() - (msgWidth - player1.getDrawWidth()*MainPanel.ratio)/ 2 ,player1.getModY());
            String pointer2 = "2P";
            fm = g2d.getFontMetrics();
            msgWidth = fm.stringWidth(pointer2);
            msgAscent = fm.getAscent();
            g2d.drawString(pointer2, player2.getModX() - (msgWidth - player2.getDrawWidth()*MainPanel.ratio)/ 2, player2.getModY());

            // 印出吃到食物的回覆值
            g2d.setFont(MainPanel.ENGLISH_FONT.deriveFont(15.0f*MainPanel.ratio));
            g2d.setColor(Color.GREEN);
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
            fm = g2d.getFontMetrics();
            msgWidth = fm.stringWidth(healMsg1);
            msgAscent = fm.getAscent();
            g2d.drawString(healMsg1, player1.getModX() - (msgWidth*MainPanel.ratio - player1.getDrawWidth()*MainPanel.ratio)/ 2, player1.getModY());
            g2d.drawString(healMsg2, player2.getModX() - (msgWidth*MainPanel.ratio - player2.getDrawWidth()*MainPanel.ratio)/ 2, player2.getModY());

            // 印出飢餓值
            Font engFont = MainPanel.ENGLISH_FONT.deriveFont(16.0f*MainPanel.ratio);
            Font chiFont = MainPanel.CHINESE_FONT.deriveFont(36.0f*MainPanel.ratio);
            g2d.setFont(engFont);
            g2d.setColor(Color.WHITE);
            g2d.drawString(String.valueOf(hungerValue1), (96 + 112)*MainPanel.ratio, 30*MainPanel.ratio);
            g2d.drawString(String.valueOf(hungerValue2), (96 + 112 + 250)*MainPanel.ratio, 30*MainPanel.ratio);
            g2d.setFont(chiFont);

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
//        g2d.drawString(msg, 250*MainPanel.ratio - msgWidth/2, 350*MainPanel.ratio);
//        g2d.setFont(chiFont.deriveFont(16.0f*MainPanel.ratio));
//        g2d.drawString("地下 " + layer + " 層", 365*MainPanel.ratio, 30*MainPanel.ratio);

            //閃光開始
            if(FlashTrap.getFlashState()){
                flashCount++;
            }//閃光持續
            if(flashCount <15 && flashCount >0){
                FlashTrap.getFlash().setCounter(flashCount -1);
                //System.out.println("**"+flashCount);
                FlashTrap.getFlash().paint(g2d, mainPanel);
            }//閃光結束
            else if(flashCount >=15){
                FlashTrap.setFlashState(false);
                flashCount = 0;
            }

            // 印出選單
            if (isCalled){
                button_menu.paint(g2d, mainPanel);
                button_resume.paint(g2d, mainPanel);
                button_new_game.paint(g2d, mainPanel);
                cursor.paint(g2d, mainPanel);
            }

            if (p1_win){
                winnerBoard1.paint(g, mainPanel);
                restart.paint(g, mainPanel);
                menu.paint(g, mainPanel);
            }
            if (p2_win){
                winnerBoard2.paint(g, mainPanel);
                restart.paint(g, mainPanel);
                menu.paint(g, mainPanel);
            }
            g2d.setClip(new Ellipse2D.Float(player1.getCenterPoint().x - 75 * MainPanel.ratio, player1.getCenterPoint().y - 75 * MainPanel.ratio, 150 * MainPanel.ratio, 150 * MainPanel.ratio));
            g2d.setClip(new Ellipse2D.Float(player2.getCenterPoint().x - 75 * MainPanel.ratio, player2.getCenterPoint().y - 75 * MainPanel.ratio, 150 * MainPanel.ratio, 150 * MainPanel.ratio));
        }
        background_0.paint(g2d, mainPanel);
        background_1.paint(g2d, mainPanel);
        fire_left.paint(g2d, mainPanel);
        fire_right.paint(g2d, mainPanel);
        roof.paint(g2d, mainPanel);
        hungerLabel1.paint(g2d, mainPanel);
        hungerBack1.paint(g2d, mainPanel);
        hungerCount1.paint(g2d, mainPanel);
        hungerLabel2.paint(g2d, mainPanel);
        hungerBack2.paint(g2d, mainPanel);
        hungerCount2.paint(g2d, mainPanel);

        for (Floor floor : floors) {
            floor.paint(g, mainPanel);
        }

        player1.paint(g2d, mainPanel);
        player2.paint(g2d, mainPanel);
        // 畫出 p1, p2
        g2d.setFont(MainPanel.ENGLISH_FONT.deriveFont(16.0f*MainPanel.ratio));
        g2d.setColor(Color.RED);
        String pointer1 = "1P";
        fm = g2d.getFontMetrics();
        msgWidth = fm.stringWidth(pointer1);
        msgAscent = fm.getAscent();
        g2d.drawString(pointer1, player1.getModX() - (msgWidth - player1.getDrawWidth()*MainPanel.ratio)/ 2 ,player1.getModY());
        String pointer2 = "2P";
        fm = g2d.getFontMetrics();
        msgWidth = fm.stringWidth(pointer2);
        msgAscent = fm.getAscent();
        g2d.drawString(pointer2, player2.getModX() - (msgWidth - player2.getDrawWidth()*MainPanel.ratio)/ 2, player2.getModY());

        // 印出吃到食物的回覆值
        g2d.setFont(MainPanel.ENGLISH_FONT.deriveFont(15.0f*MainPanel.ratio));
        g2d.setColor(Color.GREEN);
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
        fm = g2d.getFontMetrics();
        msgWidth = fm.stringWidth(healMsg1);
        msgAscent = fm.getAscent();
        g2d.drawString(healMsg1, player1.getModX() - (msgWidth*MainPanel.ratio - player1.getDrawWidth()*MainPanel.ratio)/ 2, player1.getModY());
        g2d.drawString(healMsg2, player2.getModX() - (msgWidth*MainPanel.ratio - player2.getDrawWidth()*MainPanel.ratio)/ 2, player2.getModY());

        // 印出飢餓值
        Font engFont = MainPanel.ENGLISH_FONT.deriveFont(16.0f*MainPanel.ratio);
        Font chiFont = MainPanel.CHINESE_FONT.deriveFont(36.0f*MainPanel.ratio);
        g2d.setFont(engFont);
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.valueOf(hungerValue1), (96 + 112)*MainPanel.ratio, 30*MainPanel.ratio);
        g2d.drawString(String.valueOf(hungerValue2), (96 + 112 + 250)*MainPanel.ratio, 30*MainPanel.ratio);
        g2d.setFont(chiFont);

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
//        g2d.drawString(msg, 250*MainPanel.ratio - msgWidth/2, 350*MainPanel.ratio);
//        g2d.setFont(chiFont.deriveFont(16.0f*MainPanel.ratio));
//        g2d.drawString("地下 " + layer + " 層", 365*MainPanel.ratio, 30*MainPanel.ratio);

        //閃光開始
        if(FlashTrap.getFlashState()){
            flashCount++;
        }//閃光持續
        if(flashCount <15 && flashCount >0){
            FlashTrap.getFlash().setCounter(flashCount -1);
            //System.out.println("**"+flashCount);
            FlashTrap.getFlash().paint(g2d, mainPanel);
        }//閃光結束
        else if(flashCount >=15){
            FlashTrap.setFlashState(false);
            flashCount = 0;
        }

        // 印出選單
        if (isCalled){
            button_menu.paint(g2d, mainPanel);
            button_resume.paint(g2d, mainPanel);
            button_new_game.paint(g2d, mainPanel);
            cursor.paint(g2d, mainPanel);
        }

        if (p1_win){
            winnerBoard1.paint(g, mainPanel);
            restart.paint(g, mainPanel);
            menu.paint(g, mainPanel);
        }
        if (p2_win){
            winnerBoard2.paint(g, mainPanel);
            restart.paint(g, mainPanel);
            menu.paint(g, mainPanel);
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

    // 更新背景圖
    private void updateBackgroundImage(){
        int background_rising_speed = 5;
        if (background_0.getModY() + background_0.getDrawHeight()*MainPanel.ratio <= 0){
            background_0 = new GameObject(0, 700, 500, 700, 500, 700, "background/Jungle2.png");
            layer++;
            showLayer = true;
        }
        if (background_1.getModY() + background_1.getDrawHeight()*MainPanel.ratio <= 0){
            background_1 = new GameObject(0, 700, 500, 700, 500, 700, "background/Jungle2.png");
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

