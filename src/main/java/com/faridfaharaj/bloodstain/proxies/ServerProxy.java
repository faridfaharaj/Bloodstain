package com.faridfaharaj.bloodstain.proxies;

public class ServerProxy extends CommonProxy{

    @Override
    public void proxyPreInit(){
        // Run CommonProxy's (extended class) pre-initialization processes
        super.proxyPreInit();

        // Pre-initialization (server) code

    }

    @Override
    public void proxyInit() {
        // Run CommonProxy's (extended class) initialization processes
        super.proxyInit();

        // initialization (server) code

    }

    @Override
    public void proxyPostInit() {
        // Run CommonProxy's (extended class) post-initialization processes
        super.proxyPostInit();

        // Post-initialization (server) code

    }

}
