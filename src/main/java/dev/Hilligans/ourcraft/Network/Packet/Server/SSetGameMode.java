package dev.Hilligans.ourcraft.Network.Packet.Server;

import dev.Hilligans.ourcraft.ClientMain;
import dev.Hilligans.ourcraft.Network.PacketBase;
import dev.Hilligans.ourcraft.Network.PacketData;

public class SSetGameMode extends PacketBase {

    int id;

    public SSetGameMode() {
        super(25);
    }

    public SSetGameMode(int id) {
        this();
        this.id = id;
    }

    @Override
    public void encode(PacketData packetData) {
        packetData.writeByte((byte)id);
    }

    @Override
    public void decode(PacketData packetData) {
        id = packetData.readByte();
    }

    @Override
    public void handle() {
        if(id == 0) {
            ClientMain.getClient().playerData.flying = false;
            ClientMain.getClient().playerData.creative = false;
            ClientMain.getClient().playerData.spectator = false;
        } else {
            ClientMain.getClient().playerData.creative = true;
            ClientMain.getClient().playerData.flying = true;
            ClientMain.getClient().playerData.spectator = true;
        }
    }
}
