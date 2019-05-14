package frame.scene;

import frame.MainPanel;
//import sun.jvm.hotspot.memory.SystemDictionary;
import util.ResourcesManager;
import util.TypingMachine;

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
    private BufferedImage background;
    private int key;
    private ArrayList<String> messages = loadMsg();
    private boolean isOver;
    private int showMsgDelayCount, showMsgDelay = 20;
    private int index;
    private String msg;
    private String message; // 整篇文章
    private boolean isTyping;

    private TypingMachine tm;

    public LoadingScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        this.background = ResourcesManager.getInstance().getImage("background/EndBackground.png");
        this.isOver = false;
        this.isTyping = true;
        this.index = 0;
        this.msg = messages.get(0);
        this.message = "";
        TYPING.loop();

        tm = new TypingMachine(messages.size());
        for (int i = 0; i < messages.size(); i++) {
            message += (messages.get(i) + "\n");
        }

    }

    @Override
    public KeyListener genKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                key = e.getKeyCode();
                switch (key){
                    case KeyEvent.VK_ESCAPE:
                        TYPING.stop();
                        gsChangeListener.changeScene(MainPanel.STORY_GAME_SCENE);
                        break;
                    case KeyEvent.VK_ENTER:
                        TYPING.stop();
                        gsChangeListener.changeScene(MainPanel.STORY_GAME_SCENE);
                        break;
                }
            }
        };
    }

    @Override
    public void paint(Graphics g, MainPanel mainPanel) {
        Font font = MainPanel.CHINESE_FONT.deriveFont(24.0f*MainPanel.RATIO);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawImage(background, 0, 0, MainPanel.CURRENT_WINDOW.width, MainPanel.CURRENT_WINDOW.height, null);
//        System.out.println(tm.typing(g, message.split("\n"), 2, 200, 650));
        if (tm.typing(g, message.split("\n"), 3, 250, 650)){
            isTyping = false;
            TYPING.stop();
        }
    }

    @Override
    public void logicEvent() {

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

    private ArrayList<String> loadMsg(){
        ArrayList<String> data = new ArrayList<>();
        BufferedReader br;
        do {
            try {
                String introductionFilePath = "introduction.txt";
                br = new BufferedReader(new FileReader(introductionFilePath));
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
