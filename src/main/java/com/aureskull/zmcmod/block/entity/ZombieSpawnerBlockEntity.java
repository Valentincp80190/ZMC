package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.entity.ModEntities;
import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
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

public class ZombieSpawnerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {

    private BlockPos linkedDoorwayPos;

    public ZombieSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZOMBIE_SPAWNER_BLOCK_ENTITY, pos, state);
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
        return Text.literal("Zombie Spawner");
    }

    @Override
    protected void writeNbt(NbtCompound nbt){
        if (linkedDoorwayPos != null) {
            nbt.put("linked_doorway", NbtHelper.fromBlockPos(linkedDoorwayPos));
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        if (nbt.contains("linked_doorway")) {
            this.linkedDoorwayPos = NbtHelper.toBlockPos(nbt.getCompound("linked_doorway"));
        }
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
        return null;
    }

    public void setLinkedDoorway(BlockPos pos) {
        this.linkedDoorwayPos = pos;
        this.markDirty();
    }

    public BlockPos getLinkedDoorway() {
        return this.linkedDoorwayPos;
    }

    public void spawnZombie(BlockPos spawnerPos) {
        if (!world.isClient && linkedDoorwayPos != null) {
            // Logic to spawn the zombie
            StandingZombieEntity zombie = ModEntities.STANDING_ZOMBIE.create(world);
            if (zombie != null) {
                zombie.setPosition(spawnerPos.getX() + 0.5, spawnerPos.getY() + 1, spawnerPos.getZ() + 0.5);
                world.spawnEntity(zombie);

                // Direct the zombie to the linked doorway
                zombie.getNavigation().startMovingTo(
                        linkedDoorwayPos.getX() + 0.5,
                        linkedDoorwayPos.getY(),
                        linkedDoorwayPos.getZ() + 0.5,
                        1.0);
            }
        }
    }
}