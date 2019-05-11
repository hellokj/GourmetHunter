/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frame.scene;

import character.Button;
import character.*;
import character.trap.FlashTrap;
import character.trap.TrapGenerator;
import frame.MainPanel;
import util.PainterManager;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;

public class GuideScene_2 extends Scene {
    private GameObject background, questionMark;
    private GameObject hungerCount, hungerBack, hungerLabel;
    private GameObject frameS,frameG,frameS2,frameG2;
    private GameObject hint,num1,num2,num3;
    private Floor floorStone,floorDarkness,floorFlipping;
    private Actor player1,player2,player3;
    private int choose;
    private Button menu,back;

    public GuideScene_2(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        this.background = new GameObject(0,0,500,700,1024, 768,"background/EndBackground.png");
        this.frameS = new GameObject(80,70,150,250, 150, 250,"background/frameS.png");
        this.frameG = new GameObject(250,70,150,250,150, 250,"background/frameG.png");
        this.frameG2 = new GameObject(80,330,150,250,150, 250,"background/frameG.png");
        this.frameS2 = new GameObject(250,330,150,250,150, 250,"background/frameS.png");
        this.questionMark = new GameObject(253+25, 331+25, 100, 163, 554, 908, "background/QuestionMark.png");
        this.hint = new GameObject(10,10,300,50,300, 50,"background/Guide.png");
        this.num1 = new GameObject(90,80,30,30,32, 32,"background/num1.png");
        this.num2 = new GameObject(260,80,30,30,32, 32,"background/num2.png");
        this.num3 = new GameObject(90,340,30,30,32, 32,"background/num3.png");
        floorStone = new Floor(130, 270, TrapGenerator.getInstance().genSpecificTrap(TrapGenerator.TRAP_STONE));
        floorFlipping = new Floor(300, 270, TrapGenerator.getInstance().genSpecificTrap(TrapGenerator.TRAP_FLIPPING));
        floorDarkness = new Floor(130, 530, TrapGenerator.getInstance().genSpecificTrap(TrapGenerator.TRAP_DARKNESS));
        player1 = new Actor(140, 130, 32, 32, 32, 32, "actor/Actor1.png");
        player1.setHunger(30);
        player2 = new Actor(310, 130, 32, 32,32, 32, "actor/Actor1.png");
        player3 = new Actor(140, 390, 32, 32,32, 32, "actor/Actor1.png");
        // 飢餓值
        this.hungerLabel = new GameObject(28,8, 64, 32,64, 32, "background/HungerLabel.png");
        this.hungerBack = new GameObject(120, 92, 80, 16,5, 5, "background/Hunger.png");
        this.hungerCount = new GameObject(120, 92, (int) (player1.getHunger()*0.8f), 16, 5, 5, "background/HungerCount.png");
        back = new Button(0,580,120,70,150,100,"button/Back_L.png");
        menu = new Button(360,580,120,70,150,100,"button/Button_Menu_R.png");
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
                    case KeyEvent.VK_RIGHT:
                        menu.setImageOffsetX(1);
                        break;
                    case KeyEvent.VK_LEFT:
                        back.setImageOffsetX(1);
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e){
                int key = e.getKeyCode();
                if(key==KeyEvent.VK_RIGHT){
                    menu.setImageOffsetX(0);
                    BUTTON_CLICK.play();
                    gsChangeListener.changeScene(MainPanel.MENU_SCENE);
                }
                if(key==KeyEvent.VK_LEFT){
                    back.setImageOffsetX(0);
                    BUTTON_CLICK.play();
                    gsChangeListener.changeScene(MainPanel.GUIDE_SCENE_1);
                }
            }
        };
    }
    private void setChoose(int choose){
        this.choose = choose;
    }
    private void playerStop(Actor player,int x,int y){
        player.setSpeedY(0);
        player.setX(x);
        player.setY(y);
    }

    @Override
    public void logicEvent() {
        if (isDark){
            if (darkDelay++ == 40){
                isDark = false;
                darkDelay = 0;
            }
        }
        floorStone.stay();
        floorDarkness.stay();
        floorFlipping.stay();
        if(choose==2){
            this.outOfFrame(player2, floorFlipping, frameG, 310, 150);
            playerStop(player1, 140, 130);
            playerStop(player3, 140, 390);
        }
        else if(choose==1){
            this.outOfFrame(player1, floorStone, frameS, 140, 150);
            hungerCount.setDrawWidth((int) (player1.getHunger()*0.8f));
            if (hungerCount.getDrawWidth() >= 80){
                hungerCount.setDrawWidth(80);
            }
            playerStop(player2, 310, 130);
            playerStop(player3, 140, 390);
        }
        else if(choose==3){
            this.outOfFrame(player3, floorDarkness, frameG2, 140, 410);
            playerStop(player1, 140, 130);
            playerStop(player2, 310, 130);
        }
    }

    public void outOfFrame(Actor player, Floor floor, GameObject frame, int x, int y){
        if(player.getBottom()>frame.getBottom()||player.getLeft()<frame.getLeft()||
            player.getRight()>frame.getRight()){
            player.setX(x);
            player.setY(y);
            player.setSpeedX(0);
        }
        player.checkOnFloor(floor, this);
        player.stay();
        player.update();
    }
    @Override
    public void paint(Graphics g, MainPanel mainPanel) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setClip(null);
        if (isDark){
            background.paint(g2d, mainPanel);
            floorStone.paint(g2d, mainPanel);
            floorFlipping.paint(g2d, mainPanel);
            floorDarkness.paint(g2d, mainPanel);
            frameS.paint(g2d, mainPanel);
            hungerBack.paint(g, mainPanel);
            hungerCount.paint(g, mainPanel);
            frameG.paint(g2d, mainPanel);
            g2d.setColor(Color.BLACK);
            g2d.fillRect((int) (253 * MainPanel.ratio), (int) (331 * MainPanel.ratio), (int) (147 * MainPanel.ratio), (int) (249 * MainPanel.ratio));
            questionMark.paint(g, mainPanel);
            g2d.drawImage(darkness, (int) (83 * MainPanel.ratio), (int) (331 * MainPanel.ratio), (int) ((81+149) * MainPanel.ratio), (int) ((331+249) * MainPanel.ratio), 0, 0, 1024, 768, null);
            frameG2.paint(g2d, mainPanel);
            frameS2.paint(g2d, mainPanel);
            hint.paint(g2d, mainPanel);
            num1.paint(g2d, mainPanel);
            num2.paint(g2d, mainPanel);
            num3.paint(g2d, mainPanel);
            player1.paint(g2d, mainPanel);
            player2.paint(g2d, mainPanel);
            player3.paint(g2d, mainPanel);
            menu.paint(g2d, mainPanel);
            back.paint(g2d, mainPanel);
            g2d.setClip((new Ellipse2D.Float(player3.getCenterPoint().x - 50 * MainPanel.ratio, player3.getCenterPoint().y - 50 * MainPanel.ratio, 100* MainPanel.ratio, 100* MainPanel.ratio)));
        }
        //
        background.paint(g2d, mainPanel);
        floorStone.paint(g2d, mainPanel);
        floorFlipping.paint(g2d, mainPanel);
        floorDarkness.paint(g2d, mainPanel);
        frameS.paint(g2d, mainPanel);
        hungerBack.paint(g, mainPanel);
        hungerCount.paint(g, mainPanel);
        frameG.paint(g2d, mainPanel);
        g2d.setColor(Color.BLACK);
        g2d.fillRect((int) (253 * MainPanel.ratio), (int) (331 * MainPanel.ratio), (int) (147 * MainPanel.ratio), (int) (249 * MainPanel.ratio));
        questionMark.paint(g, mainPanel);
        frameG2.paint(g2d, mainPanel);
        frameS2.paint(g2d, mainPanel);
        hint.paint(g2d, mainPanel);
        num1.paint(g2d, mainPanel);
        num2.paint(g2d, mainPanel);
        num3.paint(g2d, mainPanel);
        player1.paint(g2d, mainPanel);
        player2.paint(g2d, mainPanel);
        player3.paint(g2d, mainPanel);
        menu.paint(g2d, mainPanel);
        back.paint(g2d, mainPanel);
        //
//        g2d.setClip(new Ellipse2D.Float(player3.getCenterPoint().x - 50, player3.getCenterPoint().y - 50, 100, 100));
//        g2d.draw(g2d.getClip());
//        if (isDark){
//            System.out.println(player3.getCenterPoint().x + "," + player3.getCenterPoint().y);
//            g2d.clip((new Ellipse2D.Float(player3.getCenterPoint().x - 50, player3.getCenterPoint().y - 50, 100, 100)));
//        }
    }
}
