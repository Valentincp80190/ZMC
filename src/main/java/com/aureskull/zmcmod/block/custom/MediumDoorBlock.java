package com.aureskull.zmcmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MediumDoorBlock extends HorizontalFacingBlock {
    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public enum DoorSide implements StringIdentifiable {
        LEFT,
        RIGHT,
        CENTER;

        @Override
        public String asString() {
            return name().toLowerCase();
        }
    }

    public static final EnumProperty<DoorSide> SIDE = EnumProperty.of("side", DoorSide.class);

    public MediumDoorBlock(Settings settings) {
        super(settings.nonOpaque());
        setDefaultState(this.stateManager.getDefaultState()
                .with(SIDE, DoorSide.CENTER)
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
                .with(Properties.OPEN, false));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SIDE, Properties.HORIZONTAL_FACING, Properties.OPEN);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            toggleDoor(world, pos, state.get(OPEN), state.get(FACING));
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }

    private void toggleDoor(World world, BlockPos pos, boolean isOpen, Direction facing) {
        // Calculate the base position of the 3x3 door depending on its facing
        BlockPos basePos = pos;
        if (facing == Direction.NORTH) {
            basePos = pos.west(); // Door's base when facing north
        } else if (facing == Direction.SOUTH) {
            basePos = pos.east().south(2); // Door's base when facing south
        } else if (facing == Direction.WEST) {
            basePos = pos.north(); // Door's base when facing west
        } else if (facing == Direction.EAST) {
            basePos = pos.west().south(2); // Door's base when facing east
        }

        // The range for x and z should be 3 to cover the width of the door
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                BlockPos currentPos;
                if (facing.getAxis() == Direction.Axis.X) {
                    // If the door is facing West or East, iterate over z
                    currentPos = basePos.add(0, y, x);
                } else {
                    // If the door is facing North or South, iterate over x
                    currentPos = basePos.add(x, y, 0);
                }

                BlockState currentBlockState = world.getBlockState(currentPos);
                if (currentBlockState.getBlock() == this) {
                    // We need to ensure we toggle only the part of the door that has the property OPEN
                    if (currentBlockState.getProperties().contains(OPEN)) {
                        world.setBlockState(currentPos, currentBlockState.with(OPEN, !isOpen), 3);
                    }
                }
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(SIDE, DoorSide.CENTER).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        DoorSide side = state.get(SIDE);

        if (state.get(OPEN)) {
            if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                if(facing == Direction.NORTH){
                    if(side == DoorSide.LEFT){
                        return VoxelShapes.cuboid(.75, 0, .5, 1, 1, 1);
                    }else if(side == DoorSide.RIGHT){
                        return VoxelShapes.cuboid(0, 0, .5, .25, 1, 1);
                    }
                }

                else if(facing == Direction.SOUTH){
                    if(side == DoorSide.LEFT){
                        return VoxelShapes.cuboid(0, 0, 0, .25, 1, .5);
                    }else if(side == DoorSide.RIGHT){
                        return VoxelShapes.cuboid(.75, 0, 0, 1, 1, .5);
                    }
                }
            }else{
                if(facing == Direction.EAST){
                    if(side == DoorSide.LEFT){
                        return VoxelShapes.cuboid(0, 0, .75, .5, 1, 1);
                    }else if(side == DoorSide.RIGHT){
                        return VoxelShapes.cuboid(0, 0, 0, .5, 1, .25);
                    }
                }


                else if(facing == Direction.WEST){
                    if(side == DoorSide.LEFT){
                        return VoxelShapes.cuboid(.5, 0, 0, 1, 1, .25);
                    }else if(side == DoorSide.RIGHT){
                        return VoxelShapes.cuboid(.5, 0, .75, 1, 1, 1);
                    }
                }
            }
            return VoxelShapes.empty();
        } else {
            // Adjust this based on the actual model dimensions
            return VoxelShapes.cuboid(0, 0, 0, 1, 1, 1);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            Direction facing = state.get(FACING);
            // Determining the increment for positioning blocks relative to the door's orientation
            for (int x = -1; x <= 1; x++) {
                for (int y = 0; y <= 2; y++) {
                    BlockPos blockPos;
                    DoorSide side;

                    if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                        blockPos = pos.add(x, y, 0);  // Expands east-west
                        // Determine side based on X position relative to facing
                        if (x == 0) {
                            side = DoorSide.CENTER; // Middle blocks might be DoorSide.NULL or whatever you define for invisible/non-interactable blocks
                        } else if ((x == 1 && facing == Direction.NORTH) || (x == -1 && facing == Direction.SOUTH)) {
                            side = DoorSide.LEFT;
                        } else {
                            side = DoorSide.RIGHT;
                        }
                    } else {
                        blockPos = pos.add(0, y, x);  // Expands north-south
                        // Determine side based on Z position relative to facing
                        if (x == 0) {
                            side = DoorSide.CENTER;
                        } else if ((x == 1 && facing == Direction.WEST) || (x == -1 && facing == Direction.EAST)) {
                            side = DoorSide.RIGHT;
                        } else {
                            side = DoorSide.LEFT;
                        }
                    }

                    if (!blockPos.equals(pos) && world.getBlockState(blockPos).isAir()) {
                        world.setBlockState(blockPos, state.with(SIDE, side), 3);
                    }
                }
            }
        }
    }


    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        Direction facing = state.get(FACING);
        int dx = 0;
        int dz = 0;
        switch (facing) {
            case NORTH:
            case SOUTH:
                dx = 1; // Door expands east-west
                break;
            case EAST:
            case WEST:
                dz = 1; // Door expands north-south
                break;
        }

        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                BlockPos currentPos = pos.add(dx * x, y, dz * x);
                BlockState currentBlockState = world.getBlockState(currentPos);
                if (currentBlockState.getBlock() == this) { // Check if it's the same type of door block
                    world.removeBlock(currentPos, false);
                }
            }
        }
        super.onBreak(world, pos, state, player);
        return state;
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }

    @Override
    public boolean isEnabled(FeatureSet enabledFeatures) {
        return super.isEnabled(enabledFeatures);
    }
}
