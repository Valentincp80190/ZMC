package com.aureskull.zmcmod.item.custom;

import com.aureskull.zmcmod.block.custom.MapControllerBlock;
import com.aureskull.zmcmod.block.custom.SmallZombieWindowBlock;
import com.aureskull.zmcmod.block.custom.ZombieSpawnerBlock;
import com.aureskull.zmcmod.block.custom.ZoneControllerBlock;
import com.aureskull.zmcmod.block.custom.door.DoorPartBlock;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.block.entity.ZombieSpawnerBlockEntity;
import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.door.DoorBlockEntity;
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
            if (state.getBlock() instanceof ZombieSpawnerBlock
                    || state.getBlock() instanceof SmallZombieWindowBlock
                    || state.getBlock() instanceof MapControllerBlock
                    || state.getBlock() instanceof ZoneControllerBlock
                    || state.getBlock() instanceof DoorPartBlock) {
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
        if (firstEntity instanceof ZombieSpawnerBlockEntity zombieSpawnerBE && secondEntity instanceof SmallZombieWindowBlockEntity smallZombieWindowBE) {
            //((ZombieSpawnerBlockEntity) firstEntity).unlink(world, SmallZombieWindowBlockEntity.class);
            smallZombieWindowBE.unlink(smallZombieWindowBE, ZombieSpawnerBlockEntity.class, true);

            zombieSpawnerBE.addLink(secondPos, SmallZombieWindowBlockEntity.class);
            smallZombieWindowBE.addLink(firstPos, ZombieSpawnerBlockEntity.class);

        } else if (firstEntity instanceof SmallZombieWindowBlockEntity smallZombieWindowBE && secondEntity instanceof ZombieSpawnerBlockEntity zombieSpawnerBE) {
            smallZombieWindowBE.unlink(smallZombieWindowBE, ZombieSpawnerBlockEntity.class, true);
            //((ZombieSpawnerBlockEntity) secondEntity).unlink(world, SmallZombieWindowBlockEntity.class);

            smallZombieWindowBE.addLink(secondPos, ZombieSpawnerBlockEntity.class);
            zombieSpawnerBE.addLink(firstPos, SmallZombieWindowBlockEntity.class);
        }


        //ZoneController <=> MapController
        else if (firstEntity instanceof MapControllerBlockEntity mapControllerBE && secondEntity instanceof ZoneControllerBlockEntity zoneControllerBE) {
            mapControllerBE.unlink(mapControllerBE, ZoneControllerBlockEntity.class, true);
            //zoneControllerBE.unlink(zoneControllerBE, MapControllerBlockEntity.class);

            mapControllerBE.addLink(secondPos, ZoneControllerBlockEntity.class);
            zoneControllerBE.addLink(firstPos, MapControllerBlockEntity.class);

        } else if (firstEntity instanceof ZoneControllerBlockEntity zoneControllerBE && secondEntity instanceof MapControllerBlockEntity mapControllerBE) {
            //zoneControllerBE.unlink(zoneControllerBE, MapControllerBlockEntity.class);
            mapControllerBE.unlink(mapControllerBE, ZoneControllerBlockEntity.class, true);

            zoneControllerBE.addLink(secondPos, MapControllerBlockEntity.class);
            mapControllerBE.addLink(firstPos, ZoneControllerBlockEntity.class);
        }


        //ZoneController <=> SmallZombieDoorway
        else if (firstEntity instanceof SmallZombieWindowBlockEntity smallZombieWindowBE && secondEntity instanceof ZoneControllerBlockEntity zoneControllerBE) {
            smallZombieWindowBE.unlink(smallZombieWindowBE, ZoneControllerBlockEntity.class, true);

            smallZombieWindowBE.addLink(secondPos, ZoneControllerBlockEntity.class);
            zoneControllerBE.addLink(firstPos, SmallZombieWindowBlockEntity.class);
        } else if (firstEntity instanceof ZoneControllerBlockEntity zoneControllerBE && secondEntity instanceof SmallZombieWindowBlockEntity smallZombieWindowBE) {
            smallZombieWindowBE.unlink(smallZombieWindowBE, ZoneControllerBlockEntity.class, true);

            zoneControllerBE.addLink(secondPos, SmallZombieWindowBlockEntity.class);
            smallZombieWindowBE.addLink(firstPos, ZoneControllerBlockEntity.class);
        }


        //ZoneController <=> ZoneController
        else if (firstEntity instanceof ZoneControllerBlockEntity z1 && secondEntity instanceof ZoneControllerBlockEntity z2) {
            if(firstPos.getX() == secondPos.getX() && firstPos.getY() == secondPos.getY() && firstPos.getZ() == secondPos.getZ()) return;

            //first pos = parent
            if(z2.getChildLink(ZoneControllerBlockEntity.class).contains(firstPos)){
                z2.removeChildLink(z2, firstPos, ZoneControllerBlockEntity.class, true);
            }

            z1.addChildLink(secondPos, ZoneControllerBlockEntity.class);
            z2.addParentLink(firstPos, ZoneControllerBlockEntity.class);
        }


        //ZoneController <=> Door (0..* => 0..*)
        else if (firstEntity instanceof DoorBlockEntity doorBlockEntity && secondEntity instanceof ZoneControllerBlockEntity zoneControllerBlockEntity) {
            doorBlockEntity.addLink(secondPos, ZoneControllerBlockEntity.class);
            zoneControllerBlockEntity.addLink(firstPos, DoorBlockEntity.class);

        } else if (firstEntity instanceof ZoneControllerBlockEntity zoneControllerBlockEntity && secondEntity instanceof DoorBlockEntity doorBlockEntity) {
            doorBlockEntity.addLink(firstPos, ZoneControllerBlockEntity.class);
            zoneControllerBlockEntity.addLink(secondPos, DoorBlockEntity.class);
        }
    }
}