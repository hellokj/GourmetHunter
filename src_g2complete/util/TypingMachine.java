package util;

import org.omg.PortableInterceptor.INACTIVE;

import java.awt.*;
import java.util.ArrayList;

public class TypingMachine {
    private ArrayList<String> data;
    private int delayCount;
    private int index;
    private String[] msg;
    private ArrayList<TypingMachine> typingMachines;
    private int machineSerial;
    private boolean[] isOverFlags;

    private int count;

    public TypingMachine(){
        reset();
    }

    public TypingMachine(ArrayList<String> data){
        this.data = data;
        this.typingMachines = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            typingMachines.add(new TypingMachine());
        }
        this.isOverFlags = new boolean[typingMachines.size()];
    }
    public TypingMachine(int msgLength) {
        this.msg = new String[msgLength];
        for(int i = 0; i < msgLength; i++) {
            this.msg[i] = "";
        }
    }

    // 第一台打完換第二台，持續直到每台打完
    public void type(Graphics g, int delay, ArrayList<Integer> typingXs, ArrayList<Integer> typingYs){
//        int machineSerial = 0;
//        for (int i = 0; i < typingMachines.size(); i++) {
//            while (!typingMachines.get(i).typing(g, data.get(i), delay, typingXs.get(i), typingYs.get(i))){
//                current = typingMachines.get(machineSerial);
//                continue;
//            }
//            isOverFlags[machineSerial] = true;
//        }
//        for (int i = 0; i < isOverFlags.length; i++) {
//            System.out.println(isOverFlags[i]);
//            if (isOverFlags[i]){
//                g.drawString(data.get(i), typingXs.get(i), typingYs.get(i));
//            }
//        }
    }

    public boolean typing(Graphics g, String[] string, int delay, int typingX, int typingY){
        if (++delayCount % delay == 0){
            if (index < string[count].length()){
                msg[count] = string[count].substring(0, ++index);
            }else if(count < string.length - 1){
                index = 0;
                count++;
            }
        }
        FontMetrics fm = g.getFontMetrics();
        for(int i = 0; i < count + 1; i++) {
//            g.drawString(msg[i], typingX - msgWidth / 2, typingY - (count - i) * fm.getAscent());
            int msgWidth = fm.stringWidth(msg[count]);
            g.drawString(msg[i], typingX - msgWidth / 2, typingY - (count - i) * 25);
        }
        return false;
    }

    public void reset(){
        delayCount = 0;
        index = 0;
        machineSerial = 0;
//        msg = "";
    }
}
