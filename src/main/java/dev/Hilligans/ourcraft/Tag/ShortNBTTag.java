package dev.Hilligans.ourcraft.Tag;

import java.nio.ByteBuffer;

public class ShortNBTTag extends NBTTag {

    public short val;

    public ShortNBTTag() {}

    public ShortNBTTag(short val) {
        this.val = val;
    }

    @Override
    int getSize() {
        return 2;
    }

    @Override
    public byte getId() {
        return 2;
    }

    @Override
    public void read(ByteBuffer byteBuf) {
        val = byteBuf.getShort();
    }

    @Override
    public void write(ByteBuffer byteBuf) {
        byteBuf.putShort(val);
    }

    @Override
    public NBTTag duplicate() {
        return new ShortNBTTag(val);
    }

    @Override
    public String getVal() {
        return val + "";
    }

    @Override
    public String toString() {
        return "ShortTag{" +
                "val=" + val +
                '}';
    }
}
