package com.aureskull.zmcmod.block.custom;

import com.aureskull.zmcmod.block.entity.ModBlockEntities;
import com.aureskull.zmcmod.block.entity.window.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.block.entity.ZombieSpawnerBlockEntity;
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
                spawnerEntity.unlink(be, SmallZombieWindowBlockEntity.class, true);
            }
        }
        super.onBreak(world, pos, state, player);
        return state;
    }
}
