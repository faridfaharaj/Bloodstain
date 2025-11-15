package com.faridfaharaj.bloodstain.handlers;

import com.faridfaharaj.bloodstain.Tags;
import com.faridfaharaj.bloodstain.data.StainData;
import com.faridfaharaj.bloodstain.enchantments.ModEnchantments;
import com.faridfaharaj.bloodstain.entities.entity.Ghost;
import com.faridfaharaj.bloodstain.playerHistories.PlayerMotionRecorder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class Events {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            GameRules rules = event.getWorld().getGameRules();
            rules.setOrCreateGameRule("keepInventory", "true");
        }
    }

    @SubscribeEvent
    public void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getContainer().inventorySlots.isEmpty()) return;
        EntityPlayer player = event.getEntityPlayer();
        World world = player.world;
        for(Slot slot : event.getContainer().inventorySlots){
            ItemStack stack = slot.getStack();
            if(isStainExpired(stack, world)){
                slot.putStack(ItemStack.EMPTY);
            }
        }
        event.getContainer().detectAndSendChanges();
    }



    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        EntityItem entityItem = event.getItem();
        ItemStack stack = entityItem.getItem();

        if(stack.isEmpty()) return;
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP)) return;
        if(!stack.hasTagCompound())return;
        if(!stack.getTagCompound().hasKey("bloodstain_curse_tracker")) return;

        if (isStainExpired(stack, entityItem.world)) {
            entityItem.setDead();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntityLoad(EntityEvent.EntityConstructing event) {
        Entity entity = event.getEntity();
        if(entity instanceof EntityItem){
            EntityItem entityItem = (EntityItem) entity;
            if(isStainExpired(entityItem.getItem(), entity.world)){
                entityItem.setDead();
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP) {

            EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
            World world = player.world;

            if (!world.isRemote) {
                // ghost items
                StainData.PlayerStainData stainData = StainData.getStainData(player.getUniqueID(), player.world);
                stainData.deaths = player.getStatFile().readStat(StatList.DEATHS)+1;

                IInventory inventory = player.inventory;
                for(int i = 0; i < inventory.getSizeInventory(); i++){
                    ItemStack stack = inventory.getStackInSlot(i);
                    if(stack.isEmpty()) continue;

                    if(isStainExpired(stack, player.world)){
                        inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                    }else {
                        stainItem(stack, player);
                    }
                }

                for(Entity entity : player.getEntityWorld().getLoadedEntityList()){
                    if(entity instanceof EntityItem){
                        EntityItem entityItem = (EntityItem) entity;
                        if(isStainExpired(entityItem.getItem(), player.world)){
                            entityItem.setDead();
                        }
                    }
                }


                // spawn ghost
                PlayerMotionRecorder recorder = stainData.recorder;
                if (recorder != null) {
                    recorder.saveRecording();
                }

                Ghost ghost = new Ghost(world, player.getUniqueID());
                ghost.setPosition(player.posX, player.posY, player.posZ);
                world.spawnEntity(ghost);
            }
        }
    }

    public boolean isStainExpired(ItemStack stack, World world){
        if(!stack.hasTagCompound()) return false;
        NBTTagCompound nbt = stack.getTagCompound();
        if(!nbt.hasKey("bloodstain_curse_tracker"))return false;

        NBTTagCompound stainNBT = nbt.getCompoundTag("bloodstain_curse_tracker");

        int currentDeaths = StainData.getStainData(stainNBT.getUniqueId("owner"), world).deaths;
        return currentDeaths > stainNBT.getInteger("deaths");
    }

    public void stainItem(ItemStack stack, EntityPlayer player){
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt == null) {
            nbt = new NBTTagCompound();
        }
        NBTTagCompound deathNBT = new NBTTagCompound();

        UUID id = player.getUniqueID();
        deathNBT.setUniqueId("owner", player.getUniqueID());
        deathNBT.setInteger("deaths", StainData.getStainData(id, player.world).deaths);

        nbt.setTag("bloodstain_curse_tracker", deathNBT);
        stack.setTagCompound(nbt);
        stack.addEnchantment(ModEnchantments.GHOST_CURSE, 1);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;

        if(!player.world.isRemote){
            PlayerMotionRecorder recorder = StainData.getStainData(player.getUniqueID(), player.world).recorder;
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
