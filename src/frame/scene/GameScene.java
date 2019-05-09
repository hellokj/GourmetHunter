package frame.scene;

import frame.MainPanel;
import mode.GameMode;
import state.GameState;

public abstract class GameScene extends Scene{
    public GameScene(MainPanel.GameStatusChangeListener gsChangeListener) {
        super(gsChangeListener);
    }

    public abstract void setGame(GameMode gameMode);
    public abstract void setGameState(GameState state);

}
