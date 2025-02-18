package dev.Hilligans.ourcraft.World.DataProviders;

import dev.Hilligans.ourcraft.Data.Other.Inventory;
import dev.Hilligans.ourcraft.Tag.CompoundNBTTag;
import dev.Hilligans.ourcraft.World.DataProvider;

public class ChestDataProvider extends DataProvider {

    public Inventory inventory = new Inventory(27);

    @Override
    public void read(CompoundNBTTag tag) {
        for(int x = 0; x < 27; x++) {
            inventory.setItem(x,tag.readStack(x));
        }
        super.read(tag);
    }

    @Override
    public void write(CompoundNBTTag tag) {
        for(int x = 0; x < 27; x++) {
            tag.writeStack(x,inventory.getItem(x));
        }
        super.write(tag);
    }
}
