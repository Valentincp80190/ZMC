package com.aureskull.zmcmod.item.custom;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.custom.SmallZombieDoorwayBlock;
import com.aureskull.zmcmod.block.custom.ZombieSpawnerBlock;
import com.aureskull.zmcmod.block.entity.SmallZombieDoorwayBlockEntity;
import com.aureskull.zmcmod.block.entity.ZombieSpawnerBlockEntity;
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

    /*@Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        playerEntity.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, 1.0F, 1.0F);
        return TypedActionResult.success(playerEntity.getStackInHand(hand));
    }*/

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
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
            if (state.getBlock() instanceof ZombieSpawnerBlock || state.getBlock() instanceof SmallZombieDoorwayBlock) {
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

        if (firstEntity instanceof ZombieSpawnerBlockEntity && secondEntity instanceof SmallZombieDoorwayBlockEntity) {
            unlinkExistingDoorway(world, (ZombieSpawnerBlockEntity) firstEntity);
            unlinkExistingZombieSpawner(world, (SmallZombieDoorwayBlockEntity) secondEntity);

            ((ZombieSpawnerBlockEntity) firstEntity).setLinkedDoorway(secondPos);
            ((SmallZombieDoorwayBlockEntity) secondEntity).setLinkedSpawner(firstPos);

        } else if (firstEntity instanceof SmallZombieDoorwayBlockEntity && secondEntity instanceof ZombieSpawnerBlockEntity) {
            unlinkExistingDoorway(world, (ZombieSpawnerBlockEntity) secondEntity);
            unlinkExistingZombieSpawner(world, (SmallZombieDoorwayBlockEntity) firstEntity);

            ((SmallZombieDoorwayBlockEntity) firstEntity).setLinkedSpawner(secondPos);
            ((ZombieSpawnerBlockEntity) secondEntity).setLinkedDoorway(firstPos);
        }
    }

    private void unlinkExistingDoorway(World world, ZombieSpawnerBlockEntity spawnerEntity) {
        BlockPos existingLinkedDoorway = spawnerEntity.getLinkedDoorway();
        if (existingLinkedDoorway != null) {
            BlockEntity existingDoorwayEntity = world.getBlockEntity(existingLinkedDoorway);
            if (existingDoorwayEntity instanceof SmallZombieDoorwayBlockEntity) {
                ((SmallZombieDoorwayBlockEntity) existingDoorwayEntity).setLinkedSpawner(null);
                existingDoorwayEntity.markDirty();

                ModMessages.sendRemoveLinkPacket(world, existingLinkedDoorway);
            }
        }
    }

    private void unlinkExistingZombieSpawner(World world, SmallZombieDoorwayBlockEntity ZombieDoorwayBE) {
        BlockPos existingZombieSpawner = ZombieDoorwayBE.getLinkedSpawner();
        if (existingZombieSpawner != null) {
            BlockEntity existingZombieSpawnerBE = world.getBlockEntity(existingZombieSpawner);
            if (existingZombieSpawnerBE instanceof ZombieSpawnerBlockEntity) {
                ((ZombieSpawnerBlockEntity) existingZombieSpawnerBE).setLinkedDoorway(null);
                existingZombieSpawnerBE.markDirty();

                ModMessages.sendRemoveLinkPacket(world, existingZombieSpawner);
            }
        }
    }
}