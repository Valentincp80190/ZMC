package com.aureskull.zmcmod.screen.mapcontroller;

import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.screen.ModScreenHandlers;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class MapControllerScreenHandler extends ScreenHandler {
    public final MapControllerBlockEntity mapControllerBlockEntity;

    public MapControllerScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    public MapControllerScreenHandler(int syncId, BlockEntity blockEntity) {
        super(ModScreenHandlers.MAP_CONTROLLER_SCREEN_HANDLER, syncId);
        this.mapControllerBlockEntity = ((MapControllerBlockEntity) blockEntity);
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

        this.mapControllerBlockEntity.setMapName(newMapName);
        this.mapControllerBlockEntity.markDirty();
    }

    public void updateMapNameOnServer(String newMapName) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(newMapName);
        buf.writeBlockPos(this.mapControllerBlockEntity.getPos());
        ClientPlayNetworking.send(ModMessages.MAP_CONTROLLER_UPDATE_MAP_NAME, buf);
    }

    public void updateStartGameOnServer(Boolean isStarted) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(isStarted);
        buf.writeBlockPos(this.mapControllerBlockEntity.getPos());
        ClientPlayNetworking.send(ModMessages.MAP_CONTROLLER_UPDATE_START_STATE, buf);
    }

    private void updatePosOnServer(BlockPos newPos, String posVariable) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(this.mapControllerBlockEntity.getPos());
        buf.writeBlockPos(newPos);
        buf.writeString(posVariable);
        ClientPlayNetworking.send(ModMessages.MAP_CONTROLLER_UPDATE_POS, buf);
    }

    public void updatePos(BlockPos newPos, String posVariable){
        updatePosOnServer(newPos, posVariable);

        if(posVariable.equals("posA"))
            mapControllerBlockEntity.setPosA(newPos);
        else if(posVariable.equals("posB"))
            mapControllerBlockEntity.setPosB(newPos);

        mapControllerBlockEntity.markDirty();
    }
}
