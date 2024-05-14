package com.aureskull.zmcmod.networking.packet;

import com.aureskull.zmcmod.block.entity.window.MediumZombieWindowBlockEntity;
import com.aureskull.zmcmod.block.entity.window.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.block.entity.door.DoorBlockEntity;
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

            if(blockEntity == null){//In case when we are in the block that we want to interact like a door for example
                blockEntity = world.getBlockEntity(playerBlockPos);
            }

            if (blockEntity instanceof SmallZombieWindowBlockEntity smallZombieWindowBlockEntity) {
                smallZombieWindowBlockEntity.rebuild(player);
                //player.sendMessage(Text.literal("nbr planks : " + ((SmallZombieWindowBlockEntity) mapControllerBlockEntity).plank), false);
            }

            if (blockEntity instanceof MediumZombieWindowBlockEntity mediumZombieWindowBlockEntity) {
                mediumZombieWindowBlockEntity.rebuild(player);
                //player.sendMessage(Text.literal("nbr planks : " + ((SmallZombieWindowBlockEntity) mapControllerBlockEntity).plank), false);
            }

            if (blockEntity instanceof DoorBlockEntity doorBlockEntity) {
                if(!doorBlockEntity.isOpen())
                    doorBlockEntity.buy(world, player, doorBlockEntity.getPrice());
            }
        });
    }
}
