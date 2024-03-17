package com.aureskull.zmcmod.item.custom;

import com.aureskull.zmcmod.block.custom.SmallZombieDoorwayBlock;
import com.aureskull.zmcmod.block.custom.ZombieSpawnerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
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

        playerEntity.sendMessage(Text.literal("useOnBlock"));

        if (!world.isClient && playerEntity != null) {
            // Check if the player is sneaking to clear the stored positions
            if (playerEntity.isSneaking()) {
                playerEntity.sendMessage(Text.literal("Call clear position"));
                clearStoredPositions(itemStack, playerEntity);
                return ActionResult.SUCCESS;
            }

            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof ZombieSpawnerBlock || state.getBlock() instanceof SmallZombieDoorwayBlock) {
                // Process the block interaction
                playerEntity.sendMessage(Text.literal("Call make link"));
                handleBlockLinking(world, pos, itemStack, playerEntity);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    private void handleBlockLinking(World world, BlockPos pos, ItemStack itemStack, PlayerEntity player) {
        NbtCompound tag = itemStack.getOrCreateNbt();

        if (!tag.contains("FirstPos")) {
            // Store the first position
            player.sendMessage(Text.literal("First block selected at " + pos.toString()), false);
            tag.put("FirstPos", NbtHelper.fromBlockPos(pos));
        } else {
            // Retrieve the first position and link with the second position
            BlockPos firstPos = NbtHelper.toBlockPos(tag.getCompound("FirstPos"));
            player.sendMessage(Text.literal("Blocks linked: " + firstPos.toString() + " and " + pos.toString()), false);
            //linkBlocks(world, firstPos, pos);
            tag.remove("FirstPos"); // Clear after linking
        }
    }

    private void clearStoredPositions(ItemStack itemStack, PlayerEntity player) {
        NbtCompound tag = itemStack.getOrCreateNbt();
        if (tag.contains("FirstPos")) {
            tag.remove("FirstPos");
            player.sendMessage(Text.literal("Linker reset"), false);
        }
    }
}