package frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class GameFrame{
    public static final int W = 5, H = 7;
    public static final int FRAME_WIDTH = 500;
    public static final int FRAME_HEIGHT = 700;

    public static void main(String[] args) throws IOException {
        final JFrame mainFrame = new JFrame();
        MainPanel mp = new MainPanel();
        mainFrame.setTitle("Gourmet Hunter");
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(mp);

        System.out.println(mainFrame.getContentPane().getWidth() + " " + mainFrame.getContentPane().getHeight());
        mainFrame.setVisible(true);
        System.out.println(mainFrame.getContentPane().getWidth() + " " + mainFrame.getContentPane().getHeight());

        Timer t1 = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Rectangle b = mainFrame.getBounds();
                mainFrame.setBounds(b.x, b.y, b.width, b.width*H/W);
                mainFrame.repaint();
            }
        });
        t1.start();
    }
}
