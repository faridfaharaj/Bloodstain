package com.faridfaharaj.bloodstain.enchantments.ghostcurse;

import com.faridfaharaj.bloodstain.Tags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class GhostCurse extends Enchantment {
    public GhostCurse() {
        super(Rarity.COMMON, EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
        this.setRegistryName(Tags.MOD_ID, "ghosted");
        this.setName("ghosted");
    }

    @Override
    public boolean isCurse()
    {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return true;
    }



}
