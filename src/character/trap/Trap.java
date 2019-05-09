package character.trap;

import character.Actor;
import character.Floor;
import frame.scene.Scene;

public interface Trap {
    void setFloorState(Floor floor);
    void execute(Actor player, Floor floor, Scene scene);
}
