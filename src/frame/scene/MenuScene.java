package frame.scene;

import character.Actor;
import character.Button;
import character.GameObject;
import frame.MainPanel;


import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MenuScene extends Scene{
    private GameObject background, logo, road;
    private Button buttonMode, buttonLeader, buttonExit;
    private Actor player;
    private int countM,countL,countE; // 碰觸按鈕延遲
    private int key;

    public MenuScene(MainPanel.GameStatusChangeListener gsChangeListener){
        super(gsChangeListener);
        this.background = new GameObject(0,-22,500, 700, "background/MenuBackground.png");
        this.logo = new GameObject(50,60,400,200,"background/Logo.png");
        this.road = new GameObject(0, 644, 600, 44, "background/Road.png");
        this.buttonMode = new Button(60,400, 100, 75, 150, 100, "button/Button_Mode.png");
        this.buttonLeader = new Button(190,400,100, 75, 150, 100,"button/Button_LB.png");
        this.buttonExit = new Button(320,400,100, 75, 150, 100,"button/Button_Exit.png");
        this.player = new Actor(250, 700, 32, 32, 32, 32, "actor/Actor1.png");
    }

    @Override
    public KeyListener genKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                key = e.getKeyCode();
                switch (key){
                    case KeyEvent.VK_RIGHT:
                        player.changeDir(Actor.MOVE_RIGHT);
                        break;
                    case KeyEvent.VK_LEFT:
                        player.changeDir(Actor.MOVE_LEFT);
                        break;
                    case KeyEvent.VK_SPACE:
                        if (player.canJump()){
                            player.jump();
                        }
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e){
                if (key == e.getKeyCode()){
                    key = -1;
                }
            }
        };
    }

    @Override
    public void logicEvent() {
        MainPanel.checkLeftRightBoundary(player);
        friction(player);
        if (player.checkOnObject(road)){
            player.setCanJump(true); // 將可以跳躍設回true
            player.setSpeedY(0); // 落到地板上，
        }
        // 設定按鈕圖片
        buttonMode.setImageOffsetX(0);
        buttonLeader.setImageOffsetX(0);
        buttonExit.setImageOffsetX(0);
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_LEFT){
            player.acceleration();
        }
        player.update();
//        player.setBoundary(); // 更新完座標後，設定邊界
        player.stay();

        // 按鈕碰撞換圖
        // 切換至模式場景
        if(buttonMode.checkCollision(player)){
            buttonMode.setImageOffsetX(1);
            if (countM++ == 40){ // 一個延遲後切換場景
                gsChangeListener.changeScene(MainPanel.MODE_SCENE);
                countM = 0;
            }
        }

        // 切換至排行榜場景
        if(buttonLeader.checkCollision(player)){
            buttonLeader.setImageOffsetX(1);
            if (countL++ == 40){ // 一個延遲後切換場景
                gsChangeListener.changeScene(MainPanel.LEADER_BOARD_SCENE);
                countL = 0;
            }
        }

        // 結束遊戲
        if(buttonExit.checkCollision(player)){
            buttonExit.setImageOffsetX(1);
            if (countE++ == 40){ // 一個延遲後切換場景
                System.exit(1);
                countE = 0;
            }
        }
    }


    @Override
    public void paint(Graphics g) {
        background.paint(g);
        road.paint(g);
        buttonMode.paint(g);
        buttonLeader.paint(g);
        buttonExit.paint(g);
        logo.paint(g);
        player.paint(g);
    }
}