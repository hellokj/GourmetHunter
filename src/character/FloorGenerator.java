package character;

import character.trap.Trap;
import character.trap.TrapGenerator;
import frame.GameFrame;

public class FloorGenerator {

    private static FloorGenerator floorGenerator;

    public static FloorGenerator getInstance(){
        if (floorGenerator == null){
            floorGenerator = new FloorGenerator();
        }
        return floorGenerator;
    }

    // 傳入當前層數，調整生成機率
    public Floor genFloor(Floor last, int layer){
        Trap trap = TrapGenerator.getInstance().genTrap(layer);
        Floor floor = new Floor(getRandom(0, GameFrame.FRAME_WIDTH - 64), last.bottom + getRandom(30, 50), trap);
        return floor;
    }

    private int getRandom(int min, int max){
        return ((int)(Math.random()*(max - min)) + min);
    }
}
