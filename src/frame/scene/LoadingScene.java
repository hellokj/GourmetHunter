package frame.scene;

import frame.MainPanel;
import sun.jvm.hotspot.memory.SystemDictionary;
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
    private static final String INTRODUCTION_FILE_PATH = "introduction.txt";
    private BufferedImage background;
    private int key;
    private static ArrayList<String> messages = loadMsg();
    private boolean isOver;
    private int showMsgDelayCount, showMsgDelay = 20;
    private int index;
    private String msg;
    private String message; // 整篇文章

    private ArrayList<TypingMachine> typingMachines;
    private ArrayList<Integer> typingWidths, typingHeights;
    private TypingMachine tm;

    public LoadingScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        this.background = ResourcesManager.getInstance().getImage("background/EndBackground.png");
        this.isOver = false;
        this.index = 0;
        this.msg = messages.get(0);
        this.message = "";
        this.typingMachines = new ArrayList<>();
        this.typingWidths = new ArrayList<>();
        this.typingHeights = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            typingMachines.add(new TypingMachine());
            typingWidths.add(250);
            typingHeights.add(100 + 75 * i);
        }

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
        g.drawImage(background, 0, 0, MainPanel.window.width, MainPanel.window.height, null);
//        for (int i = 0; i <= index; i++) {
//            if (typingMachines.get(i).typing(g, messages.get(i), 10, typingWidths.get(i), typingHeights.get(i))){
//                if (index < typingMachines.size() && i == index){
//                    index++;
//                }
//            }
//        }
        tm.typing(g, message.split("\n"), 10, 250, 200);
    }

    @Override
    public void logicEvent() {
//        showMsg();
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
