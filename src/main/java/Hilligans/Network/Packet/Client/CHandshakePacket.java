package Hilligans.Network.Packet.Client;

import Hilligans.Client.ClientPlayerData;
import Hilligans.ClientMain;
import Hilligans.Data.Other.BlockPos;
import Hilligans.Data.Other.ServerSidedData;
import Hilligans.Data.Primitives.DoubleTypeWrapper;
import Hilligans.Entity.Entity;
import Hilligans.Entity.LivingEntities.PlayerEntity;
import Hilligans.Network.ClientAuthNetworkHandler;
import Hilligans.Network.Packet.AuthServerPackets.CTokenValid;
import Hilligans.Network.Packet.Server.*;
import Hilligans.Data.Other.Server.ServerPlayerData;
import Hilligans.Network.PacketBase;
import Hilligans.Network.PacketData;
import Hilligans.Network.ServerNetworkHandler;
import Hilligans.ServerMain;
import Hilligans.Util.Settings;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.Random;


public class CHandshakePacket extends PacketBase {

    public int id;
    public String name;
    public String authToken;
    public long version;

    public CHandshakePacket() {
        super(5);
    }

    @Override
    public void encode(PacketData packetData) {
        packetData.writeInt(Settings.gameVersion);
        packetData.writeString(ClientMain.getClient().playerData.userName);
        packetData.writeLong(ServerSidedData.getInstance().version);
        packetData.writeString(ClientMain.getClient().playerData.authToken);
    }

    @Override
    public void decode(PacketData packetData) {
        id = packetData.readInt();
        name = packetData.readString();
        version = packetData.readLong();
        authToken = packetData.readString();
    }

    @Override
    public void handle() {
        if(id == Settings.gameVersion) {

            if(Settings.isOnlineServer) {
                String token = getToken(16);
                ServerMain.server.waitingPlayers.put(ctx,this);
                ServerMain.server.playerQueue.put(token,new DoubleTypeWrapper<>(ctx,System.currentTimeMillis() + 5000));
                ChannelFuture future = ClientAuthNetworkHandler.sendPacketDirect(new CTokenValid(name,authToken,((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress(),token));
            } else {
                handlePlayer(name, version, ctx, name);
            }
        } else {
            ServerNetworkHandler.sendPacketClose(new SDisconnectPacket("Game version is incompatible"),ctx);
        }
    }

    public static synchronized void handlePlayer(String name, long version, ChannelHandlerContext ctx, String identifier) {
        int playerId = Entity.getNewId();

        ChannelId channelId = ServerNetworkHandler.nameToChannel.get(name);
        BlockPos spawn = ServerMain.getWorld(0).getWorldSpawn(Settings.playerBoundingBox);
        PlayerEntity playerEntity = new PlayerEntity(spawn.x,spawn.y,spawn.z,playerId);
        ServerPlayerData serverPlayerData = ServerPlayerData.loadOrCreatePlayer(playerEntity,identifier);

        ServerNetworkHandler.playerData.put(playerId, serverPlayerData);
        ServerNetworkHandler.mappedChannels.put(playerId,ctx.channel().id());
        ServerNetworkHandler.mappedId.put(ctx.channel().id(),playerId);
        ServerNetworkHandler.mappedName.put(ctx.channel().id(),name);
        ServerNetworkHandler.nameToChannel.put(name, ctx.channel().id());
        ServerMain.getWorld(serverPlayerData.getDimension()).addEntity(playerEntity);
        ServerNetworkHandler.sendPacket(new SHandshakePacket(playerId,ServerSidedData.getInstance().version),ctx);
        ServerNetworkHandler.sendPacket(new SChatMessage(name + " has joined the game"));
        for(Entity entity : ServerMain.getWorld(serverPlayerData.getDimension()).entities.values()) {
            ServerNetworkHandler.sendPacket(new SCreateEntityPacket(entity),ctx);
        }
        ServerNetworkHandler.sendPacket(new SUpdatePlayer(spawn.x,spawn.y,spawn.z,0,0),ctx);
        serverPlayerData.playerInventory.age++;

        if(version != ServerSidedData.getInstance().version) {
            ServerSidedData.getInstance().sendDataToClient(ctx);
        }

        ServerNetworkHandler.sendPacket(new SUpdateInventory(serverPlayerData.playerInventory),ctx);
    }

    public static final String alphanum = "ABCDEFGHIJKLMNOPQRSTUVQXYZabcdefghijklmnopqrstuvwxyz1234567890`!@#$%^&*()-_=+~[]\\;',./{}|:\"<>?;";
    private static final char[] symbols = alphanum.toCharArray();
    static Random random = new SecureRandom();

    public static String getToken(int length) {
        char[] buf = new char[length];
        int salt = (int) (System.nanoTime() & 31);
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = getChar(Math.abs(Integer.rotateRight(random.nextInt(),salt)));
        return new String(buf);
    }

    public static char getChar(int index) {
        return symbols[index % symbols.length];
    }
}
