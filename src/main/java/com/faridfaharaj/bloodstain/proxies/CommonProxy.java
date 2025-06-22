package com.faridfaharaj.bloodstain.proxies;

import com.faridfaharaj.bloodstain.Events;
import com.faridfaharaj.bloodstain.entities.EntityRegistry;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {

    public void proxyPreInit(){

        MinecraftForge.EVENT_BUS.register(new Events());
        EntityRegistry.registerEntities();

    }

    public void proxyInit() {

        // initialization (server/client) code

    }

    public void proxyPostInit() {

        // Post-initialization (server/client) code

    }

}
