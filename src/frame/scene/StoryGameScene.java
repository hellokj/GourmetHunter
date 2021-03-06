package frame.scene;

import character.*;
import character.Button;
import character.food.Food;
import character.trap.FlashTrap;
import character.trap.TrapGenerator;
import frame.MainPanel;
import util.ResourcesManager;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public class StoryGameScene extends GameScene {
    private GameObject background_end, roof;
    private GameObject hungerCount, hungerBack;
    private int hungerValue;
    private GameObject endingFloor;
    private AnimationGameObject endingGate;
    private Actor player;
    private ArrayList<Floor> floors;
    private FloorGenerator fg;
    private TrapGenerator tg;

    // 選單相關
    private boolean isCalled;
    private boolean isPause;
    private GameObject cursor; // 光標
    private Button chooser;
    private Button button_resume, button_menu, button_new_game; // 三個按鈕

    // 顯示板
    private GameObject hungerLabel, timeLabel;

    private int time;
    private int minute, second; // 印出時間
    private String colon; // 冒號
    private int timeCount; // 時間刷新delay

    // 印出文字相關
    private boolean showLayer, showHeal;
    private int msgWidth, msgAscent;
    private FontMetrics fm;
    private int healDrawingCount; // 文字顯示時間
    private Food eatenFood;

    private boolean isOver; // 結束

//    private int key; // 鍵盤輸入值
    private int count; // 死亡跳起計數器

    private int flashCount; //閃光延遲

    // 音效
    private AudioClip bgm;

    private boolean up = false, down = false, left = false, right = false;

    public StoryGameScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        // 場景物件
        setGameObject();
        // 場景狀態
        isOver = false;
        isCalled = false;
        isPause = false;
    }

    @Override
    public void setGameObject() {
        bgm = ResourcesManager.getInstance().getSound("sound/StoryMode.au");
        bgm.loop();
        fg = new FloorGenerator();
        tg = new TrapGenerator();
        background_0 = new GameObject(0, 0, 500, 700, 1417, 1984,"background/EgyptBackground_0.png");
        background_1 = new GameObject(0, 700, 500, 700, 1417, 1984,"background/EgyptBackground_0.png");
        background_end = new GameObject(0, 678, 500, 700, 1417, 1984, "background/EgyptBackground_1.png");
        roof = new GameObject(0, 0, 500, 64, 500, 64,"background/Roof.png");
        endingFloor = new GameObject(0, 800, 500, 32, 500, 32,"floor/EndingFloor.png");
        endingGate = new AnimationGameObject(300, endingFloor.getTop() - 64, 64, 64, 64, 64, "background/Door.png");
        player = new Actor(250, 200, 32, 32, 32, 32, "actor/Actor1.png");
        // 顯示板
        hungerLabel = new GameObject(28,8, 64, 32,64, 32, "background/HungerLabel.png");
        timeLabel = new GameObject(300 ,8,64, 32,64, 32, "background/TimeLabel.png");
        // 飢餓值
        hungerBack = new GameObject(96, 16, 100, 16, 5, 5, "background/Hunger.png");
        hungerCount = new GameObject(96, 16, 0, 16,5, 5, "background/HungerCount.png");
        // 時間相關
        time = 60;
        colon = " : ";
        // 初始10塊階梯
        floors = new ArrayList<>();
        floors.add(new Floor(player.getX() - (64 - 32), 200 + 32, tg.genSpecificTrap(TrapGenerator.TRAP_NORMAL))); // 初始站立
        for (int i = 0; i < 14; i++) {
            floors.add(fg.genFloor(floors, floors.get(i), 10));
        }
    }

    @Override
    public KeyListener genKeyListener() {

        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                switch (e.getKeyCode()){
                    // p1 controller
                    case KeyEvent.VK_RIGHT:
                        if (!isPause){
                            right = true;
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (!isPause){
                            left = true;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (!isPause){
                            up = true;
                        }else {
                            if (!(cursor.getY() - 150 < button_resume.getY())){
                                cursor.setY(cursor.getY() - 150);
                            }
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (!isPause){
                            down = true;
                        }else {
                            if (!(cursor.getY() + 150 > button_menu.getY() + button_menu.getDrawHeight())){
                                cursor.setY(cursor.getY() + 150);
                            }
                        }
                        break;
                    case KeyEvent.VK_R:
                        player.reset();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        if (isPause){
                            resume();
                            isCalled = false;
                        }else {
                            pause();
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
                key = e.getKeyCode();
            }

            @Override
            public  void keyReleased(KeyEvent e){
                if (!isPause){
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
                            bgm.stop();
                            gsChangeListener.changeScene(MainPanel.MENU_SCENE);
                        }
                        isCalled = false;
                    }
                }
                key = -1;
            }
        };
    }

    @Override
    public void logicEvent() {
        if (!isPause){
            if (!player.isDie()){ // 還沒死亡的狀態
                if (isDark){
                    if (darkDelay++ == 120){
                        isDark = false;
                        darkDelay = 0;
                    }
                }
                MainPanel.checkLeftRightBoundary(player);
                changeDirection();
                int floorAmount = checkSceneFloorAmount();
                hungerValue = player.getHunger();
                if (floorAmount < 15 && time > 6 && floors.size() < 20){
                    for (int i = 0; i < 15 - floorAmount; i++) {
                        floors.add(fg.genFloor(floors, findLast(), 10));
                    }
                }
                // 逆向摩擦力
                friction(player);
                if ((right || left) && !player.isStop()){
                    player.acceleration();
                }
                if (checkTopBoundary(player)){
                    player.touchRoof();
                }else {
                    player.stay();
                }
                for (int i = 0; i < floors.size(); i++) {
                    player.checkOnFloor(floors.get(i), this);
                    // 吃食物機制
                    if (player.eat(floors.get(i).getFood())){
                        eatenFood = floors.get(i).getFood();
                        if (eatenFood != null){
                            showHeal = true;
                        }
                        floors.get(i).setFood(null); // 吃完，食物設回null
                    }
                    floors.get(i).stay();
                    if (checkTopBoundary(floors.get(i))){
                        floors.remove(i);
                        break;
                    }
                }
                // 人物飢餓
                player.hunger();
                // 繪製現在飢餓值
                hungerCount.setDrawWidth(player.getHunger());
                // 每次都要更新此次座標
                for (Floor floor : floors) {
                    floor.update();
                }
                player.update();
                // 掉落死亡 or 餓死後落下
                if (player.getModY() + player.getDrawHeight()*MainPanel.RATIO > MainPanel.CURRENT_WINDOW.getHeight()){
                    player.die();
                }
                // 時間刷新
                updateTime();
                if (time < 5){
                    isOver = true;
                    endingGate.setBoundary();
                }
                if (isOver){
                    if (player.checkOnObject(endingFloor)){
                        player.stay();
                        player.setSpeedY(0);
                    }
                }
                endingFloor.setBoundary();
            }else {
                // 死亡跳起後落下
                if (count++ < 20){
                    player.setSpeedX(0);
                    player.setY(player.getY()-5);
                }else {
                    player.setSpeedX(0);
                    player.update();
                    // 完全落下後切場景
                    if (player.getModY() + player.getDrawHeight()*MainPanel.RATIO > MainPanel.CURRENT_WINDOW.getHeight()){
                        bgm.stop();
                        gsChangeListener.changeScene(MainPanel.GAME_OVER_SCENE);
                    }
                }
            }
            if (background_end.getModY() + background_end.getDrawHeight() * MainPanel.RATIO > MainPanel.CURRENT_WINDOW.getHeight()){
                updateBackgroundImage();
            }else {
                for (int i = 0; i < floors.size(); i++) {
                    floors.get(i).setSpeedY(0);
                }
            }
            if (endingGate != null){
                endingGate.setBoundary();
                if (endingGate.checkCollision(player)){
                    endingGate.playAnimation();
                    if (key == KeyEvent.VK_UP){
                        bgm.stop();
                        gsChangeListener.changeScene(MainPanel.END_SCENE);
                    }
                }
            }
        }

    }

    @Override
    public void paint(Graphics g, MainPanel mainPanel) {
        Graphics2D g2d = (Graphics2D)g;
        g.setColor(Color.BLACK);
        g.fillRect(0,0, 500, 700);
        background_0.paint(g2d, mainPanel);
        background_1.paint(g2d, mainPanel);
        background_end.paint(g2d, mainPanel);

        roof.paint(g2d, mainPanel);
        hungerLabel.paint(g2d, mainPanel);
        timeLabel.paint(g2d, mainPanel);
        hungerBack.paint(g2d, mainPanel);
        hungerCount.paint(g2d, mainPanel);

        for (Floor floor : floors) {
            floor.paint(g2d, mainPanel);
        }

        if (isDark){
            g.drawImage(blanket, (int) ((player.getX() + 16 - 575)*MainPanel.RATIO), (int) ((player.getY() + 16 - 775)*MainPanel.RATIO), (int) ((player.getX() + 16 - 575 + 1150)*MainPanel.RATIO), (int) ((player.getY() + 16 - 775 + 1550)*MainPanel.RATIO),0,0, 1150, 1550, null);
        }
        if (isOver){
            endingFloor.paint(g2d, mainPanel);
            endingGate.paint(g2d, mainPanel);
        }

        player.paint(g, mainPanel);

        // 印出吃到食物的回覆值
        g2d.setFont(MainPanel.ENGLISH_FONT.deriveFont(15.0f*MainPanel.RATIO));
        g2d.setColor(Color.GREEN);
        String healMsg = "";
        if (showHeal){
            if (++healDrawingCount <= 50){
                healMsg = "+ "+ eatenFood.getHeal();
            }else {
                showHeal = false;
                healDrawingCount = 0;
            }
        }
        FontMetrics fm = g2d.getFontMetrics();
        int msgWidth = fm.stringWidth(healMsg);
        int msgAscent = fm.getAscent();
        g2d.drawString(healMsg, player.getModX() - (msgWidth*MainPanel.RATIO - player.getDrawWidth()*MainPanel.RATIO)/ 2, player.getModY());

        if (isCalled){
            button_menu.paint(g2d, mainPanel);
            button_resume.paint(g2d, mainPanel);
            button_new_game.paint(g2d, mainPanel);
            cursor.paint(g2d, mainPanel);
        }

        //閃光開始
        if(FlashTrap.getFlashState()){
            flashCount++;
        }//閃光持續
        if(flashCount < 15 && flashCount > 0){
            FlashTrap.getFlash().setCounter(flashCount - 1);
            //System.out.println("**"+flashCount);
            FlashTrap.getFlash().paint(g2d, mainPanel);
        }//閃光結束
        else if(flashCount >= 15){
            FlashTrap.setFlashState(false);
            flashCount = 0;
        }

        // 印出時間
        Font font = g2d.getFont().deriveFont(16.0f * MainPanel.RATIO);
        g2d.setFont(font);
        g2d.setColor(Color.RED);
        fm = g.getFontMetrics();
        msgWidth = fm.stringWidth(String.valueOf(minute));
        msgAscent = fm.getAscent();
        g2d.drawString(String.valueOf(minute), (int) (384*MainPanel.RATIO), (int) (30*MainPanel.RATIO));
        g2d.drawString(colon, (int) (384*MainPanel.RATIO + msgWidth*MainPanel.RATIO), (int) (30*MainPanel.RATIO));
        g2d.drawString(String.valueOf(second), (int) (384*MainPanel.RATIO + 2*msgWidth*MainPanel.RATIO), (int) (30*MainPanel.RATIO));
        g2d.drawString(String.valueOf(hungerValue), (int) (MainPanel.RATIO *(96 + 112 + 10)), (int) (30*MainPanel.RATIO));
    }

    // 比天花板高就消失
    private boolean checkTopBoundary(GameObject gameObject){
        return gameObject.getTop() <= this.roof.getModY() + this.roof.getDrawHeight()*MainPanel.RATIO;
    }

    // 確認畫面中階梯數量
    private int checkSceneFloorAmount(){
        int count = 0;
        for (int i = 0; i < floors.size(); i++) {
            Floor current = floors.get(i);
            if (current.getModY() > 0 && current.getModY() + current.getDrawHeight() * MainPanel.RATIO < MainPanel.CURRENT_WINDOW.height){
                count++;
            }
        }
        return count;
    }

    @Override
    // 更新背景圖
    protected void updateBackgroundImage(){
        super.updateBackgroundImage();
        if (time <= 5){
            background_end.setY(background_end.getY() - 3);
            endingFloor = new GameObject(0, background_end.getY() + 700 + 3 - 32, 500, 32, 500, 32,"floor/EndingFloor.png");
            endingGate = new AnimationGameObject(300, endingFloor.getY() - 64, 64, 64, 64, 64, "background/Door.png");
        }
        background_0.setBoundary();
        background_1.setBoundary();
        background_end.setBoundary();
        if (time <= 1){
            endingFloor.setY(endingFloor.getY() - 3);
            endingGate.setY(endingGate.getY() - 3);
        }
    }

    // 更新時間
    private void updateTime(){
        if (time < 0){
            return;
        }
        minute = time / 60;
        second = time % 60;
        if(++timeCount % 40 == 0){
            time -= 1;
        }
    }

    // 找到最後一塊
    private Floor findLast(){
        return floors.get(floors.size() - 1);
    }

    @Override
    // 重新開始遊戲
    public void reset(){
        gsChangeListener.changeScene(MainPanel.STORY_GAME_SCENE);
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
