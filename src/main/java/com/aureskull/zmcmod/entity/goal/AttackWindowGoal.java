package com.aureskull.zmcmod.entity.goal;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

public class AttackWindowGoal extends Goal {
    private final StandingZombieEntity zombie;
    private BlockPos windowBlockPos;
    private long lastAttackTime = 0;

    public AttackWindowGoal(StandingZombieEntity zombie, BlockPos windowBlockPos) {
        this.zombie = zombie;
        this.windowBlockPos = windowBlockPos;
    }

    @Override
    public boolean canStart() {
        if(this.zombie.isPassedThroughWindow() || this.windowBlockPos == null) return false;

            // Check for nearby window every second
            if (this.lastAttackTime + 3000 > System.currentTimeMillis()) {
                return false;
            }
            this.lastAttackTime = System.currentTimeMillis();

            // Calculate the Manhattan distance to the window
            if (this.zombie.getBlockPos().getManhattanDistance(this.windowBlockPos) <= 1) {
                return true; // The window is within reach
            }

            return false;
    }

    @Override
    public void start() {
        // Attack the window
        if(this.windowBlockPos != null && this.zombie.getWorld().getBlockEntity(this.windowBlockPos) instanceof SmallZombieWindowBlockEntity window){
            window.removePlank();
        }
    }
}
