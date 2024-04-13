package com.aureskull.zmcmod.entity.goal;

import com.aureskull.zmcmod.block.entity.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class CrawlThroughWindowGoal extends Goal {
    private final StandingZombieEntity zombie;
    private BlockPos windowSouthPos;
    private SmallZombieWindowBlockEntity window;
    private boolean isCrawling;
    private Direction windowDirection;

    public CrawlThroughWindowGoal(StandingZombieEntity zombie) {
        this.zombie = zombie;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.zombie.getWindowBlockPos() != null) {
            BlockEntity entity = this.zombie.getWorld().getBlockEntity(this.zombie.getWindowBlockPos());
            if (entity instanceof SmallZombieWindowBlockEntity window && window.getPlank() <= 0) {
                this.window = window;
                this.windowDirection = window.getWindowFacing();

                windowSouthPos = window.getDirectionPosition(Direction.SOUTH);
                return !this.zombie.isPassedThroughWindow() && this.zombie.squaredDistanceTo(windowSouthPos.getX() + .5f, windowSouthPos.getY(), windowSouthPos.getZ()+.5f) < .5;
            }
        }
        return false;
    }

    @Override
    public void start() {
        super.start();
        this.isCrawling = true;
    }

    @Override
    public void stop() {
        super.stop();
        this.isCrawling = false;
    }

    @Override
    public void tick() {
        if (this.isCrawling) {
            // Keep updating the movement to encourage going through the window
            if (window.canPassThrough()) {
                window.onZombiePassedThrough();
                if (windowDirection != null) {
                    Vec3d destination = getDestination();
                    if(destination != null){
                        this.zombie.teleport(windowSouthPos.getX() + .5f, windowSouthPos.getY(), windowSouthPos.getZ() + .5f);
                        this.zombie.move(MovementType.SELF, getDestination());
                        this.zombie.setPassedThroughWindow(true);
                    }
                }
            }
        }
    }

    private Vec3d getDestination(){
        switch (this.windowDirection) {
            case NORTH:
                return new Vec3d(0, 0, -2);
            case SOUTH:
                return new Vec3d(0, 0, 2);
            case EAST:
                return new Vec3d(2, 0, 0);
            case WEST:
                return new Vec3d(-2, 0, 0);
            default:
                return null;
        }
    }
}