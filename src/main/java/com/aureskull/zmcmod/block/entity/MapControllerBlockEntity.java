package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.ILinkable;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.screen.mapcontroller.MapControllerScreenHandler;
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

import java.util.List;

public class MapControllerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ILinkable {
    public String mapName = "";

    private BlockPos linkedZoneController = null;

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

        if (nbt.contains("map_controller.linked_zone_controller")) {
            this.linkedZoneController = NbtHelper.toBlockPos(nbt.getCompound("map_controller.linked_zone_controller"));
        }

        if (linkedZoneController != null) {
            nbt.put("map_controller.linked_zone_controller", NbtHelper.fromBlockPos(linkedZoneController));
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        if (nbt.contains("map_controller.mapname"))
            this.mapName = nbt.getString("map_controller.mapname");

        if (nbt.contains("map_controller.linked_zone_controller")) {
            this.linkedZoneController = NbtHelper.toBlockPos(nbt.getCompound("map_controller.linked_zone_controller"));
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
        return new MapControllerScreenHandler(syncId, this);
    }

    /*public BlockPos getLinkedZoneController() {
        return linkedZoneController;
    }

    public void setLinkedZoneController(BlockPos linkedZoneController) {
        this.linkedZoneController = linkedZoneController;
        markDirty();
    }*/

    /*public void unlinkExistingZoneController(World world) {
        BlockPos existingZoneController = getLinkedZoneController();
        if (existingZoneController != null) {
            BlockEntity existingZoneControllerEntity = world.getBlockEntity(existingZoneController);
            if (existingZoneControllerEntity instanceof ZoneControllerBlockEntity) {
                ((ZoneControllerBlockEntity) existingZoneControllerEntity).setLinkedMapController(null);
                existingZoneControllerEntity.markDirty();

                ModMessages.sendRemoveLinkPacket(world, existingZoneController);
            }
        }
    }*/

    @Override
    public void unlink(World world, Class<? extends BlockEntity> linkType) {
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType) && linkedZoneController != null){
            unlinkZone(world);
        }
    }

    private void unlinkZone(World world){
        ModMessages.sendRemoveLinkPacket(world, this.getLinkedBlock(ZoneControllerBlockEntity.class));

        //Remove from MapController the zone
        BlockEntity zoneControllerBE = world.getBlockEntity(this.getLinkedBlock(ZoneControllerBlockEntity.class));
        if(zoneControllerBE instanceof ZoneControllerBlockEntity)
            ((ZoneControllerBlockEntity) zoneControllerBE).setLinkedBlock(null, MapControllerBlockEntity.class);

        setLinkedBlock(null, ZoneControllerBlockEntity.class);
    }

    @Override
    public void setLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType)){
            this.linkedZoneController = linkedBlockPos;
        }
        markDirty();
    }

    @Override
    public @Nullable BlockPos getLinkedBlock(Class<? extends BlockEntity> linkType) {
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType))
            return this.linkedZoneController;

        return null;
    }

    @Override
    public void addLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {

    }

    @Override
    public void removeLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {

    }

    @Override
    public List<BlockPos> getAllLinkedBlocks(Class<? extends BlockEntity> linkType) {
        return null;
    }
}
