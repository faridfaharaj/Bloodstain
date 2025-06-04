package com.faridfaharaj.bloodstain.proxies;

public class ClientProxy extends CommonProxy{

    @Override
    public void proxyPreInit(){
        // Run CommonProxy's (extended class) pre-initialization processes
        super.proxyPreInit();

        // Pre-initialization (client) code

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
