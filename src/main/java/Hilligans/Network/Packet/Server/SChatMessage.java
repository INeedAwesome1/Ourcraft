package Hilligans.Network.Packet.Server;

import Hilligans.Client.ChatWindow;
import Hilligans.Network.PacketBase;
import Hilligans.Network.PacketData;

public class SChatMessage extends PacketBase {

    String message;

    public SChatMessage() {
        super(12);
    }

    public SChatMessage(String message) {
        this();
        this.message = message;
    }

    @Override
    public void encode(PacketData packetData) {
        packetData.writeString(message);
    }

    @Override
    public void decode(PacketData packetData) {
        message = packetData.readString();

    }

    @Override
    public void handle() {
        ChatWindow.addMessage(message);
    }
}
