package frame.scene;

import character.Actor;
import frame.MainPanel;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

public abstract class Scene {
    public static final float GRAVITY = 0.5f;
    public static final float FRICTION = 0.2f;
    MainPanel.GameStatusChangeListener gsChangeListener;

    public Scene(MainPanel.GameStatusChangeListener gsChangeListener){
        this.gsChangeListener = gsChangeListener;
    }

    abstract public KeyListener genKeyListener();
    abstract public void paint(Graphics g);
    abstract public void logicEvent();

    // 摩擦力
    protected void friction(Actor player){
        if (player.getSpeedX() > 0){
            if (player.getSpeedX() - FRICTION == 0){
                player.setSpeedX(0);
            }else {
                player.setSpeedX(player.getSpeedX() - FRICTION);
            }
        }
        if (player.getSpeedX() < 0){
            if (player.getSpeedX() + FRICTION == 0){
                player.setSpeedX(0);
            }else {
                player.setSpeedX(player.getSpeedX() + FRICTION);
            }
        }
    }
}
