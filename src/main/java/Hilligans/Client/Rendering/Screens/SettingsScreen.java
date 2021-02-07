package Hilligans.Client.Rendering.Screens;

import Hilligans.Client.Camera;
import Hilligans.Client.MatrixStack;
import Hilligans.Client.Rendering.ScreenBase;
import Hilligans.Client.Rendering.Widgets.SliderChange;
import Hilligans.Client.Rendering.Widgets.SliderWidget;
import Hilligans.Client.Rendering.World.StringRenderer;
import Hilligans.Util.Settings;

public class SettingsScreen extends ScreenBase {

    public SettingsScreen() {
        widgets.add(new SliderWidget(50, 50, 200, 40, 50, 200, (int)Camera.sensitivity, value -> Camera.sensitivity = value));
        widgets.add(new SliderWidget(50, 150, 200, 40, 10, 110, Camera.fov, value -> Camera.fov = value));
        widgets.add(new SliderWidget(50, 250, 200, 40, 2, 16, Settings.renderDistance, value -> Settings.renderDistance = value));
    }

    @Override
    public void render(MatrixStack matrixStack) {
        super.render(matrixStack);

        StringRenderer.drawString(matrixStack, "Sensitivity",50,14,0.5f);
        StringRenderer.drawString(matrixStack, "FOV",50,114,0.5f);
        StringRenderer.drawString(matrixStack,"Render Distance",50,214,0.5f);
    }
}