package com.faridfaharaj.bloodstain.proxies;

import com.faridfaharaj.bloodstain.entities.entity.Ghost;
import com.faridfaharaj.bloodstain.entities.renderers.GhostRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy{

    @Override
    public void proxyPreInit(){
        // Run CommonProxy's (extended class) pre-initialization processes
        super.proxyPreInit();

        RenderingRegistry.registerEntityRenderingHandler(
                Ghost.class,
                GhostRenderer::new
        );

    }

    @Override
    public void proxyInit() {
        // Run CommonProxy's (extended class) initialization processes
        super.proxyInit();

        // initialization (client) code

    }

    @Override
    public void proxyPostInit() {
        // Run CommonProxy's (extended class) post-initialization processes
        super.proxyPostInit();

        // Post-initialization (client) code

    }

}
