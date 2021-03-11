package Hilligans.Data.Other.Server;

import Hilligans.Container.Container;
import Hilligans.Container.Containers.InventoryContainer;
import Hilligans.Container.Slot;
import Hilligans.Data.Other.Inventory;
import Hilligans.Entity.Entities.ItemEntity;
import Hilligans.Entity.Entity;
import Hilligans.Entity.LivingEntities.PlayerEntity;
import Hilligans.Item.ItemStack;
import Hilligans.Item.Items;
import Hilligans.ServerMain;

public class PlayerData {

    public PlayerEntity playerEntity;
    public ItemStack heldStack = ItemStack.emptyStack();
    public Container openContainer;
    public Inventory playerInventory;
    public boolean isCreative = false;

    public PlayerData(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
        playerInventory = playerEntity.inventory;
        openContainer = new InventoryContainer(playerInventory).setPlayerId(playerEntity.id);
        playerInventory.setItem(0,new ItemStack(Items.HASHED_ITEMS.get("chest"), (byte)2));
        playerInventory.setItem(1,new ItemStack(Items.HASHED_ITEMS.get("slab"), (byte)10));
        playerInventory.setItem(2,new ItemStack(Items.HASHED_ITEMS.get("weeping_vine"), (byte)64));
        playerInventory.setItem(3,new ItemStack(Items.HASHED_ITEMS.get("stair"), (byte)63));
        playerInventory.setItem(4,new ItemStack(Items.HASHED_ITEMS.get("grass_plant"), (byte)63));
        playerInventory.setItem(5,new ItemStack(Items.HASHED_ITEMS.get("blue"),(byte)63));

    }

    public void openContainer(Container container) {
        if(!(container instanceof InventoryContainer)) {
            container.uniqueId = Container.getId();
        }
        openContainer.closeContainer();
        openContainer = container;
    }

    public void swapStack(short slot) {
        Slot itemSlot = openContainer.slots.get(slot);
        if(itemSlot != null) {
            if(itemSlot.canItemBeAdded(heldStack)) {
               heldStack = itemSlot.swapItemStacks(heldStack);
            }
        }
    }

    public void splitStack(short slot) {
        if(heldStack.isEmpty()) {
            heldStack = openContainer.slots.get(slot).splitStack();
        }
    }

    public void putOne(short slot) {
        if(!heldStack.isEmpty()) {
            if(openContainer.slots.get(slot).canAdd(1,heldStack)) {
                heldStack.count -= 1;
            }
        }
    }

    public void dropItem(short slot, byte count) {
        if(slot == -1) {
            if(!heldStack.isEmpty()) {
                if(count == -1 || count >= heldStack.count) {
                    ServerMain.world.addEntity(new ItemEntity(playerEntity.x, playerEntity.y, playerEntity.z, Entity.getNewId(), heldStack).setVel(playerEntity.getForeWard().mul(-0.5f).add(0, 0.25f, 0)));
                    heldStack = ItemStack.emptyStack();
                } else {
                    ServerMain.world.addEntity(new ItemEntity(playerEntity.x,playerEntity.y,playerEntity.z,Entity.getNewId(),new ItemStack(heldStack.item,count)).setVel(playerEntity.getForeWard().mul(-0.5f).add(0, 0.25f, 0)));
                    heldStack.count -= count;
                }
            }
        } else {
            Slot itemSlot = openContainer.getSlot(slot);
            if(itemSlot != null) {
                if(!itemSlot.getContents().isEmpty()) {
                    if(count == -1 || count >= itemSlot.getContents().count) {
                        ServerMain.world.addEntity(new ItemEntity(playerEntity.x, playerEntity.y, playerEntity.z, Entity.getNewId(), itemSlot.getContents()).setVel(playerEntity.getForeWard().mul(-0.5f).add(0, 0.25f, 0)));
                        itemSlot.setContents(ItemStack.emptyStack());
                    } else {
                        ServerMain.world.addEntity(new ItemEntity(playerEntity.x,playerEntity.y,playerEntity.z,Entity.getNewId(),new ItemStack(itemSlot.getContents().item,count)).setVel(playerEntity.getForeWard().mul(-0.5f).add(0, 0.25f, 0)));
                        itemSlot.getContents().count -= count;
                    }
                }
            }
        }
    }

    public void close() {
        openContainer.closeContainer();
    }

}