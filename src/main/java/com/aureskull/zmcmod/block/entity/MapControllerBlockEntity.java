package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.screen.MapControllerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MapControllerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    public String mapName = "";

    public MapControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAP_CONTROLLER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Map Controller");
    }

    @Override
    protected void writeNbt(NbtCompound nbt){
        nbt.putString("map_controller.mapname", this.mapName);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        if (nbt.contains("map_controller.mapname"))
            this.mapName = nbt.getString("map_controller.mapname");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(world.isClient()) {
            return;
        }
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MapControllerScreenHandler(syncId, this);
    }
}
