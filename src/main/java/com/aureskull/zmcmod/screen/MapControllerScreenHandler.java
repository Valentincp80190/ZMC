package com.aureskull.zmcmod.screen;

import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;

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
        MinecraftClient.getInstance().execute(() -> {
            this.blockEntity.mapName = newMapName;
            this.blockEntity.markDirty();
        });
    }

    public String getMapName(){
        return this.blockEntity.mapName;
    }
}
