package com.faridfaharaj.bloodstain.entities.entity;

import com.faridfaharaj.bloodstain.playerHistories.PlayerMotionRecorder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class Ghost extends EntityLiving {

    private final UUID playerUUID;
    long playbackTime = System.currentTimeMillis();
    int actualTick = 0;

    public Ghost(World worldIn) {
        super(worldIn);
        this.setNoAI(true);
        this.setSize(0.6F, 1.8F);

        this.playerUUID = null;
    }

    public Ghost(World worldIn, UUID playerUUID) {
        super(worldIn);
        this.setNoAI(true);
        this.setSize(0.6F, 1.8F);

        this.playerUUID = playerUUID;
    }

    @Override
    public float getEyeHeight() {
        return 1.62F;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.noClip = true;
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (world.isRemote) return;

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
    public boolean isAIDisabled() {
        return true;
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
