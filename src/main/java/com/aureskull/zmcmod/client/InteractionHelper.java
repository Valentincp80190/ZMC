package com.aureskull.zmcmod.client;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class InteractionHelper {

    private InteractionHelper() { }

    public static Direction getLookDirection(PlayerEntity player) {
        // Normalize the yaw to a value between 0 and 360
        float yaw = MathHelper.wrapDegrees(player.getYaw());

        if (yaw < 0) {
            yaw += 360; // Convert to positive degrees if necessary
        }

        // Determine the direction based on the yaw value
        if (yaw >= 315 || yaw < 45) {
            return Direction.SOUTH;
        } else if (yaw < 135) {
            return Direction.WEST;
        } else if (yaw < 225) {
            return Direction.NORTH;
        } else {
            return Direction.EAST;
        }
    }

    public static boolean isFacingInteractable(PlayerEntity player, Direction blockFacing) {
        Direction playerFacing = getLookDirection(player);
        return playerFacing == blockFacing.getOpposite();
    }

    public static boolean isLookingAtInteractable(PlayerEntity player, Direction blockFacing){
        Direction playerFacing = getLookDirection(player);
        return playerFacing == blockFacing || playerFacing == blockFacing.getOpposite();
    }

    public static HitResult rayTrace(PlayerEntity player, Box boundingBox) {
        Vec3d startVec = player.getCameraPosVec(1.0F);
        Vec3d endVec = startVec.add(player.getRotationVec(1.0F).multiply(1.5)); // Length of the look vector
        return player.getWorld().raycast(new RaycastContext(startVec, endVec, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
    }
}
