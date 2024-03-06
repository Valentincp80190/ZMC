package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.screen.zonecontroller.ZoneControllerScreenHandler;
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

public class ZoneControllerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    public BlockPos posA = new BlockPos(5, -56, -4);
    public BlockPos posB = new BlockPos(12, -53, -11);

    public float red = 1f;
    public float green = 1f;
    public float blue = 1f;

    public ZoneControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZONE_CONTROLLER_BLOCK_ENTITY, pos, state);
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
        return Text.literal("Zone Controller");
    }

    @Override
    protected void writeNbt(NbtCompound nbt){
        nbt.putFloat("zone_controller.red", red);
        nbt.putFloat("zone_controller.green", green);
        nbt.putFloat("zone_controller.blue", blue);

        nbt.putFloat("zone_controller.posa.x", posA.getX());
        nbt.putFloat("zone_controller.posa.y", posA.getY());
        nbt.putFloat("zone_controller.posa.z", posA.getZ());

        nbt.putFloat("zone_controller.posb.x", posB.getX());
        nbt.putFloat("zone_controller.posb.y", posB.getY());
        nbt.putFloat("zone_controller.posb.z", posB.getZ());
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);

        if (nbt.contains("zone_controller.red"))
            this.red = nbt.getFloat("zone_controller.red");

        if (nbt.contains("zone_controller.green"))
            this.green = nbt.getFloat("zone_controller.green");

        if (nbt.contains("zone_controller.blue"))
            this.blue = nbt.getFloat("zone_controller.blue");

        if (nbt.contains("zone_controller.posa.x") &&
                nbt.contains("zone_controller.posa.y") &&
                nbt.contains("zone_controller.posa.z"))
            this.posA = new BlockPos((int)nbt.getFloat("zone_controller.posa.x"),
                                     (int)nbt.getFloat("zone_controller.posa.y"),
                                     (int)nbt.getFloat("zone_controller.posa.z"));

        if (nbt.contains("zone_controller.posb.x") &&
                nbt.contains("zone_controller.posb.y") &&
                nbt.contains("zone_controller.posb.z"))
            this.posB = new BlockPos((int)nbt.getFloat("zone_controller.posb.x"),
                                     (int)nbt.getFloat("zone_controller.posb.y"),
                                     (int)nbt.getFloat("zone_controller.posb.z"));
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
        return new ZoneControllerScreenHandler(syncId, this);
    }
}