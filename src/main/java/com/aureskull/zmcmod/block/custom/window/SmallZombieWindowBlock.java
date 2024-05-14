package com.aureskull.zmcmod.block.custom.window;

import com.aureskull.zmcmod.block.entity.ModBlockEntities;
import com.aureskull.zmcmod.block.entity.window.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.block.entity.ZombieSpawnerBlockEntity;
import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.minecraft.state.StateManager.Builder;

public class SmallZombieWindowBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public static final IntProperty PLANKS = IntProperty.of("planks", 0, 6);

    public static final MapCodec<SmallZombieWindowBlock> CODEC = SmallZombieWindowBlock.createCodec(SmallZombieWindowBlock::new);

    public SmallZombieWindowBlock(Settings settings){
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(PLANKS, 0));
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, PLANKS);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // Set the block orientation based on the direction the player is facing
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(PLANKS, 0);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
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
        return new SmallZombieWindowBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.SMALL_ZOMBIE_WINDOW_BLOCK_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.0D, 0.0D, 0.0D, 1.0f, 2.0f, 1.0f);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) { // Server side
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof SmallZombieWindowBlockEntity) {
                SmallZombieWindowBlockEntity smallZombieDoorway = (SmallZombieWindowBlockEntity) be;

                smallZombieDoorway.unlink(be, ZombieSpawnerBlockEntity.class, true);
                smallZombieDoorway.unlink(be, ZoneControllerBlockEntity.class, true);
            }
        }
        super.onBreak(world, pos, state, player);
        return state;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context instanceof EntityShapeContext) {
            BlockEntity be = world.getBlockEntity(pos);

            Entity entity = ((EntityShapeContext) context).getEntity();
            if (be instanceof SmallZombieWindowBlockEntity window && window.getPlank() == 0 && entity instanceof StandingZombieEntity zombie && !zombie.isPassedThroughWindow()) {
                return VoxelShapes.empty();
            }
        }
        return super.getCollisionShape(state, world, pos, context);
    }
}
