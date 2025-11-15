package com.faridfaharaj.bloodstain.proxies;

import com.faridfaharaj.bloodstain.handlers.Events;
import com.faridfaharaj.bloodstain.entities.EntityRegistry;
import com.faridfaharaj.bloodstain.networking.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {

    public void proxyPreInit(){

        MinecraftForge.EVENT_BUS.register(new Events());
        EntityRegistry.registerEntities();
        NetworkHandler.registerPackets();

    }

    public void proxyInit() {

        // initialization (server/client) code

    }

    public void proxyPostInit() {

        // Post-initialization (server/client) code

    }

}
