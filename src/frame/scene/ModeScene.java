package frame.scene;

import character.Actor;
import character.Button;
import character.GameObject;
import frame.MainPanel;
import util.ResourcesManager;
import util.TextManager;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ModeScene extends Scene{
    private GameObject background, road, hole_top, hole;
    private GameObject character_1,character_2,character_3,character_4;
    private GameObject frame; // 人物選擇底版
    private GameObject choosingFrame; // 人物選框
    private Button buttonStory, buttonInfinity, button2P;
    private Actor player1, player2;
    private int key;
    private int countM,countI,count2;
    private GameObject picker, picker1, picker2; // 選擇人物
    private boolean isPicked_1, isPicked_2; // 確認已選擇
    
    public ModeScene(MainPanel.GameStatusChangeListener gsChangeListener){
        super(gsChangeListener);
        this.background = new GameObject(0,-22,500, 700, "background/MenuBackground.png");
        this.choosingFrame = new GameObject(29,110,400,200,"background/ChooseFrame.png");
        this.frame = new GameObject(45,195,74,74,"background/Frame.png");
        this.picker = new GameObject(57 ,145, 50, 50, 100, 120, "background/Picker.png");
        this.picker1 = new GameObject(picker.getX() ,145, 50, 50, 100, 120, "background/Picker1.png");
        this.picker2 = new GameObject(160 ,145, 50, 50, 100, 120, "background/Picker2.png");
        this.character_1 = new GameObject(50,200,64,64,"actor/Actor_1.png");
        this.character_2 = new GameObject(150,200,64,64,"actor/Actor_2.png");
        this.character_3 = new GameObject(250,200,64,64,"actor/Actor_3.png");
        this.character_4 = new GameObject(350,200,64,64,"actor/Actor_4.png");
        this.road = new GameObject(0, 644, 600, 44, "background/Road.png");
        this.hole_top = new GameObject(400,630,64,32,"background/hole_top.png");
        this.hole = new GameObject(400,630,64,32,"background/hole.png");
        this.buttonStory = new Button(60,400, 100, 75, 150, 100, "button/Button_Story.png");
        this.buttonInfinity = new Button(190,400,100, 75, 150, 100,"button/Button_Infinity.png");
        this.button2P = new Button(320,400,100, 75, 150, 100,"button/Button_2P.png");
        MainPanel.player1.setX(250);
        MainPanel.player1.setY(700);
        this.player1 = MainPanel.player1;
        isPicked_1 = isPicked_2 = false;
    }
     @Override
    public KeyListener genKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                key = e.getKeyCode();
                switch (key){
                    case KeyEvent.VK_RIGHT:
                        player1.changeDir(Actor.MOVE_RIGHT);
                        break;
                    case KeyEvent.VK_LEFT:
                        player1.changeDir(Actor.MOVE_LEFT);
                        break;
                    case KeyEvent.VK_UP:
                        if (player1.canJump()){
                            player1.jump();
                        }
                        break;
                    case KeyEvent.VK_A:
                        if (hole_top.getX() == 452){
                            player2.changeDir(Actor.MOVE_LEFT);
                            break;
                        }
                    case KeyEvent.VK_D:
                        if (hole_top.getX() == 452){
                            player2.changeDir(Actor.MOVE_RIGHT);
                            break;
                        }
                    case KeyEvent.VK_W:
                        if (hole_top.getX() == 452){
                            if (player2.canJump()){
                                player2.jump();
                            }
                        }
                    case KeyEvent.VK_1:
                        picker.setX(57);
                        frame.setX(45);
                        if (!isPicked_1){
                            picker1.setX(57);
                            player1.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor1.png"));
                        }
                        if (hole_top.getX() == 452) {
                            if (isPicked_1 && !isPicked_2) {
                                picker2.setX(57);
                                player2.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor1.png"));
                            }
                        }
                        break;
                    case KeyEvent.VK_2:
                        picker.setX(160);
                        frame.setX(145);
                        if (!isPicked_1){
                            picker1.setX(160);
                            player1.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor2.png"));
                        }
                        if (hole_top.getX() == 452){
                            if (isPicked_1 && !isPicked_2){
                                picker2.setX(160);
                                player2.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor2.png"));
                            }
                        }
                        break;
                    case KeyEvent.VK_3:
                        picker.setX(257);
                        frame.setX(245);
                        if (!isPicked_1){
                            picker1.setX(257);
                            player1.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor3.png"));
                        }
                        if (hole_top.getX() == 452) {
                            if (isPicked_1 && !isPicked_2) {
                                picker2.setX(257);
                                player2.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor3.png"));
                            }
                        }
                        break;
                    case KeyEvent.VK_4:
                        picker.setX(357);
                        frame.setX(345);
                        if (!isPicked_1){
                            picker1.setX(357);
                            player1.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor4.png"));
                        }
                        if (hole_top.getX() == 452) {
                            if (isPicked_1 && !isPicked_2) {
                                picker2.setX(357);
                                player2.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor4.png"));
                            }
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        if (!isPicked_1){
                            isPicked_1 = true;
                        }else {
                            if (!isPicked_2){
                                isPicked_2 = true;
                            }
                        }
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
        MainPanel.checkLeftRightBoundary(player1);
        friction(player1);
        if (player1.checkOnObject(road)){
            player1.setCanJump(true); // 將可以跳躍設回true
            player1.setSpeedY(0); // 落到地板上，
        }
        // 設定按鈕圖片
        buttonStory.setImageOffsetX(0);
        buttonInfinity.setImageOffsetX(0);
        button2P.setImageOffsetX(0);

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT){
            player1.acceleration();
        }
        player1.update();
//        player1.setBoundary(); // 更新完座標後，設定邊界
        player1.stay();

        if (hole_top.getX() == 452){
            MainPanel.checkLeftRightBoundary(player2);
            friction(player2);
            if (player2.checkOnObject(road)){
                player2.setCanJump(true); // 將可以跳躍設回true
                player2.setSpeedY(0); // 落到地板上，
            }
            if (key == KeyEvent.VK_A || key == KeyEvent.VK_D){
                player2.acceleration();
            }
            player2.update();
            player2.stay();
        }

        // 按鈕碰撞，打開故事模式的水溝蓋
        if(buttonStory.checkCollision(player1)){
            this.isPicked_1 = this.isPicked_2 = false;
            buttonStory.setImageOffsetX(1);
            if (countM++ == 1){
                hole_top.setX(450);
                countM = 0;
            }
        }
        // 進入故事模式
        if(hole_top.getX()==450&&hole.checkCollision(player1)){
            if (countM++ == 6){
                gsChangeListener.changeScene(MainPanel.LOADING_SCENE);
                countM = 0;
            }
        }

        // 按鈕碰撞，打開無限模式的水溝蓋
        if(buttonInfinity.checkCollision(player1)){
            this.isPicked_1 = this.isPicked_2 = false;
            buttonInfinity.setImageOffsetX(1);
            if (countM++ == 1){
                hole_top.setX(451);
                countM = 0;
                }
            }
        // 進入無限模式
        if(hole_top.getX()==451&&hole.checkCollision(player1)){
            if (countM++ == 6){
                gsChangeListener.changeScene(MainPanel.INFINITY_GAME_SCENE);
                countM = 0;
            }
        }

        // 按鈕碰撞，打開2p模式的水溝蓋
        if(button2P.checkCollision(player1)){
            this.isPicked_1 = this.isPicked_2 = false;
            MainPanel.player2.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor2.png"));
            player2 = MainPanel.player2;
            button2P.setImageOffsetX(1);
            if (countM++ == 1){
                hole_top.setX(452);
                countM = 0;
            }
        }
        if(hole_top.getX()==452&&hole.checkCollision(player1)){
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
        if (!isPicked_1 || !isPicked_2){
            frame.paint(g);
        }
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
        if (hole_top.getX() != 452){
            picker.paint(g);
        }
        if (hole_top.getX() == 452){
            picker1.paint(g);
            picker2.paint(g);
            player2.paint(g);
            String msg = "2P";
            g.setFont(TextManager.ENGLISH_FONT.deriveFont(36.0f));
            g.setColor(Color.RED);
            FontMetrics fm = g.getFontMetrics();
            int msgWidth = fm.stringWidth(msg);
            int msgAscent = fm.getAscent();
            g.drawString(msg, player2.getX() + player2.getDrawWidth()/2 - msgWidth/2, player2.getY());
        }
        player1.paint(g);
        if (hole_top.getX() == 452){
            String msg = "1P";
            g.setFont(TextManager.ENGLISH_FONT.deriveFont(36.0f));
            g.setColor(Color.RED);
            FontMetrics fm = g.getFontMetrics();
            int msgWidth = fm.stringWidth(msg);
            int msgAscent = fm.getAscent();
            g.drawString(msg, player1.getX() + player1.getDrawWidth()/2 - msgWidth/2, player1.getY());
        }
    }

    private void checkPicker(){

    }
}
