package com.faridfaharaj.bloodstain.entities.entity;

import com.faridfaharaj.bloodstain.playerHistories.PlayerMotionRecorder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.UUID;

public class Ghost extends EntityLiving {

    private final UUID playerUUID;
    long playbackTime = System.currentTimeMillis();
    int actualTick = 0;

    private static final DataParameter<Boolean> RENDER_RIDING = EntityDataManager.createKey(Ghost.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> SWINGING = EntityDataManager.createKey(Ghost.class, DataSerializers.FLOAT);

    public float pastSwing = 0;
    public float getSwing(){
        float swing = this.dataManager.get(SWINGING);
        if(world.isRemote){
            pastSwing = swing;
        }
        return swing;
    }
    public boolean getRiding(){
        return this.dataManager.get(RENDER_RIDING);
    }

    public Ghost(World worldIn) {
        super(worldIn);
        this.setNoAI(true);
        this.setSize(0.6F, 1.8F);
        this.setHealth(Integer.MAX_VALUE);

        this.playerUUID = null;
    }

    public Ghost(World worldIn, UUID playerUUID) {
        super(worldIn);
        this.setNoAI(true);
        this.setSize(0.6F, 1.8F);
        this.setHealth(Integer.MAX_VALUE);

        this.playerUUID = playerUUID;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(RENDER_RIDING, false);
        this.dataManager.register(SWINGING, 0.0f);
    }

    @Override
    public float getEyeHeight() {
        return 1.62F;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.noClip = true;
    }

    @Override
    public EnumHandSide getPrimaryHand() {
        return EnumHandSide.RIGHT;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if(world.isRemote) return;

        if (playerUUID == null) return;

        PlayerMotionRecorder recorder = PlayerMotionRecorder.recorders.get(playerUUID);
        if (recorder == null || recorder.getSnapshotsSize() < 2) return;

        PlayerMotionRecorder.PlayerMotionSnapshot prev = recorder.getSnapshot(actualTick);
        PlayerMotionRecorder.PlayerMotionSnapshot next = recorder.getSnapshot(actualTick + 1);

        float t = (System.currentTimeMillis() - playbackTime) / (float) (next.time - prev.time);
        t = Math.min(Math.max(t, 0f), 1f);

        double x = lerp(prev.posX, next.posX, t);
        double y = lerp(prev.posY, next.posY, t);
        double z = lerp(prev.posZ, next.posZ, t);
        float yaw = prev.rotationYaw + MathHelper.wrapDegrees(next.rotationYaw - prev.rotationYaw) * t;
        float pitch = lerp(prev.rotationPitch, next.rotationPitch, t);

        this.setPositionAndRotation(x, y, z, yaw, pitch);

        if (prev.isBurning) {
            this.setFire(1);
        }

        this.setSneaking(prev.isSneaking);

        if (prev.hasPotion) {
            if(!this.isPotionActive(MobEffects.HUNGER)){
                addPotionEffect(new PotionEffect(MobEffects.HUNGER, 99999, 1));
            }
        }else {
            if(this.isPotionActive(MobEffects.HUNGER)){
                clearActivePotions();
            }
        }

        if(prev.ishurt){
            if(this.hurtTime<=0){
                this.hurtTime = this.maxHurtTime = 10; // WTF this should be illegal
                this.world.setEntityState(this, (byte)2);
            }
        }


        if (prev.isSwingInProgress) {
            if (!this.isSwingInProgress) {
                this.swingArm(EnumHand.MAIN_HAND);
            }
        }
        this.updateArmSwingProgress();
        this.dataManager.set(SWINGING, this.swingProgress);

        this.dataManager.set(RENDER_RIDING, prev.isRiding);

        if (t >= 1f) {
            playbackTime = System.currentTimeMillis();
            actualTick++;
            if(actualTick >= recorder.getSnapshotsSize()-1){
                this.setHealth(0.0F);
            }
        }
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private double lerp(double a, double b, float t) {
        return a + (b - a) * t;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        this.hurtTime = this.maxHurtTime = 10;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.isFireDamage()) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return null;
    }

    @Nullable
    public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
        return null;
    }

    @Override
    public boolean isImmuneToExplosions()
    {
        return true;
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {}

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean canBeHitWithPotion() {
        return false;
    }

    @Override
    public boolean isInWater()
    {
        return false;
    }

    @Override
    public boolean isOverWater()
    {
        return false;
    }

    @Override
    public boolean isInLava()
    {
        return false;
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound compound) {
        return false;
    }

}
