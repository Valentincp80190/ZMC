package com.aureskull.zmcmod.item.custom;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.custom.MapControllerBlock;
import com.aureskull.zmcmod.block.custom.SmallZombieWindowBlock;
import com.aureskull.zmcmod.block.custom.ZombieSpawnerBlock;
import com.aureskull.zmcmod.block.custom.ZoneControllerBlock;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.block.entity.ZombieSpawnerBlockEntity;
import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
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
                playerEntity.sendMessage(Text.literal("")
                        .append(Text.literal("Linker position reset")
                                .formatted(Formatting.GOLD)));
                return ActionResult.SUCCESS;
            }

            BlockState state = world.getBlockState(pos);
            ZMCMod.LOGGER.info(state.getBlock().toString());
            if (state.getBlock() instanceof ZombieSpawnerBlock
                    || state.getBlock() instanceof SmallZombieWindowBlock
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
            player.sendMessage(Text.literal("")
                    .append(Text.literal("First position set at [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "] on " + world.getBlockEntity(pos).getClass().getSimpleName())
                            .formatted(Formatting.GOLD)));
        } else {
            // Retrieve the first position and link with the second position
            BlockPos firstPos = NbtHelper.toBlockPos(tag.getCompound("FirstPos"));
            player.sendMessage(Text.literal("")
                    .append(Text.literal("Second position set at [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "] on " + world.getBlockEntity(pos).getClass().getSimpleName())
                            .formatted(Formatting.GOLD)));
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
        if (firstEntity instanceof ZombieSpawnerBlockEntity && secondEntity instanceof SmallZombieWindowBlockEntity) {
            ((ZombieSpawnerBlockEntity) firstEntity).unlink(world, SmallZombieWindowBlockEntity.class);
            ((SmallZombieWindowBlockEntity) secondEntity).unlink(world, ZombieSpawnerBlockEntity.class);

            ((ZombieSpawnerBlockEntity) firstEntity).setLinkedBlock(secondPos, SmallZombieWindowBlockEntity.class);
            ((SmallZombieWindowBlockEntity) secondEntity).setLinkedBlock(firstPos, ZombieSpawnerBlockEntity.class);

        } else if (firstEntity instanceof SmallZombieWindowBlockEntity && secondEntity instanceof ZombieSpawnerBlockEntity) {
            ((SmallZombieWindowBlockEntity) firstEntity).unlink(world, ZombieSpawnerBlockEntity.class);
            ((ZombieSpawnerBlockEntity) secondEntity).unlink(world, SmallZombieWindowBlockEntity.class);

            ((SmallZombieWindowBlockEntity) firstEntity).setLinkedBlock(secondPos, ZombieSpawnerBlockEntity.class);
            ((ZombieSpawnerBlockEntity) secondEntity).setLinkedBlock(firstPos, SmallZombieWindowBlockEntity.class);
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
        else if (firstEntity instanceof SmallZombieWindowBlockEntity && secondEntity instanceof ZoneControllerBlockEntity) {
            ((SmallZombieWindowBlockEntity) firstEntity).unlink(world, ZoneControllerBlockEntity.class);

            ((SmallZombieWindowBlockEntity) firstEntity).setLinkedBlock(secondPos, ZoneControllerBlockEntity.class);
            ((ZoneControllerBlockEntity) secondEntity).addLinkedBlock(firstPos, SmallZombieWindowBlockEntity.class);

        } else if (firstEntity instanceof ZoneControllerBlockEntity && secondEntity instanceof SmallZombieWindowBlockEntity) {
            ((SmallZombieWindowBlockEntity) secondEntity).unlink(world, ZoneControllerBlockEntity.class);

            ((ZoneControllerBlockEntity) firstEntity).addLinkedBlock(secondPos, SmallZombieWindowBlockEntity.class);
            ((SmallZombieWindowBlockEntity) secondEntity).setLinkedBlock(firstPos, ZoneControllerBlockEntity.class);
        }


        //ZoneController <=> ZoneController
        else if (firstEntity instanceof ZoneControllerBlockEntity && secondEntity instanceof ZoneControllerBlockEntity) {
            if(firstPos.getX() == secondPos.getX() && firstPos.getY() == secondPos.getY() && firstPos.getZ() == secondPos.getZ()) return;

            //first pos = parent
            ((ZoneControllerBlockEntity) firstEntity).addChild(secondPos, ZoneControllerBlockEntity.class);
            ((ZoneControllerBlockEntity) secondEntity).addParent(firstPos, ZoneControllerBlockEntity.class);
        }
    }
}