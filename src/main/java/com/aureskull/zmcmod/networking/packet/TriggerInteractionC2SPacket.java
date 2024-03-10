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
            boolean found = false;

            Direction playerDirection = InteractionHelper.getLookDirection(player);

            BlockPos checkPos = playerBlockPos.offset(playerDirection);
            BlockEntity blockEntity = world.getBlockEntity(checkPos);

            if (blockEntity instanceof SmallZombieDoorwayBlockEntity) {
                found = true;
                ((SmallZombieDoorwayBlockEntity) blockEntity).rebuild();
                player.sendMessage(Text.literal("nbr planks : " + ((SmallZombieDoorwayBlockEntity) blockEntity).plank), false);
            }
            /*boolean found = false;
            Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

            for (Direction direction : directions) {
                BlockPos checkPos = playerBlockPos.offset(direction);
                BlockEntity blockEntity = world.getBlockEntity(checkPos);

                if (blockEntity instanceof SmallZombieDoorwayBlockEntity) {
                    // An instance of SmallZombieDoorwayBlockEntity was found within one block distance in one of the cardinal directions
                    found = true;
                    // Perform your interaction logic here...
                    player.sendMessage(Text.literal("Repair"), false);
                    //send packet to client to display message
                    break; // Stop checking further if we've found an instance
                }
            }*/

            if (!found) {
                player.sendMessage(Text.literal("Nothing found"), false);
            }
        });
    }
}
