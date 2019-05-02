package character.trap;

import character.Actor;
import character.Floor;

public class FragmentTrap implements Trap {
    public static final int generationRate = 30;

    @Override
    public void setFloorState(Floor floor) {

    }

    @Override
    public void execute(Actor player, Floor floor) {
        // 判定角色狀態or站立時間
        // 判定碰觸到的是哪塊
        // 從那塊開始掉落->再來是那塊的兩側->最後一塊掉落
    }
}
