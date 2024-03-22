package com.aureskull.zmcmod.block.custom;

import com.aureskull.zmcmod.block.entity.*;
import com.aureskull.zmcmod.item.custom.Linker;
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
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MapControllerBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final MapCodec<MapControllerBlock> CODEC = MapControllerBlock.createCodec(MapControllerBlock::new);

    public MapControllerBlock(Settings settings){
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
        return new MapControllerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.MAP_CONTROLLER_BLOCK_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack mainHandStack = player.getMainHandStack();
        ItemStack offHandStack = player.getOffHandStack();

        if (mainHandStack.getItem() instanceof Linker || offHandStack.getItem() instanceof Linker) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = ((MapControllerBlockEntity) world.getBlockEntity(pos));

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) { // Server side
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof MapControllerBlockEntity) {
                MapControllerBlockEntity mapControllerBE = (MapControllerBlockEntity) be;

                mapControllerBE.unlinkExistingZoneController(world);
            }
        }
        super.onBreak(world, pos, state, player);
        return state;
    }
}
