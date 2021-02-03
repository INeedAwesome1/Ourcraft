package Hilligans.Client.Rendering;

import Hilligans.Client.Key.CharPress;
import Hilligans.Client.Key.KeyHandler;
import Hilligans.Client.Key.KeyPress;
import Hilligans.Client.MatrixStack;
import Hilligans.Client.Rendering.Widgets.Widget;

import java.util.ArrayList;

public abstract class ScreenBase implements Screen {

    public ArrayList<Widget> widgets = new ArrayList<>();
    public ArrayList<CharPress> charPresses = new ArrayList<>();
    public ArrayList<KeyPress> keyPresses = new ArrayList<>();

    public void drawScreen(MatrixStack matrixStack) {}

    public void render(MatrixStack matrixStack) {
        drawScreen(matrixStack);
        for(Widget widget : widgets) {
            widget.render(matrixStack);
        }
    }

    public void registerCharPress(CharPress charPress) {
        charPresses.add(charPress);
        KeyHandler.register(charPress);
    }

    public void registerKeyPress(KeyPress keyPress, int id) {
        keyPresses.add(keyPress);
        KeyHandler.register(keyPress,id);
    }

    @Override
    public void close() {
        for(Widget widget : widgets) {
            widget.screenClose();
        }

        for(CharPress charPress : charPresses) {
            KeyHandler.remove(charPress);
        }

        for(KeyPress keyPress : keyPresses) {
            KeyHandler.remove(keyPress);
        }

    }

    @Override
    public void mouseClick(int x, int y) {
        for(Widget widget : widgets) {
            widget.isFocused = false;
            if(widget.isInBounds(x,y)) {
                widget.activate();
            }
        }
    }
}
