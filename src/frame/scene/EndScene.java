package frame.scene;

import character.Button;
import character.GameObject;
import frame.MainPanel;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class EndScene extends Scene {
    private GameObject background;
    private GameObject light1;
    private GameObject light2;
    private GameObject food;
    private GameObject foodTxt;
    
    private GameObject player;

    private Button buttonMenu;
    int count;

    private int lightCount;
    private int key;

    public EndScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        this.lightCount = 0;
        ArrayList<String> foods = new ArrayList<>();
        ArrayList<String> foodTxts = new ArrayList<>();
        int foodKind = 9;
        for(int i = 0; i< foodKind; i++){
            foods.add("food/food"+(i+1)+".png");
        }
        for(int i = 0; i< foodKind; i++){
            foodTxts.add("food/foodTxt"+(i+1)+".png");
        }
        int r = (int)(Math.random()* foodKind);
        this.background = new GameObject(0,0,500,700,"background/EndBackground.png");
        this.light1 = new GameObject(55,105,375,375,"background/light1.png");
        this.light2 = new GameObject(55,105,375,375,"background/light2.png");
        this.food = new GameObject(150,200,200,200, foods.get(r));
        this.foodTxt = new GameObject(100,70,300,100,foodTxts.get(r));
        this.player = new GameObject(-200,450,175,200,"actor/ActorBack.png");
        this.buttonMenu = new Button(300, 525, 150, 100,150, 100, "button/Button_Menu_R.png");
        
    }

    @Override
    public KeyListener genKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                key = e.getKeyCode();
                switch (key){
                    case KeyEvent.VK_RIGHT:
                        buttonMenu.setImageOffsetX(1);
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e){
                buttonMenu.setImageOffsetX(0);
                if (e.getKeyCode() == KeyEvent.VK_RIGHT){
                    backToMenu();
                }
            }
        };
    }

    @Override
    public void logicEvent() {

    }

    @Override
    public void paint(Graphics g) {
        background.paint(g);
        buttonMenu.paint(g);
        light1.paint(g);
        light2.paint(g);
        lightCount++;
        if(lightCount<6){
            light2.setImageOffsetX(1);
            light1.setImageOffsetX(0);
        }
        else if(lightCount>=6){
            light2.setImageOffsetX(0);
            light1.setImageOffsetX(1);
            if(lightCount==12){
                lightCount = 0;
            }
        }
        food.paint(g);
        foodTxt.paint(g);
        player.paint(g);
        player.setX(player.getX()+8);
        if(player.getX()>0){
            player.setX(0);
        }
    }

    private void backToMenu(){
        gsChangeListener.changeScene(MainPanel.MENU_SCENE);
    }
}
