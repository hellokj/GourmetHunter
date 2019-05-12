package util;

import java.awt.*;

public class TypingMachine {
    private int delayCount;
    private int index; // 印到第幾個字
    private String[] messages; // 紀錄每句現在的狀態
    private int count; // 紀錄第幾句

    public TypingMachine(int msgLength) {
        this.messages = new String[msgLength];
        for(int i = 0; i < msgLength; i++) {
            this.messages[i] = "";
        }
    }

    public boolean typing(Graphics g, String[] input, int delay, int typingX, int typingY){
        if (++delayCount % delay == 0){
            if (index < input[count].length()){
                this.messages[count] = input[count].substring(0, ++index);
            }else if(count < input.length - 1){
                index = 0;
                count++;
            }
        }
        FontMetrics fm = g.getFontMetrics();
        for(int i = 0; i < count + 1; i++) {
//            g.drawString(messages[i], typingX - msgWidth / 2, typingY - (count - i) * fm.getAscent());
            int msgWidth = fm.stringWidth(this.messages[count]);
            g.drawString(this.messages[i], typingX - msgWidth/2, typingY - (count - i) * 50);
        }
        return count == input.length - 1;
    }
}
