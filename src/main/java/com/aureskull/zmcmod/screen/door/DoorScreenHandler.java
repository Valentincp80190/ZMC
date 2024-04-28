package com.aureskull.zmcmod.screen.door;

import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.door.DoorBlockEntity;
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

public class DoorScreenHandler extends ScreenHandler {
    public final DoorBlockEntity doorBlockEntity;

    public DoorScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    public DoorScreenHandler(int syncId, BlockEntity blockEntity) {
        super(ModScreenHandlers.DOOR_SCREEN_HANDLER, syncId);
        this.doorBlockEntity = ((DoorBlockEntity) blockEntity);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public void updatePrice(int price){
        if(price > 0){
            updatePriceOnServer(price);
        }
    }

    private void updatePriceOnServer(int price) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(doorBlockEntity.getPos());
        buf.writeInt(price);
        ClientPlayNetworking.send(ModMessages.DOOR_UPDATE_PRICE, buf);
    }
}
