package com.aureskull.zmcmod.networking.packet;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.SmallZombieDoorwayBlockEntity;
import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import com.aureskull.zmcmod.client.InteractionHelper;
import com.aureskull.zmcmod.client.MessageHudOverlay;
import com.aureskull.zmcmod.networking.ModMessages;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class TriggerInteractionC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        ServerWorld world = player.getServerWorld();

        BlockPos playerBlockPos = buf.readBlockPos();

        server.execute(() -> {
            Direction playerDirection = InteractionHelper.getLookDirection(player);

            BlockPos checkPos = playerBlockPos.offset(playerDirection);
            BlockEntity blockEntity = world.getBlockEntity(checkPos);

            if (blockEntity instanceof SmallZombieDoorwayBlockEntity) {
                ((SmallZombieDoorwayBlockEntity) blockEntity).rebuild();
                player.sendMessage(Text.literal("nbr planks : " + ((SmallZombieDoorwayBlockEntity) blockEntity).plank), false);
            }
        });
    }
}
