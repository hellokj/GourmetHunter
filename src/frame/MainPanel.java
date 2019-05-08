package frame;

import character.Actor;
import character.GameObject;
import frame.scene.*;
import util.PainterManager;
import util.ResourcesManager;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainPanel extends javax.swing.JPanel {
    public static final int MENU_SCENE = 0;
    public static final int LOADING_SCENE = 1;
    public static final int MODE_SCENE = 2;
    public static final int STORY_GAME_SCENE = 3;
    public static final int TWO_PLAYER_GAME_SCENE = 4;
    public static final int INFINITY_GAME_SCENE = 5;
    public static final int END_SCENE = 6;
    public static final int GAME_OVER_SCENE = 7;
    public static final int LEADER_BOARD_SCENE = 8;

    // 載入遊戲中字體
    public static final Font ENGLISH_FONT = ResourcesManager.getInstance().getFont("Britannic Bold Regular.ttf");
    public static final Font CHINESE_FONT = ResourcesManager.getInstance().getFont("setofont.ttf");

    // 載入排行榜資訊
    public static final String LEADER_BOARD_FILE_PATH = "leader_board.txt";
    public static String[] leaderBoard;

    // 調整螢幕大小
    public static Dimension window;
    public static float ratio;

    public static Actor
            player1 = new Actor(250, 700, 32, 32, 32, 32, "actor/Actor1.png"),
            player2 = new Actor(250, 700, 32, 32, 32, 32, "actor/Actor1.png");

    // fade in / fade out
    private float alpha;
    private int fadeDelayCount;
    private boolean isFadedOut;


    public interface GameStatusChangeListener{
        void changeScene(int sceneId);
    }

    // 現在場景
    private KeyListener kl;
    private MouseListener ml;
    private Scene currentScene;
    private GameStatusChangeListener gsChangeListener;

    public MainPanel() throws IOException {
        // 作為調整大小的基準
        setPreferredSize(new Dimension(500, 700));
        // 透明度調整
        alpha = 0f;
        isFadedOut = false;
        // 讀取排行
        if (leaderBoard == null){
            leaderBoard = readLeaderBoard(LEADER_BOARD_FILE_PATH);
        }
        gsChangeListener = new GameStatusChangeListener() {
            @Override
            public void changeScene(int sceneId) {
                changeCurrentScene(genSceneById(sceneId));
            }
        };
        // 更改初始場景
        changeCurrentScene(genSceneById(MainPanel.MENU_SCENE));

        // delay 25 ms
        Timer t1 = new Timer(25, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 每次更新此時視窗大小資訊
                window = MainPanel.this.getSize();
                ratio =  ((float)window.getSize().width / (float)getPreferredSize().width);

//                if (++fadeDelayCount % 40 == 0){
//                    fadeDelayCount = 0;
//                    alphaStep();
//                }
                if (currentScene != null){
                    try {
                        currentScene.logicEvent();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        t1.start();
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = PainterManager.g2d(g);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        currentScene.paint(g, this);
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
            case MODE_SCENE:
                return new ModeScene(gsChangeListener);
            case STORY_GAME_SCENE:
                return new StoryGameScene(gsChangeListener);
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
        isFadedOut = false;
        return null;
    }

    private String[] readLeaderBoard(String path) throws IOException {
        String[] data = new String[5];
        BufferedReader br = new BufferedReader(new FileReader(path));
        int i = 0;
        while (br.ready() && i < 5){
            data[i] = br.readLine();
            i++;
        }
        br.close();
        return data;
    }

    private void alphaStep(){
        alpha += 0.1f;
        if (alpha >= 1){
            alpha = 1;
            isFadedOut = true;
        }
    }
}
