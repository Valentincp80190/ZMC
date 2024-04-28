package com.aureskull.zmcmod.networking.packet.door;

import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.door.DoorBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class DoorUpdatePriceC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        BlockPos blockPos = buf.readBlockPos();
        int newPrice = buf.readInt();

        server.execute(() -> {
            DoorBlockEntity blockEntity = (DoorBlockEntity) player.getWorld().getBlockEntity(blockPos);

            if (blockEntity != null) {
                blockEntity.getMasterBlockEntity().setPrice(newPrice);
                blockEntity.markDirty();
            }
        });
    }
}
