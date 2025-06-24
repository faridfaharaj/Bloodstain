package com.faridfaharaj.bloodstain;

import com.faridfaharaj.bloodstain.entities.entity.Ghost;
import com.faridfaharaj.bloodstain.playerHistories.PlayerMotionRecorder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class Events {

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {

            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            World world = player.world;

            if (!world.isRemote) {
                PlayerMotionRecorder recorder = PlayerMotionRecorder.recorders.get(player.getUniqueID());
                if (recorder != null) {
                    recorder.saveRecording();
                }

                Ghost ghost = new Ghost(world, player.getUniqueID());
                ghost.setPosition(player.posX, player.posY, player.posZ);
                world.spawnEntity(ghost);
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;

        if(!player.world.isRemote){
            PlayerMotionRecorder recorder = PlayerMotionRecorder.recorders.computeIfAbsent(player.getUniqueID(), id -> new PlayerMotionRecorder());
            recorder.record(player);
        }
    }

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
