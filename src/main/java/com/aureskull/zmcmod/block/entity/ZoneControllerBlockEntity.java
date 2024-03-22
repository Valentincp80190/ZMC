package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.screen.zonecontroller.ZoneControllerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
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

public class ZoneControllerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    public BlockPos posA = new BlockPos(pos.getX() - 5, pos.getY() + 1, pos.getZ() - 5);
    public BlockPos posB = new BlockPos(pos.getX() + 5, pos.getY() + 5, pos.getZ() + 5);

    public float red = 1f;
    public float green = 1f;
    public float blue = 1f;

    private BlockPos linkedMapController = null;
    private BlockPos linkedZoneController = null;

    private List<BlockPos> linkedDoorways = new ArrayList<>();

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

        if (linkedMapController != null) {
            nbt.put("zone_controller.linked_map_controller", NbtHelper.fromBlockPos(linkedMapController));
        }

        if (linkedZoneController != null) {
            nbt.put("zone_controller.linked_zone_controller", NbtHelper.fromBlockPos(linkedZoneController));
        }

        NbtList doorwaysList = new NbtList();
        for (BlockPos pos : linkedDoorways)
            doorwaysList.add(NbtHelper.fromBlockPos(pos));
        if(doorwaysList.size() > 0)
            nbt.put("zone_controller.linked_doorways", doorwaysList);

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

        if (nbt.contains("zone_controller.posa.x", 99) ||
                nbt.contains("zone_controller.posa.y", 99) ||
                nbt.contains("zone_controller.posa.z", 99)) { // The '99' checks for any numeric tag type
            this.posA = new BlockPos(
                    nbt.getInt("zone_controller.posa.x"),
                    nbt.getInt("zone_controller.posa.y"),
                    nbt.getInt("zone_controller.posa.z"));
        }

        if (nbt.contains("zone_controller.posb.x", 99) ||
                nbt.contains("zone_controller.posb.y", 99) ||
                nbt.contains("zone_controller.posb.z", 99)) { // The '99' checks for any numeric tag type
            this.posB = new BlockPos(
                    nbt.getInt("zone_controller.posb.x"),
                    nbt.getInt("zone_controller.posb.y"),
                    nbt.getInt("zone_controller.posb.z"));
        }

        if (nbt.contains("zone_controller.linked_map_controller")) {
            this.linkedMapController = NbtHelper.toBlockPos(nbt.getCompound("zone_controller.linked_map_controller"));
        }

        if (nbt.contains("zone_controller.linked_zone_controller")) {
            this.linkedZoneController = NbtHelper.toBlockPos(nbt.getCompound("zone_controller.linked_zone_controller"));
        }

        NbtList doorwaysList = nbt.getList("zone_controller.linked_doorways", 10);
        for (int i = 0; i < doorwaysList.size(); i++) {
            linkedDoorways.add(NbtHelper.toBlockPos(doorwaysList.getCompound(i)));
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
        return new ZoneControllerScreenHandler(syncId, this);
    }

    public BlockPos getLinkedMapController() {
        return this.linkedMapController;
    }

    public void setLinkedMapController(BlockPos pos) {
        this.linkedMapController = pos;
        markDirty();
    }

    public List<BlockPos> getLinkedDoorway() {
        return linkedDoorways;
    }


    public void unlinkExistingMapController(World world) {
        BlockPos existingMapController = getLinkedMapController();
        if (existingMapController != null) {
            BlockEntity existingMapControllerBE = world.getBlockEntity(existingMapController);
            if (existingMapControllerBE instanceof MapControllerBlockEntity) {
                ((MapControllerBlockEntity) existingMapControllerBE).setLinkedZoneController(null);
                existingMapControllerBE.markDirty();

                ModMessages.sendRemoveLinkPacket(world, existingMapController);
            }
        }
    }

    public void unlinkAllExistingDoorway(World world) {
        List<BlockPos> existingDoorway = getLinkedDoorway();

        for(BlockPos doorway : existingDoorway) {
            BlockEntity doorwayBE = world.getBlockEntity(doorway);
            if (doorwayBE instanceof SmallZombieDoorwayBlockEntity) {
                ((SmallZombieDoorwayBlockEntity) doorwayBE).setLinkedZonePos(null);
                doorwayBE.markDirty();

                ModMessages.sendRemoveZoneLinkFromDoorwayPacket(world, doorway);
            }
        }
    }

    public void removeLinkedDoorway(BlockPos doorwayPos) {
        if(this.linkedDoorways.contains(doorwayPos)){
            this.linkedDoorways.remove(doorwayPos);
            markDirty();
        }
    }

    public void addLinkedDoorway(BlockPos doorwayPos){
        this.linkedDoorways.add(doorwayPos);
        markDirty();
    }
}