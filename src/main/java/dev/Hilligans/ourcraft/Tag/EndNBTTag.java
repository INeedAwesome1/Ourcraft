package dev.Hilligans.ourcraft.Tag;

import java.nio.ByteBuffer;

public class EndNBTTag extends NBTTag {
    @Override
    int getSize() {
        return 0;
    }

    @Override
    public byte getId() {
        return 0;
    }

    @Override
    public void read(ByteBuffer byteBuf) {

    }

    @Override
    public void write(ByteBuffer byteBuf) {

    }

    @Override
    public NBTTag duplicate() {
        return new EndNBTTag();
    }
}
