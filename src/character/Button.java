package character;

import util.ResourcesManager;

import java.awt.*;

public class Button extends AnimationGameObject {

    public Button(int x, int y, int drawWidth, int drawHeight, String imagePath){
        super(x, y, drawWidth, drawHeight);
        this.image = ResourcesManager.getInstance().getImage(imagePath);
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(image, x, y, x+drawWidth, y+drawHeight,
                imageOffsetX*drawWidth, 0, imageOffsetX*drawWidth + drawWidth, drawHeight
                ,null);
    }
}
