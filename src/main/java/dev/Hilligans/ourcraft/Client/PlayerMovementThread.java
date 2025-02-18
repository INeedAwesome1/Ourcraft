package dev.Hilligans.ourcraft.Client;

import dev.Hilligans.ourcraft.Client.Key.KeyHandler;
import dev.Hilligans.ourcraft.ClientMain;

public class PlayerMovementThread implements Runnable {

    long window;

    public PlayerMovementThread(long window) {
        this.window = window;

    }

    @Override
    public void run() {
        if(ClientMain.getClient().valid) {
            if(Camera.sprintDelay > 0) {
                Camera.sprintDelay--;
            }
            if(Camera.sprintTimeout == 0) {
                Camera.sprinting = false;
            }
            if(KeyHandler.keyPressed[KeyHandler.GLFW_KEY_LEFT_CONTROL] && Camera.sprintDelay == 0) {
                Camera.sprinting = true;
            }
            Camera.sprintTimeout = 0;
            ClientMain.getClient().processInput(window);
            Camera.tick();
        }
    }
}
