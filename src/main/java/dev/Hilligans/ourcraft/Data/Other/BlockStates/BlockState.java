package dev.Hilligans.ourcraft.Data.Other.BlockStates;

import dev.Hilligans.ourcraft.Block.Block;
import dev.Hilligans.ourcraft.Block.Blocks;

import java.util.Objects;

public class BlockState {

    public short blockId;

    public BlockState(Block block) {
        this.blockId = block.id;
    }

    public BlockState(short blockId) {
        this.blockId = blockId;
    }

    public Block getBlock() {
        return Blocks.getBlockWithID(blockId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockState that = (BlockState) o;
        return blockId == that.blockId;
    }

    public short readData() {
        return -1;
    }

    public int get() {
        return blockId << 16 | readData();
    }

    @Override
    public int hashCode() {
        return Objects.hash(get());
    }

    public BlockState duplicate() {
        return new BlockState(blockId);
    }
}
