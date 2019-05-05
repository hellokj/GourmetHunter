package frame.scene;

import character.Actor;
import character.Button;
import character.GameObject;
import frame.MainPanel;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ModeScene extends Scene{
    private GameObject background, road, hole_top, hole;
    private GameObject character_1,character_2,character_3,character_4;
    private GameObject frame; // 人物選擇底版
    private GameObject choosingFrame; // 人物選框
    private Button buttonStory, buttonInfinity, button2P;
    private Actor player;
    private int key;
    private int countM,countI,count2;
    
    public ModeScene(MainPanel.GameStatusChangeListener gsChangeListener){
        super(gsChangeListener);
        this.background = new GameObject(0,-22,500, 700, "background/MenuBackground.png");
        this.choosingFrame = new GameObject(29,110,400,200,"background/ChooseFrame.png");
        this.frame = new GameObject(45,195,74,74,"background/Frame.png");
        this.character_1 = new GameObject(50,200,64,64,"Actor/Actor_1.png");
        this.character_2 = new GameObject(150,200,64,64,"Actor/Actor_2.png");
        this.character_3 = new GameObject(250,200,64,64,"Actor/Actor_3.png");
        this.character_4 = new GameObject(350,200,64,64,"Actor/Actor_4.png");
        this.road = new GameObject(0, 644, 600, 44, "background/Road.png");
        this.hole_top = new GameObject(400,630,64,32,"background/hole_top.png");
        this.hole = new GameObject(400,630,64,32,"background/hole.png");
        this.buttonStory = new Button(60,400, 100, 75, 150, 100, "button/Button_Story.png");
        this.buttonInfinity = new Button(190,400,100, 75, 150, 100,"button/Button_Infinity.png");
        this.button2P = new Button(320,400,100, 75, 150, 100,"button/Button_2P.png");
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
                    case KeyEvent.VK_1:
                        frame.setX(45);
                        break;
                    case KeyEvent.VK_2:
                        frame.setX(145);
                        break;
                    case KeyEvent.VK_3:
                        frame.setX(245);
                        break;
                    case KeyEvent.VK_4:
                        frame.setX(345);
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
        buttonStory.setImageOffsetX(0);
        buttonInfinity.setImageOffsetX(0);
        button2P.setImageOffsetX(0);
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT){
            player.acceleration();
        }
        player.update();
//        player.setBoundary(); // 更新完座標後，設定邊界
        player.stay();

        // 按鈕碰撞，打開故事模式的水溝蓋
        if(buttonStory.checkCollision(player)){
            buttonStory.setImageOffsetX(1);
            if (countM++ == 1){
                hole_top.setX(450);
                countM = 0;
            }
        }
        // 進入故事模式
        if(hole_top.getX()==450&&hole.checkCollision(player)){
            if (countM++ == 6){
                gsChangeListener.changeScene(MainPanel.LOADING_SCENE);
                countM = 0;
            }
        }

        // 按鈕碰撞，打開無限模式的水溝蓋
        if(buttonInfinity.checkCollision(player)){
            buttonInfinity.setImageOffsetX(1);
            if (countM++ == 1){
                hole_top.setX(451);
                countM = 0;
                }
            }
        // 進入無限模式
        if(hole_top.getX()==451&&hole.checkCollision(player)){
            if (countM++ == 6){
                gsChangeListener.changeScene(MainPanel.INFINITY_GAME_SCENE);
                countM = 0;
            }
        }

        // 按鈕碰撞，打開2p模式的水溝蓋
        if(button2P.checkCollision(player)){
            button2P.setImageOffsetX(1);
            if (countM++ == 1){
                hole_top.setX(452);
                countM = 0;
            }
        }
        if(hole_top.getX()==452&&hole.checkCollision(player)){
            if (countM++ == 6){
                gsChangeListener.changeScene(MainPanel.TWO_PLAYER_GAME_SCENE);
                countM = 0;
            }
        }
    }
    


    @Override
    public void paint(Graphics g) {
        background.paint(g);
        choosingFrame.paint(g);
        frame.paint(g);
        character_1.paint(g);
        character_2.paint(g);
        character_3.paint(g);
        character_4.paint(g);
        road.paint(g);
        buttonStory.paint(g);
        buttonInfinity.paint(g);
        button2P.paint(g);
        hole.paint(g);
        hole_top.paint(g);
        player.paint(g);
    }
}
