package frame.scene;

import frame.MainPanel;
import util.ResourcesManager;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LoadingScene extends Scene {
    private static final String INTRODUCTION_FILE_PATH = "introduction.txt";
    private BufferedImage background;
    private int key;
    private static ArrayList<String> messages = loadMsg();
    private boolean isOver;
    private int showMsgDelayCount, showMsgDelay = 20;
    private int index;
    private String msg;
    private String message; // 整篇文章

    public LoadingScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        this.background = ResourcesManager.getInstance().getImage("background/EndBackground.png");
        this.isOver = false;
        this.index = 0;
        this.msg = messages.get(0);
        this.message = "";
    }

    @Override
    public KeyListener genKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                key = e.getKeyCode();
                switch (key){
                    case KeyEvent.VK_ESCAPE:
                        gsChangeListener.changeScene(MainPanel.STORY_GAME_SCENE);
                        break;
                }
                if (isOver){
                    gsChangeListener.changeScene(MainPanel.STORY_GAME_SCENE);
                }
            }
        };
    }

    @Override
    public void paint(Graphics g, MainPanel mainPanel) {
        Font font = MainPanel.CHINESE_FONT.deriveFont(24.0f*MainPanel.ratio);
        g.setFont(font);
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        int msgWidth = fm.stringWidth(String.valueOf(msg));
        int msgAscent = fm.getAscent();
        g.drawImage(background, 0, 0, MainPanel.window.width, MainPanel.window.height, null);
        g.drawString(msg, MainPanel.window.width/2 - msgWidth/2, MainPanel.window.height / 3);
    }

    @Override
    public void logicEvent() {
        showMsg();
    }

    // 更新文字內容
    private void showMsg(){
        if (!isOver){
            if (showMsgDelayCount++ == showMsgDelay){
                msg = messages.get(index);
                index++;
                message += msg;
                System.out.println(msg);
                showMsgDelayCount = 0;
            }
        }
        if (index == messages.size()){
            isOver = true;
        }
    }

    private static ArrayList<String> loadMsg(){
        ArrayList<String> data = new ArrayList<>();
        BufferedReader br;
        do {
            try {
                br = new BufferedReader(new FileReader(INTRODUCTION_FILE_PATH));
                while (br.ready()){
                    data.add(br.readLine());
                }
                break;
            }catch (IOException e){
                System.out.println("can't find the intro file.");
                continue;
            }
        }while (true);
        return data;
    }
}
