package com.aureskull.zmcmod.entity.goal;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

public class LookAtBlockPosBlockEntityGoal extends Goal {
    private final MobEntity entity;
    private final BlockPos blockPos;

    public LookAtBlockPosBlockEntityGoal(MobEntity entity, BlockPos blockPos) {
        this.entity = entity;
        this.blockPos = blockPos;
    }

    @Override
    public void tick() {
        if (blockPos != null){
            BlockEntity be = this.entity.getWorld().getBlockEntity(blockPos);

            if(be != null){
                if(be instanceof SmallZombieWindowBlockEntity window) {
                    // Check if the window still has planks left
                    BlockPos windowPos = window.getPos();
                    double d0 = windowPos.getX() + 0.5 - this.entity.getX();
                    double d2 = windowPos.getZ() + 0.5 - this.entity.getZ();
                    this.entity.setYaw(-((float) Math.atan2(d0, d2)) * (180F / (float) Math.PI));
                    this.entity.bodyYaw = this.entity.getYaw();
                    this.entity.headYaw = this.entity.bodyYaw;
                }
            }
        }
    }

    @Override
    public boolean canStart() {
        if (blockPos != null){
            BlockEntity be = this.entity.getWorld().getBlockEntity(blockPos);

            if(be instanceof SmallZombieWindowBlockEntity window
                    && entity instanceof StandingZombieEntity standingZombie) {
                if(!standingZombie.isPassedThroughWindow()){
                    return true;
                }
            }
        }
        return false;
    }

    /*@Override
    public boolean shouldContinue(){
        if(this.mapControllerBlockEntity != null){
            if(mapControllerBlockEntity instanceof SmallZombieWindowBlockEntity window
                && entity instanceof StandingZombieEntity standingZombie) {
                if(!standingZombie.isPassedThroughWindow()){
                    return true;
                }
            }
        }
        return false;
    }*/
}
