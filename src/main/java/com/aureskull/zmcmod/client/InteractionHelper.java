package com.aureskull.zmcmod.client;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

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
}
