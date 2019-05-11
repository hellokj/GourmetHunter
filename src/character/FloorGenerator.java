package character;

import character.trap.FragmentTrap;
import character.trap.Trap;
import character.trap.TrapGenerator;
import frame.GameFrame;
import frame.MainPanel;

import java.util.ArrayList;

public class FloorGenerator {

    private static FloorGenerator floorGenerator;

    public static FloorGenerator getInstance(){
        if (floorGenerator == null){
            floorGenerator = new FloorGenerator();
        }
        return floorGenerator;
    }

    // 傳入當前層數，調整生成機率
    public Floor genFloor(ArrayList<Floor> floors, Floor last, int layer){
        Trap trap = TrapGenerator.getInstance().genTrap(layer);
        Floor floor = new Floor(getRandom(0, MainPanel.window.width - 64), last.y + getRandom(50, 70), trap);
        if (layer >= 10){
            floor.setSpeedY((float)(- 1 * (layer / 10)));
        }
        return floor;
    }

    private int getRandom(int min, int max){
        return ((int)(Math.random()*(max - min)) + min);
    }
}
