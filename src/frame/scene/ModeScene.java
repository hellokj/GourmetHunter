package frame.scene;

import character.Actor;
import character.AnimationGameObject;
import character.Button;
import character.GameObject;
import frame.MainPanel;
import util.ResourcesManager;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ModeScene extends Scene{
    private GameObject background, road, hole_top, hole;
    private GameObject character_1,character_2,character_3,character_4;
    private AnimationGameObject arrow;
    private GameObject fattyFrame;
    private AnimationGameObject fattyCharacter;
    private GameObject frame; // 人物選擇底版
    private GameObject choosingFrame; // 人物選框
    private GameObject introduction; // 操作介紹
    private boolean isRead; // 確認已讀完訊息
    private Button buttonStory, buttonInfinity, button2P;
    private Actor player1, player2;
    private int key;
    private int countM,countI,count2;
    private GameObject picker, picker1, picker2; // 選擇人物
    private boolean isPicked_1, isPicked_2; // 確認已選擇

    // 模式選擇
    private boolean game_story, game_2p, game_infinity;

    // timer延遲調整
    private boolean up_p1 = false, down_p1 = false, left_p1 = false, right_p1 = false;
    private boolean up_p2 = false, down_p2 = false, left_p2 = false, right_p2 = false;

    private AudioClip bgm;
    
    public ModeScene(MainPanel.GameStatusChangeListener gsChangeListener){
        super(gsChangeListener);
        this.bgm = ResourcesManager.getInstance().getSound("sound/Menu.au");
        this.background = new GameObject(0,-22,500, 700, 600, 840,"background/MenuBackground.png");
        this.choosingFrame = new GameObject(50,110,400,200,400, 200,"background/ChooseFrame.png");
        this.frame = new GameObject(45+21,195,74,74, 74, 74,"background/Frame.png");
        this.fattyFrame = new GameObject(250 - 100, 110, 200, 200, 200, 200, "background/FattyFrame.png");
        this.fattyCharacter = new AnimationGameObject(200, 185, 100, 100, 32, 32, "actor/Actor1.png");
        int[] movingPatter = {0,1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ,11, 12, 13, 14, 15};
        this.arrow = new AnimationGameObject(395, 630 - 64, 64, 64, 64, 64, "background/arrow.png");
        int[] arrowMovingPattern = {0, 1};
        this.arrow.setMovingPattern(arrowMovingPattern);
        this.fattyCharacter.setMovingPattern(movingPatter);
        this.picker = new GameObject(57+21 ,145, 50, 50, 100, 120, "background/Picker.png");
        this.picker1 = new GameObject(frame.getX() ,270, 35, 35, 35, 35, "background/1P.png");
        this.picker2 = new GameObject(frame.getX() + 37 ,270, 35, 35, 35, 35, "background/2P.png");
        this.character_1 = new GameObject(50+21,200,64,64, 64, 64,"actor/Actor_1.png");
        this.character_2 = new GameObject(150+21,200,64,64,64, 64,"actor/Actor_2.png");
        this.character_3 = new GameObject(250+21,200,64,64,64, 64,"actor/Actor_3.png");
        this.character_4 = new GameObject(350+21,200,64,64,64, 64,"actor/Actor_4.png");
        this.road = new GameObject(0, 644, 600, 44, 600, 44,"background/Road.png");
        this.hole_top = new GameObject(400,630,64,32, 64, 32,"background/hole_top.png");
        this.hole = new GameObject(400,630,64,32, 64, 32,"background/hole.png");
        this.buttonStory = new Button(60,400, 100, 75, 150, 100, "button/Button_Story.png");
        this.buttonInfinity = new Button(190,400,100, 75, 150, 100,"button/Button_Infinity.png");
        this.button2P = new Button(320,400,100, 75, 150, 100,"button/Button_2P.png");
        this.isRead = false;
        this.player1 = new Actor(250, road.getY() - 32, 32, 32, 32, 32, MainPanel.P1);
        isPicked_1 = isPicked_2 = false;
        game_story = false;
        game_infinity = game_2p = false;
        this.introduction = new GameObject(this.player1.getX() - 32, this.player1.getY() - 180, 162, 180,225, 250, "background/ModeGuide.png");
    }
     @Override
    public KeyListener genKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                key = e.getKeyCode();
                switch (key){
                    case KeyEvent.VK_RIGHT:
                        right_p1 = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        left_p1 = true;
                        break;
                    case KeyEvent.VK_UP:
                        if (player1.canJump()){
                            player1.jump();
                        }
                        break;
                    case KeyEvent.VK_A:
                        if (game_2p){
                            left_p2 = true;
                            break;
                        }
                    case KeyEvent.VK_D:
                        if (game_2p){
                            right_p2 = true;
                            break;
                        }
                    case KeyEvent.VK_W:
                        if (game_2p){
                            if (player2.canJump()){
                                player2.jump();
                            }
                        }
                    case KeyEvent.VK_1:
                        if (game_infinity || game_2p){
                            picker.setX(57+21);
                            frame.setX(45+21);
                            if (!isPicked_1){
                                picker1.setX(frame.getX());
                                player1.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor1.png"));
                                MainPanel.P1 = "actor/Actor1.png";
                            }
                            if (game_2p) {
                                if (isPicked_1 && !isPicked_2) {
                                    picker2.setX(frame.getX() + 37);
                                    player2.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor1.png"));
                                    MainPanel.P2 = "actor/Actor1.png";
                                }
                            }
                        }
                        break;
                    case KeyEvent.VK_2:
                        if (game_infinity || game_2p){
                            picker.setX(160+21);
                            frame.setX(145+21);
                            if (!isPicked_1){
                                picker1.setX(frame.getX());
                                player1.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor2.png"));
                                MainPanel.P1 = "actor/Actor2.png";
                            }
                            if (game_2p){
                                if (isPicked_1 && !isPicked_2){
                                    picker2.setX(frame.getX()+37);
                                    player2.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor2.png"));
                                    MainPanel.P2 = "actor/Actor2.png";
                                }
                            }
                        }
                        break;
                    case KeyEvent.VK_3:
                        if (game_infinity || game_2p){
                            picker.setX(257+21);
                            frame.setX(245+21);
                            if (!isPicked_1){
                                picker1.setX(frame.getX());
                                player1.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor3.png"));
                                MainPanel.P1 = "actor/Actor3.png";
                            }
                            if (game_2p) {
                                if (isPicked_1 && !isPicked_2) {
                                    picker2.setX(frame.getX()+37);
                                    player2.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor3.png"));
                                    MainPanel.P2 = "actor/Actor3.png";
                                }
                            }
                        }
                        break;
                    case KeyEvent.VK_4:
                        if (game_infinity || game_2p){
                            picker.setX(357+21);
                            frame.setX(345+21);
                            if (!isPicked_1){
                                picker1.setX(frame.getX());
                                player1.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor4.png"));
                                MainPanel.P1 = "actor/Actor4.png";
                            }
                            if (game_2p) {
                                if (isPicked_1 && !isPicked_2) {
                                    picker2.setX(frame.getX()+37);
                                    player2.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor4.png"));
                                    MainPanel.P2 = "actor/Actor4.png";
                                }
                            }
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        isRead = true;
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e){
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
                    case KeyEvent.VK_ENTER:
                        if (game_infinity || game_2p){
                            if (!isPicked_1){
                                BUTTON_CLICK.play();
                                isPicked_1 = true;
                                frame.setX(45+21);
                            }else {
                                if (game_2p){
                                    if (!isPicked_2){
                                        BUTTON_CLICK.play();
                                        isPicked_2 = true;
                                    }
                                }
                            }
                        }
                        break;
                }
            }
        };
    }

    @Override
    public void logicEvent() {
        arrow.stay();
        if (game_story){
            fattyCharacter.stay();
        }
        changeDirection();
        MainPanel.checkLeftRightBoundary(player1);
        friction(player1);
        if (player2 != null){
            friction(player2);
        }
        if (player1.checkOnObject(road)){
            player1.setCanJump(true); // 將可以跳躍設回true
            player1.setSpeedY(0); // 落到地板上，
            countM = countI = count2 = 0;
        }
        // 設定按鈕圖片
        buttonStory.setImageOffsetX(0);
        buttonInfinity.setImageOffsetX(0);
        button2P.setImageOffsetX(0);

        if ((right_p1 || left_p1) && !player1.isStop()){
            player1.acceleration();
        }

        player1.update();
        introduction.setX(player1.getX());
        introduction.setY(player1.getY() - (int)(160*MainPanel.RATIO));
//        P1.setBoundary(); // 更新完座標後，設定邊界
        player1.stay();

        if (game_2p){
            MainPanel.checkLeftRightBoundary(player2);
            friction(player2);
            if (player2.checkOnObject(road)){
                player2.setCanJump(true); // 將可以跳躍設回true
                player2.setSpeedY(0); // 落到地板上
            }
            if ((right_p2 || left_p2) && !player2.isStop()){
                player2.acceleration();
            }
            player2.update();
            player2.stay();
        }

        // 按鈕碰撞，打開故事模式的水溝蓋
        if(buttonStory.checkCollision(player1)){
            player1.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor1.png"));
            game_story = true;
            game_2p = game_infinity = false;
            this.isPicked_1 = this.isPicked_2 = false;
            buttonStory.setImageOffsetX(1);
            if (countM++ == 1){
                BUTTON_CLICK.play();
                hole_top.setX(450);
            }
        }
        // 進入故事模式
        if(game_story && hole.checkCollision(player1)){
            bgm.stop();
            gsChangeListener.changeScene(MainPanel.LOADING_SCENE);
        }

        // 按鈕碰撞，打開無限模式的水溝蓋
        if(buttonInfinity.checkCollision(player1)){
            game_infinity = true;
            game_story = game_2p = false;
            this.isPicked_1 = this.isPicked_2 = false;
            buttonInfinity.setImageOffsetX(1);
            player1.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor1.png"));
            picker.setX(57+21);
            frame.setX(45+21);
            if (countI++ == 1){
                BUTTON_CLICK.play();
                hole_top.setX(450);
                }
            }
        // 進入無限模式
        if(game_infinity && hole.checkCollision(player1) && isPicked_1){
            bgm.stop();
            gsChangeListener.changeScene(MainPanel.INFINITY_GAME_SCENE);
        }

        // 按鈕碰撞，打開2p模式的水溝蓋
        if(button2P.checkCollision(player1)){
            game_2p = true;
            game_story = game_infinity = false;
            this.isPicked_1 = this.isPicked_2 = false;
            player2 = new Actor(250, this.road.getY() - 32, 32, 32, 32, 32, "actor/Actor1.png");
            player1.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor1.png"));
            player2.setImageFat(ResourcesManager.getInstance().getImage("actor/Actor1.png"));
            frame.setX(45+21);
            picker1.setX(frame.getX());
            picker2.setX(frame.getX()+37);
            button2P.setImageOffsetX(1);
            if (count2++ == 1){
                BUTTON_CLICK.play();
                hole_top.setX(450);
            }
        }
        if(game_2p && hole.checkCollision(player1) && hole.checkCollision(player2) && (isPicked_1 && isPicked_2)){
            bgm.stop();
            gsChangeListener.changeScene(MainPanel.TWO_PLAYER_GAME_SCENE);
        }
    }
    


    @Override
    public void paint(Graphics g, MainPanel mainPanel) {
        background.paint(g, mainPanel);
        if (game_story){
            arrow.paint(g, mainPanel);
            fattyFrame.paint(g, mainPanel);
            fattyCharacter.paint(g, mainPanel);
        }
        if (game_infinity || game_2p){
            choosingFrame.paint(g, mainPanel);
            if (game_infinity){
                if (isPicked_1){
                    arrow.paint(g, mainPanel);
                }
                if (!isPicked_1){
                    frame.paint(g, mainPanel);
                }
            }
            if (game_2p){
                if (isPicked_1 && isPicked_2){
                    arrow.paint(g, mainPanel);
                }
                if (!isPicked_1 || !isPicked_2){
                    frame.paint(g, mainPanel);
                }
            }
            character_1.paint(g, mainPanel);
            character_2.paint(g, mainPanel);
            character_3.paint(g, mainPanel);
            character_4.paint(g, mainPanel);
        }
        road.paint(g, mainPanel);
        buttonStory.paint(g, mainPanel);
        buttonInfinity.paint(g, mainPanel);
        button2P.paint(g, mainPanel);
        hole.paint(g, mainPanel);
        hole_top.paint(g, mainPanel);
        if (game_infinity && !isPicked_1){
            picker.paint(g, mainPanel);
        }
        if (game_2p){
            if (!isPicked_1){
                picker1.paint(g, mainPanel);
            }else {
                if (!isPicked_2){
                    picker2.paint(g, mainPanel);
                }
            }
            player2.paint(g, mainPanel);
            String msg = "2P";
            g.setFont(MainPanel.ENGLISH_FONT.deriveFont(36.0f*MainPanel.RATIO));
            g.setColor(Color.RED);
            FontMetrics fm = g.getFontMetrics();
            int msgWidth = fm.stringWidth(msg);
            int msgAscent = fm.getAscent();
            g.drawString(msg, (int) (player2.getModX() + player2.getDrawWidth()*MainPanel.RATIO /2 - msgWidth/2), player2.getModY());
        }
        player1.paint(g, mainPanel);
        if (game_2p){
            String msg = "1P";
            g.setFont(MainPanel.ENGLISH_FONT.deriveFont(36.0f*MainPanel.RATIO));
            g.setColor(Color.RED);
            FontMetrics fm = g.getFontMetrics();
            int msgWidth = fm.stringWidth(msg);
            int msgAscent = fm.getAscent();
            g.drawString(msg, (int) (player1.getModX() + player1.getDrawWidth()*MainPanel.RATIO /2 - msgWidth/2), player1.getModY());
        }

        if (!isRead){
            introduction.paint(g, mainPanel);
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
