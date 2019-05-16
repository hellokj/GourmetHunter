package frame.scene;

import character.*;
import character.Button;
import character.trap.TrapGenerator;
import frame.MainPanel;
import mode.GameMode;
import state.GameState;

import java.awt.*;
import java.util.ArrayList;

public abstract class GameScene extends Scene{
    public GameObject background_0, background_1, roof;
    public boolean isCalled, isPause;
    public Button button_resume, button_new_game, button_menu;
    public GameObject cursor;
    public ArrayList<Floor> floors;
    public FloorGenerator fg;
    public TrapGenerator tg;

    public GameScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        setGameObject();
    }

//    public abstract void setGame(GameMode gameMode);
//    public abstract void setGameState(GameState state);
    public abstract void setGameObject();

    // 更新背景圖
    protected void updateBackgroundImage(){
        if (background_0.getModY() + background_0.getDrawHeight() * MainPanel.RATIO <= 10){
//            background_0 = new GameObject(0, 700, 500, 700, 1417, 1984,"background/EgyptBackground_0.png");
            background_0.setY(background_1.getY() + background_1.getDrawHeight());
        }
        if (background_1.getModY() + background_1.getDrawHeight() * MainPanel.RATIO <= 10){
//            background_1 = new GameObject(0, 700, 500, 700, 1417, 1984,"background/EgyptBackground_0.png");
            background_1.setY(background_0.getY() + background_0.getDrawHeight());
        }
        background_0.setY(background_0.getY() - 5);
        background_1.setY(background_1.getY() - 5);
    }

    // 重新開始遊戲
    protected void reset(){
//        gsChangeListener.changeScene();
    }

    // 跳出選單
    protected void menu(){
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

    protected void pause(){
        isPause = true;
    }

    protected void resume(){
        isPause = false;
    }

    protected Button checkCursorPosition(){
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
}
