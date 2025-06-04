package com.faridfaharaj.bloodstain.proxies;

import com.faridfaharaj.bloodstain.entities.EntityRegistry;

public class CommonProxy {

    public void proxyPreInit(){

        EntityRegistry.registerEntities();

    }

    public void proxyInit() {

        // initialization (server/client) code

    }

    public void proxyPostInit() {

        // Post-initialization (server/client) code

    }

}
