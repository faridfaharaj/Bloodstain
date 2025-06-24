package com.faridfaharaj.bloodstain.networking.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EntityGhostPacket implements IMessage {



    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<EntityGhostPacket, IMessage> {

        @Override
        public IMessage onMessage(EntityGhostPacket m, MessageContext ctx) {

            Minecraft.getMinecraft().addScheduledTask(() -> {

            });

            return null;
        }
    }
}
