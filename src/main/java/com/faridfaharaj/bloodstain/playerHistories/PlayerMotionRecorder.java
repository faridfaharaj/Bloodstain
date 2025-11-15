package com.faridfaharaj.bloodstain.playerHistories;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMotionRecorder {

    private static final int BUFFER_SIZE = 200;
    private final PlayerMotionSnapshot[] snapshots = new PlayerMotionSnapshot[BUFFER_SIZE];
    private final PlayerMotionSnapshot[] savedSnapshots = new PlayerMotionSnapshot[BUFFER_SIZE];
    {
        for (int i = 0; i < BUFFER_SIZE; i++) {
            snapshots[i] = new PlayerMotionSnapshot();
            savedSnapshots[i] = new PlayerMotionSnapshot();
        }
    }
    private int index = 0;
    private int size = 0;

    private int savedIndex = 0;
    private int savedSize = 0;

    private long lastRecorded = 0;


    public void record(EntityPlayer player) {
        long now = System.currentTimeMillis();
        if (now - lastRecorded >= 50L) {
            lastRecorded = now;
        }else {
            return;
        }

        snapshots[index].update(player);
        index = (index + 1) % BUFFER_SIZE;
        if (size < BUFFER_SIZE) size++;

    }

    public PlayerMotionSnapshot getSnapshot(int i) {
        int idx = (savedIndex - savedSize + i + BUFFER_SIZE) % BUFFER_SIZE;
        return savedSnapshots[idx];
    }

    public int getSnapshotsSize() {
        return savedSize;
    }

    public void saveRecording(){
        for (int i = 0; i < BUFFER_SIZE; i++) {
            savedSnapshots[i].update(snapshots[i]);
        }
        savedSize = size;
        size = 0;
        savedIndex = index;
    }

    public byte[] toBytes(){

        ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE*PlayerMotionSnapshot.getBytes() + Integer.BYTES*2);
        for(PlayerMotionSnapshot snap:savedSnapshots){
            buf.put(snap.toBytes());
        }
        buf.putInt(savedIndex);
        buf.putInt(savedSize);

        return buf.array();

    }

    public static PlayerMotionRecorder fromBytes(byte[] bytes){

        PlayerMotionRecorder recorder = new PlayerMotionRecorder();

        ByteBuffer buf = ByteBuffer.wrap(bytes);
        for(int i = 0; i<BUFFER_SIZE; i++){
            recorder.savedSnapshots[i].update(buf);
        }
        recorder.savedIndex = buf.getInt();
        recorder.savedSize = buf.getInt();

        return recorder;

    }

    public static class PlayerMotionSnapshot {
        public long time;
        public double posX, posY, posZ;
        public float rotationYaw, rotationPitch;
        public boolean isSneaking, isBurning, ishurt, hasPotion;

        public EnumAction action;

        public boolean isSwingInProgress, isRiding, isElytraFlying, isPlayerSleeping;

        public PlayerMotionSnapshot() {
            this.posX = 0;
            this.posY = 0;
            this.posZ = 0;
            this.rotationYaw = 0;
            this.rotationPitch = 0;

            this.isSneaking = false;
            this.isBurning = false;
            this.ishurt = false;
            this.hasPotion = false;

            this.action = EnumAction.NONE;

            this.isSwingInProgress = false;
            this.isRiding = false;
            this.isElytraFlying = false;
            this.isPlayerSleeping = false;

            this.time = 0;
        }

        public void update(PlayerMotionSnapshot snapshot) {
            this.posX = snapshot.posX;
            this.posY = snapshot.posY;
            this.posZ = snapshot.posZ;
            this.rotationYaw = snapshot.rotationYaw;
            this.rotationPitch = snapshot.rotationPitch;

            this.isSneaking = snapshot.isSneaking;
            this.isBurning = snapshot.isBurning;
            this.ishurt = snapshot.ishurt;
            this.hasPotion = snapshot.hasPotion;

            this.action = snapshot.action;

            this.isSwingInProgress = snapshot.isSwingInProgress;
            this.isRiding = snapshot.isRiding;
            this.isElytraFlying = snapshot.isElytraFlying;
            this.isPlayerSleeping = snapshot.isPlayerSleeping;

            this.time = snapshot.time;
        }

        public void update (EntityPlayer player){
            this.posX = player.posX;
            this.posY = player.posY;
            this.posZ = player.posZ;
            this.rotationYaw = player.rotationYaw;
            this.rotationPitch = player.rotationPitch;

            this.isSneaking = player.isSneaking();
            this.isBurning = player.isBurning();
            this.ishurt = player.hurtTime > 0;
            this.hasPotion = !player.getActivePotionEffects().isEmpty();

            if(player.isHandActive()){
                this.action = player.getActiveItemStack().getItemUseAction();
            }else {
                this.action = EnumAction.NONE;
            }

            this.isSwingInProgress = player.isSwingInProgress;
            this.isRiding = player.isRiding();
            this.isElytraFlying = player.isElytraFlying();
            this.isPlayerSleeping = player.isPlayerSleeping();

            this.time = System.currentTimeMillis();
        }

        static int BYTES = Long.BYTES + Double.BYTES*3 + Float.BYTES*2 + Byte.BYTES*4 + Byte.BYTES + Byte.BYTES*4;

        public static int getBytes(){
            return BYTES;
        }

        public byte[] toBytes(){

            ByteBuffer buf = ByteBuffer.allocate(getBytes());
            buf.putLong(time);

            buf.putDouble(posX);
            buf.putDouble(posY);
            buf.putDouble(posZ);
            buf.putFloat(rotationYaw);
            buf.putFloat(rotationPitch);

            buf.put((byte) (isSneaking?1:0));
            buf.put((byte) (isBurning?1:0));
            buf.put((byte) (ishurt?1:0));
            buf.put((byte) (hasPotion?1:0));

            byte actionNum;
            switch (action){
                case NONE:
                    actionNum = 0;
                    break;
                case EAT:
                    actionNum = 1;
                    break;
                case DRINK:
                    actionNum = 2;
                    break;
                case BLOCK:
                    actionNum = 3;
                    break;
                case BOW:
                    actionNum = 4;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid EnumAction");
            }
            buf.put(actionNum);

            buf.put((byte) (isSwingInProgress?1:0));
            buf.put((byte) (isRiding?1:0));
            buf.put((byte) (isElytraFlying?1:0));
            buf.put((byte) (isPlayerSleeping?1:0));


            return buf.array();

        }

        public void update(ByteBuffer buf){
            time = buf.getLong();

            posX = buf.getDouble();
            posY = buf.getDouble();
            posZ = buf.getDouble();
            rotationYaw = buf.getFloat();
            rotationPitch = buf.getFloat();

            isSneaking = buf.get() == 1;
            isBurning = buf.get() == 1;
            ishurt = buf.get() == 1;
            hasPotion = buf.get() == 1;

            action = EnumAction.values()[buf.get()];

            isSwingInProgress = buf.get() == 1;
            isRiding = buf.get() == 1;
            isElytraFlying = buf.get() == 1;
            isPlayerSleeping = buf.get() == 1;

        }
    }
}
