package com.faridfaharaj.bloodstain.networking;

import com.faridfaharaj.bloodstain.networking.packets.EntityGhostPacket;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel("yourmodid");

    public static void registerPackets() {
        int i = 0;

        WRAPPER.registerMessage(EntityGhostPacket.Handler.class, EntityGhostPacket.class, 0, Side.CLIENT);
    }

}
