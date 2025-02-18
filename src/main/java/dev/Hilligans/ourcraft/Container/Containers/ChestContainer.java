package dev.Hilligans.ourcraft.Container.Containers;

import dev.Hilligans.ourcraft.Client.Client;
import dev.Hilligans.ourcraft.Client.Rendering.ContainerScreen;
import dev.Hilligans.ourcraft.Client.Rendering.Screens.ContainerScreens.ChestScreen;
import dev.Hilligans.ourcraft.ClientMain;
import dev.Hilligans.ourcraft.Container.Container;
import dev.Hilligans.ourcraft.Container.Slot;
import dev.Hilligans.ourcraft.Data.Other.Inventory;
import dev.Hilligans.ourcraft.Data.Other.JoinedInventory;

public class ChestContainer extends Container {

    public ChestContainer() {
        this(new Inventory(27), ClientMain.getClient().playerData.inventory);
    }

    public ChestContainer(Inventory inventory, Inventory playerInventory) {
        super(1,new JoinedInventory(inventory,playerInventory));
        setTextureSize(158,162);
        for(int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(7 + x * 16, 12 + y * 16, inventory, x + y * 9));
            }
        }
        for(int x = 0; x < 9; x++) {
            addSlot(new Slot(7 + 16 * x,139, playerInventory,x));
        }
        for(int y = 0; y < 4; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(7 + x * 16, 70 + y * 16,playerInventory,9 + x + y * 9));
            }
        }
        resize();
    }

    @Override
    public ContainerScreen<?> getContainerScreen(Client client) {
        return new ChestScreen(client);
    }
}
