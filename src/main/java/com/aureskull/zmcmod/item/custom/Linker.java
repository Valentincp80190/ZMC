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
            ((ZombieSpawnerBlockEntity) firstEntity).unlink(world, SmallZombieDoorwayBlockEntity.class);
            ((SmallZombieDoorwayBlockEntity) secondEntity).unlink(world, ZombieSpawnerBlockEntity.class);

            ((ZombieSpawnerBlockEntity) firstEntity).setLinkedBlock(secondPos, SmallZombieDoorwayBlockEntity.class);
            ((SmallZombieDoorwayBlockEntity) secondEntity).setLinkedBlock(firstPos, ZombieSpawnerBlockEntity.class);

        } else if (firstEntity instanceof SmallZombieDoorwayBlockEntity && secondEntity instanceof ZombieSpawnerBlockEntity) {
            ((SmallZombieDoorwayBlockEntity) firstEntity).unlink(world, ZombieSpawnerBlockEntity.class);
            ((ZombieSpawnerBlockEntity) secondEntity).unlink(world, SmallZombieDoorwayBlockEntity.class);

            ((SmallZombieDoorwayBlockEntity) firstEntity).setLinkedBlock(secondPos, ZombieSpawnerBlockEntity.class);
            ((ZombieSpawnerBlockEntity) secondEntity).setLinkedBlock(firstPos, SmallZombieDoorwayBlockEntity.class);
        }


        //ZoneController <=> MapController
        else if (firstEntity instanceof MapControllerBlockEntity && secondEntity instanceof ZoneControllerBlockEntity) {
            ((MapControllerBlockEntity) firstEntity).unlink(world, ZoneControllerBlockEntity.class);
            ((ZoneControllerBlockEntity) secondEntity).unlink(world, MapControllerBlockEntity.class);

            ((MapControllerBlockEntity) firstEntity).setLinkedBlock(secondPos, ZoneControllerBlockEntity.class);
            ((ZoneControllerBlockEntity) secondEntity).setLinkedBlock(firstPos, MapControllerBlockEntity.class);

        } else if (firstEntity instanceof ZoneControllerBlockEntity && secondEntity instanceof MapControllerBlockEntity) {
            ((ZoneControllerBlockEntity) firstEntity).unlink(world, MapControllerBlockEntity.class);
            ((MapControllerBlockEntity) secondEntity).unlink(world, ZoneControllerBlockEntity.class);

            ((ZoneControllerBlockEntity) firstEntity).setLinkedBlock(secondPos, MapControllerBlockEntity.class);
            ((MapControllerBlockEntity) secondEntity).setLinkedBlock(firstPos, ZoneControllerBlockEntity.class);
        }


        //ZoneController <=> SmallZombieDoorway
        else if (firstEntity instanceof SmallZombieDoorwayBlockEntity && secondEntity instanceof ZoneControllerBlockEntity) {
            ((SmallZombieDoorwayBlockEntity) firstEntity).unlink(world, ZoneControllerBlockEntity.class);
            //((ZoneControllerBlockEntity) secondEntity).unlinkExistingMapController(world);

            ((SmallZombieDoorwayBlockEntity) firstEntity).setLinkedBlock(secondPos, ZoneControllerBlockEntity.class);
            ((ZoneControllerBlockEntity) secondEntity).addLinkedBlock(firstPos, SmallZombieDoorwayBlockEntity.class);

        } else if (firstEntity instanceof ZoneControllerBlockEntity && secondEntity instanceof SmallZombieDoorwayBlockEntity) {
            //((ZoneControllerBlockEntity) firstEntity).unlinkExistingMapController(world);
            ((SmallZombieDoorwayBlockEntity) secondEntity).unlink(world, ZoneControllerBlockEntity.class);

            ((ZoneControllerBlockEntity) firstEntity).addLinkedBlock(secondPos, SmallZombieDoorwayBlockEntity.class);
            ((SmallZombieDoorwayBlockEntity) secondEntity).setLinkedBlock(firstPos, ZoneControllerBlockEntity.class);
        }
    }
}