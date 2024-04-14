package com.aureskull.zmcmod.networking.packet.mapcontroller;

import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class MapControllerUpdatePosC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        BlockPos blockPos = buf.readBlockPos();
        BlockPos newPos = buf.readBlockPos();
        String posVariable = buf.readString();

        server.execute(() -> {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(blockPos);

            if (blockEntity != null && blockEntity instanceof MapControllerBlockEntity mapControllerBlockEntity) {
                if(new String(posVariable).equals("posA"))
                    mapControllerBlockEntity.setPosA(newPos);

                else if(new String(posVariable).equals("posB"))
                    mapControllerBlockEntity.setPosB(newPos);

                blockEntity.markDirty();
            }
        });
    }
}