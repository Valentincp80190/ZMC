package com.aureskull.zmcmod.networking.packet;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.networking.ModMessages;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class MapControllerUpdateMapNameC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        String newMapName = buf.readString();
        BlockPos blockPos = buf.readBlockPos();

        server.execute(() -> {
            MapControllerBlockEntity blockEntity = (MapControllerBlockEntity) player.getWorld().getBlockEntity(blockPos);

            if (blockEntity != null) {
                blockEntity.mapName = newMapName;
                blockEntity.markDirty();
            }
        });
    }
}
