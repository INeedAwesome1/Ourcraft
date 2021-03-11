package Hilligans.Network;

import Hilligans.Block.Blocks;
import Hilligans.Client.Rendering.Textures;
import Hilligans.ClientMain;
import Hilligans.Network.Packet.Client.CHandshakePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static org.lwjgl.glfw.GLFW.*;

public class ClientNetworkHandler extends SimpleChannelInboundHandler<PacketData> {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ClientNetworkHandler.sendPacket(new CHandshakePacket());
        super.channelActive(ctx);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Textures.clear();
        System.out.println("DISCONNECTED FROM SERVER");
        if(!ClientMain.valid) {
            System.out.println("YOUR GAME VERSION MAY BE OUT OF DATE");
        }
        super.channelInactive(ctx);

        glfwDestroyWindow(ClientMain.window);
        System.exit(1);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PacketData msg) throws Exception {
        PacketBase packetBase = msg.createPacket();
        packetBase.handle();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public static void sendPacket(PacketBase packetBase) {
        if(ClientNetworkInit.channel != null) {
            ClientNetworkInit.channel.writeAndFlush(new PacketData(packetBase));
        }
    }

}
