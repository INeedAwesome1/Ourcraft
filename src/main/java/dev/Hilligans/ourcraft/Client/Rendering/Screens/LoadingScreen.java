package dev.Hilligans.ourcraft.Client.Rendering.Screens;

import dev.Hilligans.ourcraft.Client.Client;
import dev.Hilligans.ourcraft.Client.MatrixStack;
import dev.Hilligans.ourcraft.Client.Rendering.ScreenBase;

public class LoadingScreen extends ScreenBase {

    public LoadingScreen(Client client) {
        super(client);
    }

    @Override
    public void drawScreen(MatrixStack matrixStack) {
        super.drawScreen(matrixStack);
        //if(ClientNetworkInit.networkInit != null && ClientNetworkInit.networkInit.packetDecoder != null) {
      //      StringRenderer.drawString(matrixStack, "Content downloading:" + ClientNetworkInit.networkInit.packetDecoder.getPercentage(), 100, 100, 0.5f);
     //   }
    }
}
