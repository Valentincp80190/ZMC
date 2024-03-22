package com.aureskull.zmcmod.networking.packet.link;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.SmallZombieDoorwayBlockEntity;
import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class RemoveDoorwayLinkFromZoneS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
        BlockPos zonePos = buf.readBlockPos();
        BlockPos doorwayPos = buf.readBlockPos();
        ZMCMod.LOGGER.info("Packet received on client: ZonePos = " + zonePos + ", DoorwayPos = " + doorwayPos);

        client.execute(() -> {
            if (client.world != null) {
                BlockEntity zoneEntity = client.world.getBlockEntity(zonePos);
                BlockEntity doorwayEntity = client.world.getBlockEntity(doorwayPos);

                ZMCMod.LOGGER.info("Executing on main thread.");
                if (zoneEntity != null) {
                    ZMCMod.LOGGER.info("Zone Entity Class: " + zoneEntity.getClass().getSimpleName());
                } else {
                    ZMCMod.LOGGER.info("Zone Entity not found at: " + zonePos);
                }

                if (doorwayEntity != null) {
                    ZMCMod.LOGGER.info("Doorway Entity Class: " + doorwayEntity.getClass().getSimpleName());
                } else {
                    ZMCMod.LOGGER.info("Doorway Entity not found at: " + doorwayPos);
                }

                if (zoneEntity instanceof ZoneControllerBlockEntity) {
                    ZMCMod.LOGGER.info("Correct entities found, updating links.");
                    ZoneControllerBlockEntity zoneBE = (ZoneControllerBlockEntity) zoneEntity;
                    zoneBE.removeLinkedDoorway(doorwayPos);

                    // Additional logic to confirm changes, if applicable
                    // This could be checking if the doorwayPos is indeed removed from the zoneBE's list
                    boolean linkRemoved = zoneBE.getLinkedDoorways().contains(doorwayPos);
                    ZMCMod.LOGGER.info("Link removal status: " + !linkRemoved);
                } else {
                    ZMCMod.LOGGER.info("Entities are not instances of expected BlockEntity classes.");
                }
            }
        });
    }

}
