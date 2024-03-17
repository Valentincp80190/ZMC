package com.aureskull.zmcmod.entity.client;

import com.aureskull.zmcmod.ZMCMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer STANDING_ZOMBIE =
            new EntityModelLayer(new Identifier(ZMCMod.MOD_ID, "standing_zombie"), "main");
}
