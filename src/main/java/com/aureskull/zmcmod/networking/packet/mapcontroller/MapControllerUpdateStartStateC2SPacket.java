package com.aureskull.zmcmod.networking.packet.mapcontroller;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.util.PlayerData;
import com.aureskull.zmcmod.util.StateSaverAndLoader;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class MapControllerUpdateStartStateC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        Boolean newStartState = buf.readBoolean();
        BlockPos blockPos = buf.readBlockPos();

        server.execute(() -> {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(blockPos);

            if(blockEntity instanceof MapControllerBlockEntity) {
                MapControllerBlockEntity mapController = (MapControllerBlockEntity) player.getWorld().getBlockEntity(blockPos);
                mapController.setStart(newStartState, player);
            }
        });
    }
}
