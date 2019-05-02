package character.trap;

import character.Actor;
import character.Floor;

public interface Trap {
    void setFloorState(Floor floor);
    void execute(Actor player, Floor floor);
}
