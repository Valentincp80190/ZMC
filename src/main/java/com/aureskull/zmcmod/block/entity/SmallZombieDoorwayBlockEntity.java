package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.block.custom.SmallZombieDoorwayBlock;
import com.aureskull.zmcmod.client.InteractionHelper;
import com.aureskull.zmcmod.client.MessageHudOverlay;
import com.aureskull.zmcmod.event.ModKeyInputHandler;
import com.aureskull.zmcmod.screen.mapcontroller.MapControllerScreenHandler;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

    public class SmallZombieDoorwayBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
        public final int MAX_PLANK = 6;
        public int plank = 0;
    
        public SmallZombieDoorwayBlockEntity(BlockPos pos, BlockState state) {
            super(ModBlockEntities.SMALL_ZOMBIE_DOORWAY_BLOCK_ENTITY, pos, state);
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
            return Text.literal("Small Zombie Doorway");
        }
    
        @Override
        protected void writeNbt(NbtCompound nbt){
            nbt.putInt("small_zombie_doorway.plank", plank);
            super.writeNbt(nbt);
        }
    
        @Override
        public void readNbt(NbtCompound nbt){
            super.readNbt(nbt);
            if (nbt.contains("small_zombie_doorway.plank"))
                this.plank = nbt.getInt("small_zombie_doorway.plank");
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
            if(plank < MAX_PLANK){
                Direction facing = state.get(SmallZombieDoorwayBlock.FACING);
                Box searchArea = getSearchArea(pos, facing);
    
                List<PlayerEntity> players = world.getNonSpectatingEntities(PlayerEntity.class, searchArea);
                for (PlayerEntity player : players) {
                    if(InteractionHelper.isFacingInteractable(player, facing) &&
                        Math.abs(player.getY() - pos.getY()) <= 1.5) {
                            MessageHudOverlay.setMessage("Press [" + ModKeyInputHandler.INTERACT.getBoundKeyLocalizedText().getLiteralString() + "] to Rebuild Barrier", 100);
    
                    }
                }
            }
        }

        private Box getSearchArea(BlockPos pos, Direction facing) {
            // This creates a search area in front of the block based on its facing direction.
            switch (facing) {
                case NORTH:
                    return new Box(pos.north()).expand(0, 1, -.5f); // Adjust these values as needed
                case SOUTH:
                    return new Box(pos.south()).expand(0, 1, -.5f);
                case EAST:
                    return new Box(pos.east()).expand(-.5f, 1, 0);
                case WEST:
                    return new Box(pos.west()).expand(-.5f, 1, 0);
                default:
                    return new Box(pos); // Default case, should not happen for horizontal directions
            }
        }
    
        @Nullable
        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
            return null;
        }

        public void rebuild(){
            if(plank < MAX_PLANK){
                plank++;
                markDirty();
            }
        }
    }