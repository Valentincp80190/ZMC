package com.aureskull.zmcmod.entity.goal;

import com.aureskull.zmcmod.block.entity.window.SmallZombieWindowBlockEntity;
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
    private boolean shouldHaveCrawled = false;

    public CrawlThroughWindowGoal(StandingZombieEntity zombie) {
        this.zombie = zombie;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        BlockEntity entity = zombie.getWorld().getBlockEntity(zombie.getWindowBlockPos());
        if (entity instanceof SmallZombieWindowBlockEntity window && window.getPlank() <= 0) {
            this.window = window;
            this.windowDirection = window.getWindowFacing();

            windowSouthPos = window.getDirectionPosition(Direction.SOUTH);

            BlockPos a = zombie.getBlockPos();
            BlockPos b = window.getPos();
            boolean t = a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
            return (shouldHaveCrawled && t) || //zombie stuck
                    !this.zombie.isPassedThroughWindow() && zombie.squaredDistanceTo(windowSouthPos.getX() + .5f, windowSouthPos.getY(), windowSouthPos.getZ()+.5f) < .6;
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
        BlockPos destinationBP = zombie.getBlockPos().add((int)getDestination().x, (int)getDestination().y, (int)getDestination().z);

        if(shouldHaveCrawled){
            zombie.teleport(destinationBP.getX(), destinationBP.getY(), destinationBP.getZ());
            zombie.setPassedThroughWindow(true);
            return;
        }

        //Allow zombie to pass through the door during 2s.
        window.onZombiePassedThrough();

        zombie.teleport(windowSouthPos.getX() + .5f, windowSouthPos.getY(), windowSouthPos.getZ() + .5f);
        zombie.move(MovementType.SELF, getDestination());

        zombie.setPassedThroughWindow(true);
        shouldHaveCrawled = true;
    }

    private Vec3d getDestination(){
        return switch (this.windowDirection) {
            case NORTH -> new Vec3d(0, 0, -2);
            case SOUTH -> new Vec3d(0, 0, 2);
            case EAST -> new Vec3d(2, 0, 0);
            case WEST -> new Vec3d(-2, 0, 0);
            default -> null;
        };
    }
}