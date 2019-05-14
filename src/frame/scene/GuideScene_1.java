/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frame.scene;
import character.Actor;
import character.AnimationGameObject;
import character.Button;
import character.Floor;
import frame.MainPanel;

import character.GameObject;
import character.trap.FlashTrap;
import character.trap.TrapGenerator;

import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author s7207
 */
public class GuideScene_1 extends Scene {
    private GameObject background;
    private GameObject frameS,frameG,frameS2,frameG2;
    private GameObject hint,num1,num2,num3,num4;
    private TrapGenerator tg;
    private Floor floorSpring,floorRunning,floorFlashing;
    private Actor player1,player2,player3,player4;
    private int flashDelayCount; //閃光延遲
    private AnimationGameObject DanceTest;
    private int choose;
    private int[] directions = {Actor.MOVE_DOWN, Actor.MOVE_LEFT, Actor.MOVE_UP, Actor.MOVE_RIGHT, Actor.MOVE_DOWN, Actor.MOVE_DOWN};
    private int direction;
    private Button menu,next;
    
    public GuideScene_1(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        this.tg = new TrapGenerator();
        this.background = new GameObject(0,0,500,700,1024, 768,"background/EndBackground.png");
        this.frameS = new GameObject(80,70,150,250, 150, 250,"background/frameS.png");
        this.frameG = new GameObject(250,70,150,250,150, 250,"background/frameG.png");
        this.frameG2 = new GameObject(80,330,150,250,150, 250,"background/frameG.png");
        this.frameS2 = new GameObject(250,330,150,250,150, 250,"background/frameS.png");
        this.hint = new GameObject(10,10,300,50,300, 50,"background/Guide.png");
        this.num1 = new GameObject(90,80,30,30,32, 32,"background/num1.png");
        this.num2 = new GameObject(260,80,30,30,32, 32,"background/num2.png");
        this.num3 = new GameObject(90,340,30,30,32, 32,"background/num3.png");
        this.num4 = new GameObject(260,340,30,30,32, 32,"background/num4.png");
        floorRunning = new Floor(130, 270, tg.genSpecificTrap(TrapGenerator.TRAP_RUNNING));
        floorSpring = new Floor(130, 530, tg.genSpecificTrap(TrapGenerator.TRAP_SPRING));
        floorFlashing = new Floor(300, 530, tg.genSpecificTrap(TrapGenerator.TRAP_FLASH));
        DanceTest = new AnimationGameObject(300, 230, 64, 80, 64, 80,"floor/DanceTest.png");
        int[] moving = {0,1,2,3,4,5};
        direction = 0;
        DanceTest.setMovingPattern(moving);
        DanceTest.setStayDelay(20);
        player1 = new Actor(140, 130, 32, 32, 32, 32, "actor/Actor1.png");
        player2 = new Actor(310, 130, 32, 32,32, 32, "actor/Actor1.png");
        player3 = new Actor(140, 390, 32, 32,32, 32, "actor/Actor1.png");
        player4 = new Actor(310, 390, 32, 32,32, 32, "actor/Actor1.png");
        menu = new Button(0,580,120,70,150,100,"button/Button_Menu_L.png");
        next = new Button(360,580,120,70,150,100,"button/Button_Next_R.png");
    }
    @Override
    public KeyListener genKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                int key = e.getKeyCode();
                switch (key){                   
                    case KeyEvent.VK_1:
                        BUTTON_CLICK.play();
                        setChoose(1);
                        break;
                    case KeyEvent.VK_2:
                        BUTTON_CLICK.play();
                        setChoose(2);
                        break;
                    case KeyEvent.VK_3:
                        BUTTON_CLICK.play();
                        setChoose(3);
                        break;
                    case KeyEvent.VK_4:
                        BUTTON_CLICK.play();
                        setChoose(4);
                        break;
                    case KeyEvent.VK_RIGHT:
                        next.setImageOffsetX(1);
                        break;
                    case KeyEvent.VK_LEFT:
                        menu.setImageOffsetX(1);
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e){
                int key = e.getKeyCode();
                if(key==KeyEvent.VK_RIGHT){
                    next.setImageOffsetX(0);
                    BUTTON_CLICK.play();
                    gsChangeListener.changeScene(MainPanel.GUIDE_SCENE_2);
                }
                if(key==KeyEvent.VK_LEFT){
                    menu.setImageOffsetX(0);
                    BUTTON_CLICK.play();
                    gsChangeListener.changeScene(MainPanel.MENU_SCENE);
                }
            }
        };
    }
    private void setChoose(int choose){
        this.choose = choose;
    }
    private void playerStop(Actor player,int x,int y){
        player.setX(x);
        player.setY(y);
    }

    @Override
    public void logicEvent() {
        floorRunning.stay();
        floorSpring.stay();
        floorFlashing.stay();
        if(choose==2){
            player2.stay();
            player2.update();
            if(player2.checkOnObject(DanceTest)){
                DanceTest.stay();
                if ((DanceTest.getStayDelayCount()) % DanceTest.getStayDelay() == 0){
                    player2.changeDir(directions[direction++]);
                    direction = direction % directions.length;
                }
                playerStop(player1, 140, 130);
                playerStop(player3, 140, 390);
                playerStop(player4, 310, 390);
            }
        }
        else if(choose==1){
            this.outOfFrame(player1, floorRunning, frameS, 140, 150);
            playerStop(player2, 310, 130);
            playerStop(player3, 140, 390);
            playerStop(player4, 310, 390);
            DanceTest.setImageOffsetX(0);
        }
        else if(choose==3){
            this.outOfFrame(player3, floorSpring, frameG2, 140, 410);
            playerStop(player1, 140, 130);
            playerStop(player2, 310, 130);
            playerStop(player4, 310, 390);
            DanceTest.setImageOffsetX(0);
        }
        else if(choose==4){
            this.outOfFrame(player4, floorFlashing, frameS2, 310, 410);
            playerStop(player1, 140, 130);
            playerStop(player2, 310, 130);
            playerStop(player3, 140, 390);
            DanceTest.setImageOffsetX(0);
        }
         
    }
    public void outOfFrame(Actor player, Floor floor, GameObject frame, int x, int y){
        player.stay();
        player.update();
        player.checkOnFloor(floor, this);
        if(player.getBottom()>frame.getBottom()||player.getLeft()<frame.getLeft()||
            player.getRight()>frame.getRight()){
            player.setX(x);
            player.setY(y);
            player.setSpeedX(0);
        }
    }
    @Override
    public void paint(Graphics g, MainPanel mainPanel) {
        background.paint(g, mainPanel);
        frameS.paint(g, mainPanel);
        frameG.paint(g, mainPanel);
        frameS2.paint(g, mainPanel);
        frameG2.paint(g, mainPanel);
        hint.paint(g, mainPanel);
        num1.paint(g, mainPanel);
        num2.paint(g, mainPanel);
        num3.paint(g, mainPanel);
        num4.paint(g, mainPanel);
        floorRunning.paint(g, mainPanel);
        DanceTest.paint(g, mainPanel);
        floorSpring.paint(g, mainPanel);
        floorFlashing.paint(g, mainPanel);
        player1.paint(g, mainPanel);
        player2.paint(g, mainPanel);
        player3.paint(g, mainPanel);
        player4.paint(g, mainPanel);
        menu.paint(g, mainPanel);
        next.paint(g, mainPanel);
        
        //閃光開始
        if(FlashTrap.getFlashState()){
            flashDelayCount++;
        }//閃光持續
        if(flashDelayCount <15 && flashDelayCount >0){
           FlashTrap.getFlash().setCounter(flashDelayCount -1);
           //System.out.println("**"+flashDelayCount);
           FlashTrap.getFlash().paint(g, mainPanel);
        }//閃光結束
        else if(flashDelayCount >=15){
            FlashTrap.setFlashState(false);
            flashDelayCount = 0;
        }
    }
}
