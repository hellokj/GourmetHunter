package frame.scene;

import character.Actor;
import character.Button;
import character.GameObject;
import frame.MainPanel;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LeaderBoardScene extends Scene {
    private static final String LEADER_BOARD_FILE_PATH = "leader_board.txt";
    private GameObject background,paper,road;
    private Actor player;
    private Button buttonBack;
    private int count;
    private String[] numbers, names, scores; // 編號、名稱、飢餓值 (用來比較)
    private ArrayList<String> ranks; // 每列資料
    private int key;
    
    public LeaderBoardScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
        this.background = new GameObject(0,0,500, 700,600, 840, "background/MenuBackground.png");
        this.road = new GameObject(0, 644, 600, 44, 600, 44,"background/Road.png");
        this.paper = new GameObject(250 - 170,50,340,510, 300, 450, "background/Paper.png");
        this.buttonBack = new Button(300,475, 150, 100, 150, 100, "button/Button_Back.png");
        this.player = new Actor(250, 622, 32, 32, 32, 32, "actor/Actor1.png");
        this.numbers = new String[5];
        this.names = new String[5];
        this.scores = new String[5];
        this.ranks = new ArrayList<>();
        dealWithData(readLeaderBoard());
    }

    @Override
    public KeyListener genKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                key = e.getKeyCode();
                switch (key){
                    case KeyEvent.VK_RIGHT:
                        player.changeDir(Actor.MOVE_RIGHT);
                        break;
                    case KeyEvent.VK_LEFT:
                        player.changeDir(Actor.MOVE_LEFT);
                        break;
                    case KeyEvent.VK_UP:
                        if (player.canJump()){
                            player.jump();
                        }
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e){
                if (key == e.getKeyCode()){
                    key = -1;
                }
            }
        };
    }
    @Override
    public void logicEvent() {
        MainPanel.checkLeftRightBoundary(player);
        friction(player);
        if (player.checkOnObject(road)){
            player.setCanJump(true); // 將可以跳躍設回true
            player.setSpeedY(0); // 落到地板上，
        }
        player.stay();
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_LEFT){
            player.acceleration();
        }
        player.update();
        // 設定按鈕圖片
        buttonBack.setImageOffsetX(0);
        if(buttonBack.checkCollision(player)){
            buttonBack.setImageOffsetX(1);
            if (count++ == 10){ // 一個延遲後切換場景
                VICTORY.stop();
                gsChangeListener.changeScene(MainPanel.MENU_SCENE);
                count = 0;
            }
        }
    }
    
    @Override
    public void paint(Graphics g, MainPanel mainPanel) {
        background.paint(g, mainPanel);
        road.paint(g, mainPanel);
        paper.paint(g, mainPanel);
        buttonBack.paint(g, mainPanel);

        // 印出排行榜
        Font font = MainPanel.ENGLISH_FONT.deriveFont(30.0f*MainPanel.ratio);
        g.setFont(font);
        g.setColor(Color.darkGray);
        FontMetrics fm = g.getFontMetrics();
        int msgWidth = fm.stringWidth(String.valueOf(ranks.get(0)));
        int msgAscent = fm.getAscent();
//        g.drawString("Rank", (int)(110*MainPanel.ratio), (int)(160*MainPanel.ratio - 75*MainPanel.ratio));
        g.drawString("Player", (int)(150*MainPanel.ratio), (int)(160*MainPanel.ratio - 50*MainPanel.ratio));
        g.drawString("Score", (int)(300*MainPanel.ratio), (int)(160*MainPanel.ratio - 50*MainPanel.ratio));
        for (int i = 0; i < ranks.size(); i++) {
//            g.drawString(ranks.get(i), (int)(100*MainPanel.ratio), (int)(160*MainPanel.ratio + 75*MainPanel.ratio * i));
            g.drawString(numbers[i], (int)(110*MainPanel.ratio), (int)(160*MainPanel.ratio + 75*MainPanel.ratio * i));
            g.drawString(names[i], (int)(150*MainPanel.ratio), (int)(160*MainPanel.ratio + 75*MainPanel.ratio * i));
            g.drawString(scores[i], (int)(300*MainPanel.ratio), (int)(160*MainPanel.ratio + 75*MainPanel.ratio * i));
        }

        player.paint(g, mainPanel);
    }

    private ArrayList<String> readLeaderBoard(){
        ArrayList<String> data = new ArrayList<>();
        BufferedReader br;
        do {
            try {
                br = new BufferedReader(new FileReader(LEADER_BOARD_FILE_PATH));
                while (br.ready()){
                    data.add(br.readLine());
                }
                break;
            }catch (IOException e){
                System.out.println("can't find the file.");
                continue;
            }
        }while (true);
        return data;
    }

    private void dealWithData(ArrayList<String> data){
        for (int i = 0; i < data.size(); i++) {
            String[] eachRow = data.get(i).split(",");
            numbers[i] = String.valueOf(i+1);
            names[i] = eachRow[0];
            scores[i] = eachRow[1];
        }
        for (int i = 0; i < data.size(); i++) {
            String result = numbers[i] + "    " + names[i] + "    " + scores[i];
            ranks.add(result);
        }
    }
}
