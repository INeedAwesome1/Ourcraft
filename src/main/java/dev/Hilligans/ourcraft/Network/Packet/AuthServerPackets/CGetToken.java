package dev.Hilligans.ourcraft.Network.Packet.AuthServerPackets;

import dev.Hilligans.ourcraft.Network.PacketBase;
import dev.Hilligans.ourcraft.Network.PacketData;

public class CGetToken extends PacketBase {

    String username;
    String loginToken;

    public CGetToken() {
        super(1);
    }

    public CGetToken(String username, String loginToken) {
        this();
        this.username = username;
        this.loginToken = loginToken;
    }

    @Override
    public void encode(PacketData packetData) {
        packetData.writeString(username);
        packetData.writeString(loginToken);
    }

    @Override
    public void decode(PacketData packetData) {}

    @Override
    public void handle() {}
}
