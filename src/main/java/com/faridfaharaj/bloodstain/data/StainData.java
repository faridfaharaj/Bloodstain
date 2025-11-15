package com.faridfaharaj.bloodstain.data;

import com.faridfaharaj.bloodstain.playerHistories.PlayerMotionRecorder;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StainData extends WorldSavedData {

    public static Map<UUID, PlayerStainData> stainDataMap = new HashMap<>();

    public static PlayerStainData getStainData(UUID uuid , World world){
        return stainDataMap.computeIfAbsent(uuid, k -> {

            PlayerStainData stainData = new PlayerStainData();

            File stainDataFile = new File(
                    new File(world.getSaveHandler().getWorldDirectory(), "data"),
                    "bloodstain_data.dat"
            );
            NBTTagCompound root;
            if (stainDataFile.exists()){
                try (FileInputStream in = new FileInputStream(stainDataFile)) {
                    root = CompressedStreamTools.readCompressed(in);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else {
                root = new NBTTagCompound();
            }

            NBTTagCompound data = root.hasKey("bloodstain_stain_data")? root.getCompoundTag("bloodstain_stain_data") :new NBTTagCompound();

            String stringID = uuid.toString();
            if(data.hasKey(stringID)){
                NBTTagCompound stainNBT = data.getCompoundTag(stringID);
                stainData.deaths = stainNBT.getInteger("deaths");
                stainData.recorder = PlayerMotionRecorder.fromBytes(stainNBT.getByteArray("recorder"));
            }else {
                stainData.recorder = new PlayerMotionRecorder();
            }

            return stainData;
        }
        );
    }

    public StainData(String name) {
        super(name);
    }

    public StainData() {
        super("bloodstain_data");
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound data = compound.hasKey("bloodstain_stain_data")? compound.getCompoundTag("bloodstain_stain_data") :new NBTTagCompound();

        for(Map.Entry<UUID, PlayerStainData> stainData : stainDataMap.entrySet()){
            NBTTagCompound nbtStainData = new NBTTagCompound();

            nbtStainData.setInteger("deaths", stainData.getValue().deaths);
            nbtStainData.setByteArray("recorder", stainData.getValue().recorder.toBytes());

            data.setTag(stainData.getKey().toString(), nbtStainData);
        }

        compound.setTag("bloodstain_stain_data", data);
        return compound;
    }


    public static class PlayerStainData{

        public int deaths = 0;
        public PlayerMotionRecorder recorder;

    }

}
