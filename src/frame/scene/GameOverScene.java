package frame.scene;

import character.GameObject;
import frame.GameFrame;
import frame.MainPanel;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameOverScene extends Scene {
    private GameObject background;
    private int key;

    public GameOverScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        background = new GameObject(0, 0, GameFrame.FRAME_WIDTH, GameFrame.FRAME_HEIGHT, "background/GameOver.png");
    }

    @Override
    public KeyListener genKeyListener() {
        return new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e){
                key = e.getKeyCode();
                if (key == KeyEvent.VK_R){
                    gsChangeListener.changeScene(MainPanel.STORY_GAME_SCENE);
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
    }
}
