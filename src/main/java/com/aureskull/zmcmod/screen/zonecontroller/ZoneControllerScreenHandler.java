package com.aureskull.zmcmod.screen.zonecontroller;

import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
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

public class ZoneControllerScreenHandler extends ScreenHandler {
    public final ZoneControllerBlockEntity zoneControllerBlockEntity;

    public ZoneControllerScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    public ZoneControllerScreenHandler(int syncId, BlockEntity blockEntity) {
        super(ModScreenHandlers.ZONE_CONTROLLER_SCREEN_HANDLER, syncId);
        this.zoneControllerBlockEntity = ((ZoneControllerBlockEntity) blockEntity);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    private void updateZoneColorOnServer(float newColor, String colorVariable) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(this.zoneControllerBlockEntity.getPos());
        buf.writeFloat(newColor);
        buf.writeString(colorVariable);
        ClientPlayNetworking.send(ModMessages.ZONE_CONTROLLER_UPDATE_ZONE_COLOR, buf);
    }

    public void updateZoneColor(float newColor, String colorVariable) {
        updateZoneColorOnServer(newColor, colorVariable);

        switch (colorVariable){
            case("red"):
                zoneControllerBlockEntity.setRed(newColor);
                break;
            case("green"):
                zoneControllerBlockEntity.setGreen(newColor);
                break;
            case("blue"):
                zoneControllerBlockEntity.setBlue(newColor);
                break;
        }

        zoneControllerBlockEntity.markDirty();
    }

    private void updatePosOnServer(BlockPos newPos, String posVariable) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(this.zoneControllerBlockEntity.getPos());
        buf.writeBlockPos(newPos);
        buf.writeString(posVariable);
        ClientPlayNetworking.send(ModMessages.ZONE_CONTROLLER_UPDATE_POS, buf);
    }

    public void updatePos(BlockPos newPos, String posVariable){
        updatePosOnServer(newPos, posVariable);

        if(posVariable.equals("posA"))
            zoneControllerBlockEntity.setPosA(newPos);
        else if(posVariable.equals("posB"))
            zoneControllerBlockEntity.setPosB(newPos);
        else if(posVariable.equals("spawnPos"))
            zoneControllerBlockEntity.setSpawnPoint(newPos);

        zoneControllerBlockEntity.markDirty();
    }
}
