package com.aureskull.zmcmod.management;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamesManager {
    private static GamesManager instance;

    private final Map<UUID, GameInfo> games = new HashMap<>();

    public static synchronized GamesManager getInstance() {
        if (instance == null) {
            instance = new GamesManager();
        }
        return instance;
    }

    private GamesManager() {}

    public void addGame(UUID gameUUID, BlockPos blockPos, RegistryKey<World> worldKey) {
        games.put(gameUUID, new GameInfo(gameUUID, blockPos, worldKey));
    }

    public GameInfo getGame(UUID gameUUID) {
        return games.get(gameUUID);
    }

    public void removeGame(UUID gameUUID) {
        games.remove(gameUUID);
    }

    public class GameInfo {
        private final UUID gameUUID;
        private final BlockPos blockPos;
        private final RegistryKey<World> worldKey;

        public GameInfo(UUID gameUUID, BlockPos blockPos, RegistryKey<World> worldKey) {
            this.gameUUID = gameUUID;
            this.blockPos = blockPos;
            this.worldKey = worldKey;
        }

        public UUID getGameUUID() {
            return gameUUID;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public RegistryKey<World> getWorldKey() {
            return worldKey;
        }

        public World getWorld(MinecraftServer server) {
            return server.getWorld(worldKey);
        }
    }
}