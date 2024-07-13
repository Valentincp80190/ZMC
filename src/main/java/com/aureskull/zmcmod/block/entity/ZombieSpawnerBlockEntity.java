package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.block.ILinkable;
import com.aureskull.zmcmod.block.entity.window.SmallZombieWindowBlockEntity;
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

import java.util.ArrayList;
import java.util.List;

public class ZombieSpawnerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ILinkable {

    private BlockPos linkedWindowPos;

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
        if (linkedWindowPos != null)
            nbt.put("linked_window", NbtHelper.fromBlockPos(linkedWindowPos));

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        if (nbt.contains("linked_window"))
            this.linkedWindowPos =  NbtHelper.toBlockPos(nbt.getCompound("linked_window"));

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

    public BlockPos getLinkedWindow() {
        return this.linkedWindowPos;
    }

    public BlockPos getMapControllerBlockEntityPos() {
        if (getLinkedWindow() != null && world.getBlockEntity(getLinkedWindow()) instanceof SmallZombieWindowBlockEntity window) {
            List<BlockPos> zoneBP = window.getLink(ZoneControllerBlockEntity.class);

            if (world.getBlockEntity(zoneBP.get(0)) instanceof ZoneControllerBlockEntity zoneBE)
                return zoneBE.findMapControllerRecursively(zoneBE, new ArrayList<BlockPos>());
        }
        return null; // MapControllerBlockEntity BlockPos not found
    }

    public void spawnZombie(MapControllerBlockEntity mapControllerBE) {
        if (!world.isClient && getLinkedWindow() != null) {
            // Logic to spawn the zombie
            StandingZombieEntity zombie = ModEntities.STANDING_ZOMBIE.create(world);
            zombie.setMapControllerBlockPos(mapControllerBE.getPos());
            zombie.updateSpeed();
            zombie.setWindowBlockPos(getLinkedWindow());
            zombie.setPosition(getPos().getX() + 0.5, getPos().getY() + 1, getPos().getZ() + 0.5);
            world.spawnEntity(zombie);

            mapControllerBE.setZombiesRemainingInRound(mapControllerBE.getZombiesRemainingInRound() - 1);
        }
    }

    @Override
    public void setLink(List<BlockPos> blocks, Class<? extends BlockEntity> linkType) {
        if(SmallZombieWindowBlockEntity.class.isAssignableFrom(linkType))
            linkedWindowPos = blocks.size() > 0 ? blocks.get(0) : null;
    }

    @Override
    public List<BlockPos> getLink(Class<? extends BlockEntity> linkType) {
        List<BlockPos> blockPosList = new ArrayList<>();

        if(SmallZombieWindowBlockEntity.class.isAssignableFrom(linkType)){
            if(linkedWindowPos != null) blockPosList.add(linkedWindowPos);
            return blockPosList;
        }

        return null;
    }
}