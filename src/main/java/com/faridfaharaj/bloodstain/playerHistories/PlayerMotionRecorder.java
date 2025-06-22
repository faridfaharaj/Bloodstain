package com.faridfaharaj.bloodstain.playerHistories;

import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMotionRecorder {

    public static Map<UUID, PlayerMotionRecorder> recorders = new HashMap<>();

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

    public static class PlayerMotionSnapshot {
        public long time;
        public double posX, posY, posZ;
        public double motionX, motionY, motionZ;
        public float rotationYaw, rotationPitch;

        public PlayerMotionSnapshot() {
            this.posX = 0;
            this.posY = 0;
            this.posZ = 0;
            this.motionX = 0;
            this.motionY = 0;
            this.motionZ = 0;
            this.rotationYaw = 0;
            this.rotationPitch = 0;

            this.time = 0;
        }

        public void update(PlayerMotionSnapshot snapshot) {
            this.posX = snapshot.posX;
            this.posY = snapshot.posY;
            this.posZ = snapshot.posZ;
            this.motionX = snapshot.motionX;
            this.motionY = snapshot.motionY;
            this.motionZ = snapshot.motionZ;
            this.rotationYaw = snapshot.rotationYaw;
            this.rotationPitch = snapshot.rotationPitch;

            this.time = snapshot.time;
        }

        public void update (EntityPlayer player){
            this.posX = player.posX;
            this.posY = player.posY;
            this.posZ = player.posZ;
            this.motionX = player.motionX;
            this.motionY = player.motionY;
            this.motionZ = player.motionZ;
            this.rotationYaw = player.rotationYaw;
            this.rotationPitch = player.rotationPitch;

            this.time = System.currentTimeMillis();
        }
    }
}
