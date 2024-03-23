package com.aureskull.zmcmod.networking.packet;

import com.aureskull.zmcmod.block.entity.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.client.InteractionHelper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class TriggerInteractionC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        ServerWorld world = player.getServerWorld();

        BlockPos playerBlockPos = buf.readBlockPos();

        server.execute(() -> {
            Direction playerDirection = InteractionHelper.getLookDirection(player);

            BlockPos checkPos = playerBlockPos.offset(playerDirection);
            BlockEntity blockEntity = world.getBlockEntity(checkPos);

            if (blockEntity instanceof SmallZombieWindowBlockEntity) {
                ((SmallZombieWindowBlockEntity) blockEntity).rebuild();
                //player.sendMessage(Text.literal("nbr planks : " + ((SmallZombieWindowBlockEntity) blockEntity).plank), false);
            }
        });
    }
}
