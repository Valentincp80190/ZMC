package com.aureskull.zmcmod.item.custom;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.custom.MapControllerBlock;
import com.aureskull.zmcmod.block.custom.SmallZombieDoorwayBlock;
import com.aureskull.zmcmod.block.custom.ZombieSpawnerBlock;
import com.aureskull.zmcmod.block.custom.ZoneControllerBlock;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.SmallZombieDoorwayBlockEntity;
import com.aureskull.zmcmod.block.entity.ZombieSpawnerBlockEntity;
import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import com.aureskull.zmcmod.networking.ModMessages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Linker extends Item {
    //shift + use = clear item pos
    //Shift + use sur un item lickable = retrait du lien

    public Linker(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ZMCMod.LOGGER.info("Triggered");
        World world = context.getWorld();
        PlayerEntity playerEntity = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        ItemStack itemStack = context.getStack();

        if (!world.isClient() && playerEntity != null) {//Server Side
            // Check if the player is sneaking to clear the stored positions
            if (playerEntity.isSneaking()) {
                clearStoredPositions(itemStack, playerEntity);
                return ActionResult.SUCCESS;
            }

            BlockState state = world.getBlockState(pos);
            ZMCMod.LOGGER.info(state.getBlock().toString());
            if (state.getBlock() instanceof ZombieSpawnerBlock
                    || state.getBlock() instanceof SmallZombieDoorwayBlock
                    || state.getBlock() instanceof MapControllerBlock
                    || state.getBlock() instanceof ZoneControllerBlock) {
                handleBlockLinking(world, pos, itemStack, playerEntity);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    private void handleBlockLinking(World world, BlockPos pos, ItemStack itemStack, PlayerEntity player) {
        NbtCompound tag = itemStack.getOrCreateNbt();

        if (!tag.contains("FirstPos")) {
            tag.put("FirstPos", NbtHelper.fromBlockPos(pos));
        } else {
            // Retrieve the first position and link with the second position
            BlockPos firstPos = NbtHelper.toBlockPos(tag.getCompound("FirstPos"));
            linkBlocks(world, firstPos, pos);
            tag.remove("FirstPos"); // Clear after linking
        }
    }

    private void clearStoredPositions(ItemStack itemStack, PlayerEntity player) {
        NbtCompound tag = itemStack.getOrCreateNbt();
        if (tag.contains("FirstPos")) {
            tag.remove("FirstPos");
        }
    }

    private void linkBlocks(World world, BlockPos firstPos, BlockPos secondPos) {
        BlockEntity firstEntity = world.getBlockEntity(firstPos);
        BlockEntity secondEntity = world.getBlockEntity(secondPos);

        //SmallZombieDoorway <=> ZombieSpawner
        if (firstEntity instanceof ZombieSpawnerBlockEntity && secondEntity instanceof SmallZombieDoorwayBlockEntity) {
            ((ZombieSpawnerBlockEntity) firstEntity).unlinkExistingDoorway(world);
            ((SmallZombieDoorwayBlockEntity) secondEntity).unlinkExistingZombieSpawner(world);

            ((ZombieSpawnerBlockEntity) firstEntity).setLinkedDoorway(secondPos);
            ((SmallZombieDoorwayBlockEntity) secondEntity).setLinkedSpawner(firstPos);

        } else if (firstEntity instanceof SmallZombieDoorwayBlockEntity && secondEntity instanceof ZombieSpawnerBlockEntity) {
            ((SmallZombieDoorwayBlockEntity) firstEntity).unlinkExistingZombieSpawner(world);
            ((ZombieSpawnerBlockEntity) secondEntity).unlinkExistingDoorway(world);

            ((SmallZombieDoorwayBlockEntity) firstEntity).setLinkedSpawner(secondPos);
            ((ZombieSpawnerBlockEntity) secondEntity).setLinkedDoorway(firstPos);
        }


        //ZoneController <=> MapController
        else if (firstEntity instanceof MapControllerBlockEntity && secondEntity instanceof ZoneControllerBlockEntity) {
            ((MapControllerBlockEntity) firstEntity).unlinkExistingZoneController(world);
            ((ZoneControllerBlockEntity) secondEntity).unlinkExistingMapController(world);

            ((MapControllerBlockEntity) firstEntity).setLinkedZoneController(secondPos);
            ((ZoneControllerBlockEntity) secondEntity).setLinkedMapController(firstPos);

        } else if (firstEntity instanceof ZoneControllerBlockEntity && secondEntity instanceof MapControllerBlockEntity) {
            ((ZoneControllerBlockEntity) firstEntity).unlinkExistingMapController(world);
            ((MapControllerBlockEntity) secondEntity).unlinkExistingZoneController(world);

            ((ZoneControllerBlockEntity) firstEntity).setLinkedMapController(secondPos);
            ((MapControllerBlockEntity) secondEntity).setLinkedZoneController(firstPos);
        }


        //ZoneController <=> SmallZombieDoorway
        else if (firstEntity instanceof SmallZombieDoorwayBlockEntity && secondEntity instanceof ZoneControllerBlockEntity) {
            ((SmallZombieDoorwayBlockEntity) firstEntity).unlinkExistingZoneController(world);
            //((ZoneControllerBlockEntity) secondEntity).unlinkExistingMapController(world);

            ((SmallZombieDoorwayBlockEntity) firstEntity).setLinkedZonePos(secondPos);
            ((ZoneControllerBlockEntity) secondEntity).addLinkedDoorway(firstPos);

        } else if (firstEntity instanceof ZoneControllerBlockEntity && secondEntity instanceof SmallZombieDoorwayBlockEntity) {
            //((ZoneControllerBlockEntity) firstEntity).unlinkExistingMapController(world);
            ((SmallZombieDoorwayBlockEntity) secondEntity).unlinkExistingZoneController(world);

            ((ZoneControllerBlockEntity) firstEntity).addLinkedDoorway(secondPos);
            ((SmallZombieDoorwayBlockEntity) secondEntity).setLinkedZonePos(firstPos);
        }
    }
}