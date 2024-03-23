package com.aureskull.zmcmod.block.custom;

import com.aureskull.zmcmod.block.entity.ModBlockEntities;
import com.aureskull.zmcmod.block.entity.SmallZombieDoorwayBlockEntity;
import com.aureskull.zmcmod.block.entity.ZombieSpawnerBlockEntity;
import com.aureskull.zmcmod.networking.ModMessages;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ZombieSpawnerBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final MapCodec<ZombieSpawnerBlock> CODEC = ZombieSpawnerBlock.createCodec(ZombieSpawnerBlock::new);

    public ZombieSpawnerBlock(Settings settings){
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new ZombieSpawnerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.ZOMBIE_SPAWNER_BLOCK_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) { // Server side
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ZombieSpawnerBlockEntity) {
                ZombieSpawnerBlockEntity spawnerEntity = (ZombieSpawnerBlockEntity) be;

                spawnerEntity.unlink(world, SmallZombieDoorwayBlockEntity.class);
            }
        }
        super.onBreak(world, pos, state, player);
        return state;
    }

    /*@Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof ZombieSpawnerBlockEntity) {
                ((ZombieSpawnerBlockEntity) entity).spawnZombie(pos);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }*/

    /*@Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            // Call the summon method when the player right-clicks the block
            summon(world, pos);
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }*/

    /*public void summon(World world, BlockPos spawnerPos) {
        if (!world.isClient) {
            // Define the spawn position (just above the spawner block)
            BlockPos spawnPos = spawnerPos.up();

            // Create a new zombie entity
            StandingZombieEntity zombie = ModEntities.STANDING_ZOMBIE.create(world);
            if (zombie != null) {
                // Set the zombie's position
                zombie.refreshPositionAndAngles((double)spawnPos.getX() + 0.5, (double)spawnPos.getY(), (double)spawnPos.getZ() + 0.5, 0.0F, 0.0F);

                // Example NBT data: A target block position for the zombie to move towards
                BlockPos targetPos = new BlockPos(7,-57,-13);
                NbtCompound compound = zombie.writeNbt(new NbtCompound());
                compound.put("BlockPosToGo", NbtHelper.fromBlockPos(targetPos));

                zombie.readNbt(compound);
                world.spawnEntity(zombie);

                world.playSound(null, spawnPos, SoundEvents.ENTITY_ZOMBIE_AMBIENT, SoundCategory.HOSTILE, 1.0F, 1.0F);
            }
        }
    }*/
}
