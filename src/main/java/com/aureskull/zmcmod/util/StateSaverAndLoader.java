package com.aureskull.zmcmod.util;

import com.aureskull.zmcmod.ZMCMod;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class StateSaverAndLoader extends PersistentState {
    public HashMap<UUID, PlayerData> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();

            if(playerData.getGameUUID() != null)
                playerNbt.putUuid("zmcmod.player.gameUUID", playerData.getGameUUID());

            nbt.putBoolean("zmcmod.player.ready", playerData.isReady());

            playersNbt.put(uuid.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);
        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag) {
        StateSaverAndLoader state = new StateSaverAndLoader();

        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            PlayerData playerData = new PlayerData();

            NbtCompound playerCompound = playersNbt.getCompound(key);

            if (playerCompound.contains("zmcmod.player.gameUUID", 11))
                playerData.setGameUUID(playerCompound.getUuid("zmcmod.player.gameUUID"));

            if (playerCompound.contains("zmcmod.player.ready", 1))
                playerData.setReady(playerCompound.getBoolean("zmcmod.player.ready"));

            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    private static Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::new, // If there's no 'StateSaverAndLoader' yet create one
            StateSaverAndLoader::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, ZMCMod.MOD_ID);

        // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
        // there were no actual change to any of the mods state (INCREDIBLY RARE).
        state.markDirty();

        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player) {
        StateSaverAndLoader serverState = getServerState(player.getWorld().getServer());

        // Either get the player by the uuid, or we don't have data for him yet, make a new player state
        PlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> {
            PlayerData newData = new PlayerData();
            newData.setReady(false); // Set the 'ready' attribute to false when creating new data
            return newData;
        });

        return playerState;
    }
}
