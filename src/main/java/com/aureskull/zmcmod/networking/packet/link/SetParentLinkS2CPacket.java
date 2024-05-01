package com.aureskull.zmcmod.networking.packet.link;

import com.aureskull.zmcmod.block.BlockEntityType;
import com.aureskull.zmcmod.block.ILinkable;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SetParentLinkS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
        BlockPos sourceBlockPos = buf.readBlockPos();
        int classIdentifier = buf.readInt();
        Class<? extends BlockEntity> linkType = BlockEntityType.values()[classIdentifier].getBlockEntityClass();

        List<BlockPos> newblockPosLinkList = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            newblockPosLinkList.add(buf.readBlockPos());
        }

        client.execute(() -> {
            if (client.world != null) {
                BlockEntity blockEntity = client.world.getBlockEntity(sourceBlockPos);

                if(blockEntity instanceof ILinkable)
                    ((ILinkable) blockEntity).setParentLink(newblockPosLinkList, linkType);
            }
        });
    }
}
