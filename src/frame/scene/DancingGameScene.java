package frame.scene;

import character.*;
import character.Button;
import character.trap.TrapGenerator;
import frame.MainPanel;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class DancingGameScene extends Scene {
    private GameObject background, roof, endingFloor;
    private AnimationGameObject endingGate;
    private Actor player;
    private ArrayList<Floor> floors;
    private Floor floor;
    private FloorGenerator fg;
    private TrapGenerator tg;

    // 選單相關
    private boolean isCalled;
    private boolean isPause;
    private GameObject cursor; // 光標
    private Button chooser;
    private Button button_resume, button_menu, button_new_game; // 三個按鈕

    // 遊戲狀態
//    private int key; // 鍵盤輸入值
    private int fallingDelayCount, fallingDelay = 30, fallingAmount = 5; // 天花板掉落計數器

    // 人物操控
    private boolean up = false, down = false, left = false, right = false;

    public DancingGameScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        // 設定關卡難易度
        setLevel((int)(Math.random()*4)+1);
        // 場景物件
        setSceneObject();
        // 遊戲狀態
        setGameState();
    }

    private void setGameState() {
        isCalled = false;
        isPause = false;
    }

    private void setSceneObject() {
        background = new GameObject(0, 0, 500, 700, 500, 700,"background/circus1.png");
        roof = new GameObject(0, -700, 500, 700, 500, 700,"background/BonusRoof.png");
        endingFloor = new GameObject(0, 700 - 32, 500, 32, 500, 32,"floor/EndingFloor.png");
        endingGate = new AnimationGameObject(300, endingFloor.getTop() - 64, 64, 64, 64, 64, "background/Door.png");
        // 生成初始階梯
        fg = new FloorGenerator();
        tg = new TrapGenerator();
        floors = new ArrayList<>();
        floor = new Floor(250 - 32, 150 + 32, tg.genSpecificTrap(TrapGenerator.TRAP_DANCING));
        floor.setSpeedY(0);
        floors.add(floor);
        for (int i = 0; i < 14; i++) {
            floors.add(fg.genDancingFloor(floors.get(i)));
        }
        player = new Actor(250 - 16, 150, 32, 32, 32, 32,MainPanel.P1);
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
//                        reset();
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
                        break;
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
    public void logicEvent() throws IOException {
        if (!player.isDie()){
            if (!isPause){
                MainPanel.checkLeftRightBoundary(player);
                friction(player);
                if (++fallingDelayCount % fallingDelay == 0){
                    roof.setY(roof.getY() + fallingAmount);
                }
                player.stay();
                changeDirection();

                if (player.checkOnObject(endingFloor)){
                    player.setSpeedY(0);
                }

                if (checkTopBoundary(player)){
                    player.die();
                }

                player.update();
                for (Floor floor : floors) {
                    player.checkOnFloor(floor, this);
                    floor.stay();
                    floor.update();
                    if (floor.isCompleted()){

                        floor.setX(-64);
                        floor.setY(0);
                    }
                }

                if ((right || left) && !player.isStop() && player.checkOnObject(endingFloor)){
                    player.acceleration();
                }

                if (endingGate.checkCollision(player)){
                    endingGate.playAnimation();
                    if (key == KeyEvent.VK_UP){
                        gsChangeListener.changeScene(MainPanel.MENU_SCENE);
                    }
                }
            }
        }else {
            gsChangeListener.changeScene(MainPanel.GAME_OVER_SCENE);
        }

    }

    @Override
    public void paint(Graphics g, MainPanel mainPanel) {
        Graphics2D g2d = (Graphics2D)g;
        background.paint(g2d, mainPanel);
        roof.paint(g2d, mainPanel);
        endingFloor.paint(g2d, mainPanel);
        endingGate.paint(g2d, mainPanel);

        for (Floor floor : floors) {
            floor.paint(g2d, mainPanel);
        }

        player.paint(g2d, mainPanel);

        // 印出選單
        if (isCalled){
            button_menu.paint(g, mainPanel);
            button_resume.paint(g, mainPanel);
            button_new_game.paint(g, mainPanel);
            cursor.paint(g, mainPanel);
        }
    }

    // 重新開始遊戲
    private void reset(){
        gsChangeListener.changeScene(MainPanel.DANCING_GAME_SCENE);
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
        if (cursorCenterPoint.y < button_resume.getModY() + button_resume.getDrawHeight()*MainPanel.RATIO && cursorCenterPoint.y > button_resume.getModY()){
            return button_resume;
        }
        if (cursorCenterPoint.y < button_new_game.getModY() + button_new_game.getDrawHeight()*MainPanel.RATIO && cursorCenterPoint.y > button_new_game.getModY()){
            return button_new_game;
        }
        if (cursorCenterPoint.y < button_menu.getModY() + button_menu.getDrawHeight()*MainPanel.RATIO && cursorCenterPoint.y > button_menu.getModY()){
            return button_menu;
        }
        return null;
    }

    // 比天花板高就消失
    private boolean checkTopBoundary(GameObject gameObject){
        return gameObject.getTop() <= this.roof.getModY() + this.roof.getDrawHeight()*MainPanel.RATIO;
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

    // 設定下落延遲、下落程度
    private void setLevel(int level){
        this.fallingDelayCount = 0;
        switch (level){
            case 1:
                this.fallingDelay = 30;
                this.fallingAmount = 4;
                break;
            case 2:
                this.fallingDelay = 30;
                this.fallingAmount = 8;
                break;
            case 3:
                this.fallingDelay = 30;
                this.fallingAmount = 12;
                break;
            case 4:
                this.fallingDelay = 30;
                this.fallingAmount = 16;
                break;
            case 5:
                this.fallingDelay = 20;
                this.fallingAmount = 16;
                break;
        }
    }
}