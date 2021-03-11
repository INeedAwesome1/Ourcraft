package Hilligans.Data.Other;

import Hilligans.Item.ItemStack;
import Hilligans.Network.PacketData;
import Hilligans.Data.Other.Server.IInventoryChanged;

import java.util.ArrayList;
import java.util.Arrays;

public class Inventory implements IInventory {

    ItemStack[] items;
    public int age;
    //public ArrayList<IInventoryChanged> linkedObjects = new ArrayList<>();

    public ArrayList<IInventoryChanged>[] listeners;

    public Inventory(int size) {
        items = new ItemStack[size];
        listeners = new ArrayList[size];
        for(int x = 0; x < size; x++) {
            items[x] = new ItemStack(null,(byte)0);
            listeners[x] = new ArrayList<>(1);
        }
    }

    @Override
    public int getSize() {
        return items.length;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items[slot];
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        items[slot] = item;
        notifyListeners(slot);
    }

    public void markDirty() {
        age++;
    }

    @Override
    public boolean addItem(ItemStack itemStack) {
        int x = 0;
        for(ItemStack itemStack1 : items) {
            int toRemove = itemStack1.addItem(itemStack);
            itemStack.count -= toRemove;
            if(itemStack.count == 0) {
                return true;
            }
            if(toRemove != 0) {
                notifyListeners(x);
            }
            x++;
        }
        return false;
    }

    public void notifyListeners(int slot) {
        for(IInventoryChanged iInventoryChanged : listeners[slot]) {
            iInventoryChanged.onChange(slot,this);
        }
    }


    public void writeData(PacketData packetData) {
        packetData.writeInt(age);
        packetData.writeInt(items.length);
        for (ItemStack item : items) {
            packetData.writeItemStack(item);
        }
    }

    public void readData(PacketData packetData) {
        int age = packetData.readInt();
        if(age > this.age) {
            int size = packetData.readInt();
            for (int x = 0; x < size; x++) {
                items[x] = packetData.readItemStack();
                if(!items[x].isEmpty()) {
                    //if(items[x].item instanceof BlockItem)
                    //System.out.println(((BlockItem) items[x].item).block);
                }
            }
        }
        //System.out.println(this.toString());
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "items=" + Arrays.toString(items) +
                ", age=" + age +
                '}';
    }
}
