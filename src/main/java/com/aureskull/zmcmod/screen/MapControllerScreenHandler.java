package com.aureskull.zmcmod.screen;

import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.networking.ModMessages;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;

public class MapControllerScreenHandler extends ScreenHandler {
    public final MapControllerBlockEntity blockEntity;

    public MapControllerScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    public MapControllerScreenHandler(int syncId, BlockEntity blockEntity) {
        super(ModScreenHandlers.MAP_CONTROLLER_SCREEN_HANDLER, syncId);
        this.blockEntity = ((MapControllerBlockEntity) blockEntity);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public void updateMapName(String newMapName) {
        updateMapNameOnServer(newMapName);

        this.blockEntity.mapName = newMapName;
        this.blockEntity.markDirty();
    }

    public void updateMapNameOnServer(String newMapName) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(newMapName);
        buf.writeBlockPos(this.blockEntity.getPos());
        ClientPlayNetworking.send(ModMessages.MAP_CONTROLLER_UPDATE_MAP_NAME, buf);
    }

    public String getMapName(){
        return this.blockEntity.mapName;
    }
}
