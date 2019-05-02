package frame;

import character.GameObject;
import frame.scene.*;
import util.TextManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

public class MainPanel extends javax.swing.JPanel {
    public static final int MENU_SCENE = 0;
    public static final int LOADING_SCENE = 1;
    public static final int GAME_SCENE = 2;
    public static final int TWO_PLAYER_GAME_SCENE = 3;
    public static final int INFINITY_GAME_SCENE = 4;
    public static final int END_SCENE = 5;
    public static final int GAME_OVER_SCENE = 6;
    public static final int LEADER_BOARD_SCENE = 7;

    public interface GameStatusChangeListener{
        void changeScene(int sceneId);
    }

    // 現在場景
    private KeyListener kl;
    private MouseListener ml;
    private Scene currentScene;
    private GameStatusChangeListener gsChangeListener;

    public MainPanel(){
        gsChangeListener = new GameStatusChangeListener() {
            @Override
            public void changeScene(int sceneId) {
                changeCurrentScene(genSceneById(sceneId));
            }
        };

        // 更改初始場景
        changeCurrentScene(genSceneById(INFINITY_GAME_SCENE));

        // delay 25 ms
        Timer t1 = new Timer(25, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentScene != null){
                    currentScene.logicEvent();
                }
            }
        });
        t1.start();
    }

    @Override
    public void paintComponent(Graphics g){
        currentScene.paint(g);
    }

    private void changeCurrentScene(Scene scene) {
        if (scene == null){
            return;
        }
        this.removeKeyListener(kl);
        currentScene = scene;
        kl = scene.genKeyListener();
        this.addKeyListener(kl);
        this.setFocusable(true);
    }

    // 由場景來設定邊界判定
    public static void checkLeftRightBoundary(GameObject gameObject){
        if (gameObject.getLeft() > GameFrame.FRAME_WIDTH){
            gameObject.setX(0);
        }
        if (gameObject.getRight() < 0){
            gameObject.setX(GameFrame.FRAME_WIDTH - gameObject.getDrawWidth());
        }
    }

    private frame.scene.Scene genSceneById(int id) {
        switch (id){
            case MENU_SCENE:
                return new MenuScene(gsChangeListener);
            case LEADER_BOARD_SCENE:
                return new LeaderBoardScene(gsChangeListener);
            case GAME_SCENE:
                return new GameScene(gsChangeListener);
            case TWO_PLAYER_GAME_SCENE:
                return new TwoPlayerGameScene(gsChangeListener);
            case INFINITY_GAME_SCENE:
                return new InfinityGameScene(gsChangeListener);
            case LOADING_SCENE:
                return new LoadingScene(gsChangeListener);
            case END_SCENE:
                return new EndScene(gsChangeListener);
            case GAME_OVER_SCENE:
                return new GameOverScene(gsChangeListener);
        }
        return null;
    }

}
