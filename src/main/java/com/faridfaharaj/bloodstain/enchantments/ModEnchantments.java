package com.faridfaharaj.bloodstain.enchantments;

import com.faridfaharaj.bloodstain.Tags;
import com.faridfaharaj.bloodstain.enchantments.ghostcurse.GhostCurse;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class ModEnchantments {

    public static final Enchantment GHOST_CURSE = new GhostCurse();

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().register(GHOST_CURSE);
    }

}
