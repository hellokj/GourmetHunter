package character;

import frame.MainPanel;
import util.PainterManager;
import util.ResourcesManager;

import java.awt.*;

public class Button extends AnimationGameObject {

    public Button(int x, int y, int drawWidth, int drawHeight, String imagePath){
        super(x, y, drawWidth, drawHeight);
        this.image = ResourcesManager.getInstance().getImage(imagePath);
    }

    public Button(int x, int y, int drawWidth, int drawHeight, int imageWidth, int imageHeight, String imagePath) {
        super(x, y, drawWidth, drawHeight, imageWidth, imageHeight, imagePath);
    }

    @Override
    public void paint(Graphics g, MainPanel mainPanel){
        Graphics2D g2d = PainterManager.g2d(g);
        modX = (int) (x * MainPanel.RATIO);
        modY = (int) (y * MainPanel.RATIO);
        g2d.drawImage(image, modX, modY, (int)(modX+drawWidth* MainPanel.RATIO), (int)(modY+drawHeight* MainPanel.RATIO),
                imageOffsetX*imageWidth, 0, imageOffsetX*imageWidth + imageWidth, imageHeight
                ,null);
//        g2d.setColor(Color.BLACK);
//        g2d.drawRect(modX-1, modY-1, (int)(drawWidth*MainPanel.RATIO + 1), (int)(drawHeight*MainPanel.RATIO +1));
    }
}
