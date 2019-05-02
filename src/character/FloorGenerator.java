package character;

import character.trap.Trap;
import character.trap.TrapGenerator;
import frame.GameFrame;

public class FloorGenerator {

    public FloorGenerator(){

    }

    public Floor genFloor(Floor last){
        Trap trap = TrapGenerator.getInstance().genTrap();
        Floor floor = new Floor(getRandom(0, GameFrame.FRAME_WIDTH - 64), last.bottom + getRandom(30, 50), trap);
        return floor;
    }

    private int getRandom(int min, int max){
        return ((int)(Math.random()*(max - min)) + min);
    }
}
