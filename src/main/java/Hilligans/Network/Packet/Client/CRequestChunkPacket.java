package Hilligans.Network.Packet.Client;

import Hilligans.Network.Packet.Server.SSendChunkPacket;
import Hilligans.Network.PacketBase;
import Hilligans.World.Chunk;
import Hilligans.Network.PacketData;
import Hilligans.Network.ServerNetworkHandler;
import Hilligans.ServerMain;

public class CRequestChunkPacket extends PacketBase {

    public int ChunkX;
    public int ChunkY;

    public CRequestChunkPacket(int ChunkX, int ChunkY) {
        super(1);
        this.ChunkX = ChunkX;
        this.ChunkY = ChunkY;
    }

    public CRequestChunkPacket() {
        this(0,0);
    }

    @Override
    public void encode(PacketData packetData) {
        packetData.writeInt(ChunkX);
        packetData.writeInt(ChunkY);
    }

    @Override
    public void decode(PacketData packetData) {
        ChunkX = packetData.readInt();
        ChunkY = packetData.readInt();
    }

    @Override
    public void handle() {
        Chunk chunk = ServerMain.world.getOrGenerateChunk(ChunkX,ChunkY);
        if(chunk != null) {
            ServerNetworkHandler.sendPacket(new SSendChunkPacket(chunk),ctx);

        }
    }

}
