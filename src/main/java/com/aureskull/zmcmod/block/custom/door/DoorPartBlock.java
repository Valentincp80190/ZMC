package com.aureskull.zmcmod.block.custom.door;

import com.aureskull.zmcmod.block.entity.*;
import com.aureskull.zmcmod.block.entity.door.DoorBlockEntity;
import com.aureskull.zmcmod.item.custom.Linker;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
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

public class DoorPartBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final MapCodec<DoorPartBlock> CODEC = DoorPartBlock.createCodec(DoorPartBlock::new);

    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<DoorSide> SIDE = EnumProperty.of("side", DoorSide.class);

    public enum DoorSide implements StringIdentifiable {
        LEFT, RIGHT, CENTER, EXTERNAL_COLLIDER_LEFT, EXTERNAL_COLLIDER_RIGHT;

        @Override
        public String asString() {
            return name().toLowerCase();
        }
    }


    public DoorPartBlock(Settings settings) {
        super(settings.nonOpaque());
        setDefaultState(this.stateManager.getDefaultState()
                .with(SIDE, DoorSide.LEFT)
                .with(FACING, Direction.NORTH)
                .with(OPEN, false));
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.DOOR_BLOCK_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new DoorBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SIDE, FACING, OPEN);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();
        return getDefaultState().with(FACING, facing).with(SIDE, DoorSide.CENTER).with(OPEN, false);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }

    @Override
    public boolean isEnabled(FeatureSet enabledFeatures) {
        return super.isEnabled(enabledFeatures);
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
                    }else if(side == DoorSide.EXTERNAL_COLLIDER_LEFT){
                        return VoxelShapes.cuboid(.75, 0, 0, 1, 1, 1);
                    }else if(side == DoorSide.EXTERNAL_COLLIDER_RIGHT){
                        return VoxelShapes.cuboid(0, 0, 0, .25, 1, 1);
                    }
                }

                else if(facing == Direction.SOUTH){
                    if(side == DoorSide.LEFT){
                        return VoxelShapes.cuboid(0, 0, 0, .25, 1, .5);
                    }else if(side == DoorSide.RIGHT){
                        return VoxelShapes.cuboid(.75, 0, 0, 1, 1, .5);
                    }else if(side == DoorSide.EXTERNAL_COLLIDER_LEFT){
                        return VoxelShapes.cuboid(0, 0, 0, .25, 1, 1);
                    }else if(side == DoorSide.EXTERNAL_COLLIDER_RIGHT){
                        return VoxelShapes.cuboid(.75, 0, 0, 1, 1, 1);
                    }
                }
            }else{
                if(facing == Direction.EAST){
                    if(side == DoorSide.LEFT){
                        return VoxelShapes.cuboid(0, 0, .75, .5, 1, 1);
                    }else if(side == DoorSide.RIGHT){
                        return VoxelShapes.cuboid(0, 0, 0, .5, 1, .25);
                    }else if(side == DoorSide.EXTERNAL_COLLIDER_LEFT){
                        return VoxelShapes.cuboid(0, 0, .75, 1, 1, 1);
                    }else if(side == DoorSide.EXTERNAL_COLLIDER_RIGHT){
                        return VoxelShapes.cuboid(0, 0, 0, 1, 1, .25);
                    }
                }


                else if(facing == Direction.WEST){
                    if(side == DoorSide.LEFT){
                        return VoxelShapes.cuboid(.5, 0, 0, 1, 1, .25);
                    }else if(side == DoorSide.RIGHT){
                        return VoxelShapes.cuboid(.5, 0, .75, 1, 1, 1);
                    }else if(side == DoorSide.EXTERNAL_COLLIDER_LEFT){
                        return VoxelShapes.cuboid(0, 0, 0, 1, 1, .25);
                    }else if(side == DoorSide.EXTERNAL_COLLIDER_RIGHT){
                        return VoxelShapes.cuboid(0, 0, .75, 1, 1, 1);
                    }
                }
            }
            return VoxelShapes.empty();
        } else {
            // Adjust this based on the actual model dimensions
            if(side == DoorSide.EXTERNAL_COLLIDER_LEFT || side == DoorSide.EXTERNAL_COLLIDER_RIGHT) return VoxelShapes.empty();

            if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                return VoxelShapes.cuboid(0, 0, .375, 1, 1, .625);
            }else{
                return VoxelShapes.cuboid(.375, 0, 0, .625, 1, 1);
            }
        }
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        if(!state.get(OPEN)) return false;
        DoorSide side = state.get(SIDE);
        if(side == DoorSide.EXTERNAL_COLLIDER_RIGHT || side == DoorSide.EXTERNAL_COLLIDER_LEFT)
            return false;
        return true;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof DoorBlockEntity) {
                // Ensure we are placing the master block, not any part
                //if (((DoorBlockEntity) be).getMasterPos().equals(pos)) {
                    ((DoorBlockEntity) be).buildDoor(world, pos, world.getBlockState(pos));
                //}
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack mainHandStack = player.getMainHandStack();
        ItemStack offHandStack = player.getOffHandStack();

        if (mainHandStack.getItem() instanceof Linker || offHandStack.getItem() instanceof Linker) {
            return ActionResult.PASS;
        }

        if (!world.isClient()) {
            NamedScreenHandlerFactory screenHandlerFactory = ((DoorBlockEntity) world.getBlockEntity(pos));

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }

        return ActionResult.SUCCESS;
        /*if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof DoorBlockEntity) {
                DoorBlockEntity doorBlockEntity = (DoorBlockEntity) be;
                doorBlockEntity.openDoor();
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.CONSUME;*/
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof DoorBlockEntity) {
                DoorBlockEntity doorBlockEntity = (DoorBlockEntity) be;
                doorBlockEntity.destroyDoor();
                doorBlockEntity.unlink(be, ZoneControllerBlockEntity.class, true);
            }
        }
        super.onBreak(world, pos, state, player);
        return state;
    }
}
