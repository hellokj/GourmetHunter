package character;

import character.trap.Trap;
import character.trap.TrapGenerator;
import frame.MainPanel;

import java.util.ArrayList;

public class FloorGenerator {
    private TrapGenerator trapGenerator;

    public FloorGenerator(){
        trapGenerator = new TrapGenerator();
    }

    // 傳入當前層數，調整生成機率
    public Floor genFloor(ArrayList<Floor> floors, Floor last, int layer){
        Trap trap = trapGenerator.genTrap(layer);
        Floor floor = new Floor(getRandom(0, MainPanel.CURRENT_WINDOW.width - 64), last.y + getRandom(50, 70), trap);
        if (layer >= 10){
            if ((float)(- 1 * (layer / 10)) < -3){
                floor.setSpeedY(-3);
            }else {
                floor.setSpeedY((float)(- 1 * (layer / 10)));
            }
        }
        return floor;
    }

    public Floor genDancingFloor(Floor current){
        Trap trap = trapGenerator.genSpecificTrap(TrapGenerator.TRAP_DANCING);
        Floor floor = new Floor(current.x, current.y + 32, trap);
        floor.setSpeedY(0);
        return floor;
    }

    private int getRandom(int min, int max){
        return ((int)(Math.random()*(max - min)) + min);
    }
}
