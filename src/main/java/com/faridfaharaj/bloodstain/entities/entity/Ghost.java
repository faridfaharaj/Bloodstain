package com.faridfaharaj.bloodstain.entities.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Ghost extends EntityLiving {

    public Ghost(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.8F);
    }

    @Override
    public float getEyeHeight() {
        return 1.62F;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.setNoGravity(true);
        this.noClip = true;
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
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
    public boolean hasNoGravity() {
        return true;
    }

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

}
