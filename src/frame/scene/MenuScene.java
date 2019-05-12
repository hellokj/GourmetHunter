package frame.scene;

import character.Actor;
import character.Button;
import character.GameObject;
import frame.MainPanel;
import util.ResourcesManager;
import util.TypingMachine;


import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MenuScene extends Scene{
    private GameObject background, logo, road, flower, introduction;
    private Button buttonMode, buttonLeader, buttonGuide;
    private Actor player;
    private int countM,countL,countE; // 碰觸按鈕延遲
    private int key;
    private boolean isRead; // 確認閱讀完說明

    // 人物操控
    private boolean up = false, down = false, left = false, right = false;

    public MenuScene(MainPanel.GameStatusChangeListener gsChangeListener){
        super(gsChangeListener);
        MainPanel.player1 = "actor/Actor1.png";
        this.background = new GameObject(0,-22,500, 700,600, 840, "background/MenuBackground.png");
        this.logo = new GameObject(50,60,400,200,300, 100,"background/Logo.png");
        this.road = new GameObject(0, 644, 600, 44, 600, 44, "background/Road.png");
        this.flower = new GameObject(50, 644 - 24, 21, 24, 28, 32, "background/dancing.png");
        this.buttonMode = new Button(60,400, 100, 75, 150, 100, "button/Button_Mode.png");
        this.buttonLeader = new Button(190,400,100, 75, 150, 100,"button/Button_LB.png");
        this.buttonGuide = new Button(320,400,100, 75, 150, 100,"button/Button_Guide.png");
//        MainPanel.player1.setY(road.getY() - 32);
        this.player = new Actor(250, road.getY() - 32, 32, 32, 32, 32, MainPanel.player1);
        this.introduction = new GameObject(this.player.getX(), this.player.getY() - 144, 162, 144,225, 200, "background/MenuGuide.png");
        this.isRead = false;
        BGM_MENU.loop();
    }

    @Override
    public KeyListener genKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                key = e.getKeyCode();
                switch (key){
                    case KeyEvent.VK_RIGHT:
                        right = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        left = true;
                        break;
                    case KeyEvent.VK_UP:
                        if (player.canJump()){
                            player.jump();
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        isRead = true;
                }
            }
            @Override
            public void keyReleased(KeyEvent e){
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
        };
    }

    @Override
    public void logicEvent() {
        MainPanel.checkLeftRightBoundary(player);
        changeDirection();
        friction(player);
        // 設定按鈕圖片
        buttonMode.setImageOffsetX(0);
        buttonLeader.setImageOffsetX(0);
        buttonGuide.setImageOffsetX(0);
        if ((right || left) && !player.isStop()){
            player.acceleration();
        }
        if (player.checkOnObject(road)){
            player.setCanJump(true); // 將可以跳躍設回true
            player.setSpeedY(0); // 落到地板上
        }
        player.update();

        introduction.setX(player.getX());
        introduction.setY(player.getY() - (int)(160*MainPanel.ratio));
//        player.setBoundary(); // 更新完座標後，設定邊界
        player.stay();

        // 按鈕碰撞換圖
        // 切換至模式場景
        if(buttonMode.checkCollision(player)){
            buttonMode.setImageOffsetX(1);
            if (countM == 1){
                BUTTON_CLICK.play();
            }
            if (countM++ == 20){ // 一個延遲後切換場景
                gsChangeListener.changeScene(MainPanel.MODE_SCENE);
                countM = 0;
            }
        }

        // 切換至排行榜場景
        if(buttonLeader.checkCollision(player)){
            buttonLeader.setImageOffsetX(1);
            if (countL == 1){
                BUTTON_CLICK.play();
            }
            if (countL++ == 20){ // 一個延遲後切換場景
                gsChangeListener.changeScene(MainPanel.LEADER_BOARD_SCENE);
                countL = 0;
            }
        }

        // 遊戲介紹
        if(buttonGuide.checkCollision(player)){
            buttonGuide.setImageOffsetX(1);
            if (countE == 1){
                BUTTON_CLICK.play();
            }
            if (countE++ == 20){ // 一個延遲後切換場景
                gsChangeListener.changeScene(MainPanel.GUIDE_SCENE_1);
                countE = 0;
            }
        }

        if (flower.checkCollision(player)){
            if (key == KeyEvent.VK_UP){
                gsChangeListener.changeScene(MainPanel.DANCING_GAME_SCENE);
            }
        }
    }


    @Override
    public void paint(Graphics g, MainPanel mainPanel) {
        background.paint(g, mainPanel);
        road.paint(g, mainPanel);
        buttonMode.paint(g, mainPanel);
        buttonLeader.paint(g, mainPanel);
        buttonGuide.paint(g, mainPanel);
        logo.paint(g, mainPanel);
        flower.paint(g, mainPanel);
        player.paint(g, mainPanel);
        if (!isRead){
            introduction.paint(g, mainPanel);
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
}