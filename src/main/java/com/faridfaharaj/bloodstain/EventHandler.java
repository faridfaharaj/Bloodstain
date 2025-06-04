package com.faridfaharaj.bloodstain;

import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class EventHandler {

    @SubscribeEvent
    public static void itemRegistryEvent(RegistryEvent.Register<Item> event){
        /*
        event.getRegistry().register(myItem);
        */
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void modelRegistryEvent(ModelRegistryEvent event) {
        /*
        ModelLoader.setCustomModelResourceLocation(myItem, 0, new ModelResourceLocation(Tags.MOD_ID + ":" + "item", "inventory"));
        */
    }

}
