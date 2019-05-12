package frame.scene;

import character.Actor;
import character.GameObject;
import frame.MainPanel;
import util.ResourcesManager;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class Scene {
    public static final float GRAVITY = 0.8f;
    public static final float FRICTION = 0.3f;
    public static boolean isDark = false;
//    public static boolean isCorrect, isWrong; // for dancing mode
    public static final BufferedImage darkness = ResourcesManager.getInstance().getImage("background/Darkness.png");
    public int darkDelay;
    public static final AudioClip HEAL = ResourcesManager.getInstance().getSound("sound/heal.au");
    public static final AudioClip JUMP = ResourcesManager.getInstance().getSound("sound/jump.au");
    public static final AudioClip BUMP = ResourcesManager.getInstance().getSound("sound/Collision.au");
    public static final AudioClip DIE = ResourcesManager.getInstance().getSound("sound/scream.au");
    public static final AudioClip RUN = ResourcesManager.getInstance().getSound("sound/run.au");
    public static final AudioClip FLASH = ResourcesManager.getInstance().getSound("sound/Flash1.au");
    public static final AudioClip CORRECT = ResourcesManager.getInstance().getSound("sound/correct.au");
    public static final AudioClip ERROR = ResourcesManager.getInstance().getSound("sound/error.au");
    public static final AudioClip TYPING = ResourcesManager.getInstance().getSound("sound/typing1.au");
    public static final AudioClip VICTORY = ResourcesManager.getInstance().getSound("sound/Victory2.au");
    public static final AudioClip BUTTON_CLICK = ResourcesManager.getInstance().getSound("sound/ButtonClick.au");
    public static final AudioClip BGM_MENU = ResourcesManager.getInstance().getSound("sound/Menu.au");
    public static final AudioClip BGM_END = ResourcesManager.getInstance().getSound("sound/Victory2.au");
    public static final AudioClip BGM_INFINITY = ResourcesManager.getInstance().getSound("sound/InfinityMode.au");
    public static final AudioClip BGM_STORY = ResourcesManager.getInstance().getSound("sound/StoryMode.au");
    public static final AudioClip BGM_TWO_PLAYER = ResourcesManager.getInstance().getSound("sound/Menu1.au");

    MainPanel.GameStatusChangeListener gsChangeListener;

    public Scene(MainPanel.GameStatusChangeListener gsChangeListener){
        this.gsChangeListener = gsChangeListener;
        isDark = false;
    }

    abstract public KeyListener genKeyListener();
    abstract public void paint(Graphics g, MainPanel mainPanel);
//    abstract public void paint(Graphics2D g2d, MainPanel mainPanel);
    abstract public void logicEvent() throws IOException;

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

    public void setIsDark(boolean state){
        isDark = state;
    }

//    public static boolean isIsCorrect() {
//        return isCorrect;
//    }
//
//    public static void setIsCorrect(boolean isCorrect) {
//        Scene.isCorrect = isCorrect;
//    }
//
//    public static boolean isIsWrong() {
//        return isWrong;
//    }
//
//    public static void setIsWrong(boolean isWrong) {
//        Scene.isWrong = isWrong;
//    }
}
