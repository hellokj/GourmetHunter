package frame.scene;

import character.Button;
import character.Floor;
import character.GameObject;
import frame.MainPanel;
import mode.GameMode;
import state.GameState;

import java.awt.*;

public abstract class GameScene extends Scene{
    public GameScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
    }

    public abstract void setGame(GameMode gameMode);
    public abstract void setGameState(GameState state);
    public abstract void setGameObject();

//    // 找到最後一塊
//    private Floor findLast(){
//        return floors.get(floors.size() - 1);
//    }
//
//    // 重新開始遊戲
//    private void reset(){
//        gsChangeListener.changeScene(MainPanel.TWO_PLAYER_GAME_SCENE);
//    }
//
//    // 跳出選單
//    private void menu(){
//        isCalled = true;
//        if (button_resume == null){
//            button_resume = new Button(175, 150, 150, 100, 150, 100, "button/Button_Resume.png");
//        }
//        if (button_new_game == null){
//            button_new_game = new Button(175, 300, 150, 100, 150, 100,"button/Button_NewGame.png");
//        }
//        if (button_menu == null){
//            button_menu = new Button(175, 450, 150, 100, 150, 100, "button/Button_Menu.png");
//        }
//        if (cursor == null){
//            cursor = new GameObject(100, 150 + 25, 50, 50, 168, 140, "background/Cursor.png");
//        }
//    }
//
//    private void pause(){
//        isPause = true;
//    }
//
//    private void resume(){
//        isPause = false;
//    }
//
//    private Button checkCursorPosition(){
//        Point cursorCenterPoint = cursor.getCenterPoint();
//        if (cursorCenterPoint.y < button_resume.getModY() + button_resume.getDrawHeight()*MainPanel.RATIO && cursorCenterPoint.y > button_resume.getModY()){
//            return button_resume;
//        }
//        if (cursorCenterPoint.y < button_new_game.getModY() + button_new_game.getDrawHeight()*MainPanel.RATIO && cursorCenterPoint.y > button_new_game.getModY()){
//            return button_new_game;
//        }
//        if (cursorCenterPoint.y < button_menu.getModY() + button_menu.getDrawHeight()*MainPanel.RATIO && cursorCenterPoint.y > button_menu.getModY()){
//            return button_menu;
//        }
//        return null;
//    }
}
