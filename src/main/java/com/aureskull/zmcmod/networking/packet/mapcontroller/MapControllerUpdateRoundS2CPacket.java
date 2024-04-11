package com.aureskull.zmcmod.networking.packet.mapcontroller;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.client.RoundOverlay;
import com.aureskull.zmcmod.sound.ModSounds;
import com.aureskull.zmcmod.util.PlayerData;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class MapControllerUpdateRoundS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender){
        BlockPos blockPos = buf.readBlockPos();
        int newRound = buf.readInt();

        client.execute(() -> {
            BlockEntity blockEntity = client.world.getBlockEntity(blockPos);

            if(blockEntity instanceof MapControllerBlockEntity) {
                MapControllerBlockEntity mapController = (MapControllerBlockEntity) client.world.getBlockEntity(blockPos);
                mapController.setRound(newRound);

                if(newRound > 0)
                    client.player.playSound(ModSounds.ROUND_START, SoundCategory.AMBIENT, 0.5f, 1.0f);
            }
        });
    }
}
