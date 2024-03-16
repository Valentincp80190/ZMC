package com.aureskull.zmcmod.sound;

import com.aureskull.zmcmod.ZMCMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent REBUILD_DOOR = registerSoundEvent("rebuild_door");
    public static final SoundEvent REBUILD_DOOR_MONEY = registerSoundEvent("rebuild_door_money");


    private static SoundEvent registerSoundEvent(String name){
        Identifier id = new Identifier(ZMCMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerModSounds(){
        ZMCMod.LOGGER.info("Registering MobSounds for " + ZMCMod.MOD_ID);
    }
}
