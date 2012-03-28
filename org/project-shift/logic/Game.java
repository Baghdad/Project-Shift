import com.golden.gamedev.GameLoader;
import com.golden.gamedev.engine.audio.JavaLayerMp3Renderer;

import java.awt.*;

/**
 * Author: Bogdanov Kirill
 * Date: 29.03.12
 * Time: 0:39
 */
public class Game extends com.golden.gamedev.Game{
    public void initEngine() {
        super.initEngine();
        bsSound.setBaseRenderer(new JavaLayerMp3Renderer());
    }
    public void initResources() {
         playSound("org/project-shift/resources/music/TheOnly.mp3");
    }
    public void update(long elapsedTime) {

    }
    public void render(Graphics2D g) {

    }
    public static void main (String[] args) {
        GameLoader game = new GameLoader();
        game.setup(new Game(), new Dimension(640, 480), false);
        game.start();
    }
}
