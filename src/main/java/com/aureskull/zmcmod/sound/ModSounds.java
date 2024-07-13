package com.aureskull.zmcmod.sound;

import com.aureskull.zmcmod.ZMCMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    //WAVE
    public static final SoundEvent ROUND_START = registerSoundEvent("round_start");
    public static final SoundEvent ROUND_END = registerSoundEvent("round_end");

    //DOOR/WINDOW
    public static final SoundEvent DOOR_OPEN = registerSoundEvent("door_open");
    public static final SoundEvent REBUILD_WINDOW = registerSoundEvent("rebuild_window");
    public static final SoundEvent SNAP_WINDOW = registerSoundEvent("snap_window");
    public static final SoundEvent REBUILD_WINDOW_MONEY = registerSoundEvent("rebuild_window_money");


    //Standing zombie
    public static final SoundEvent STANDING_ZOMBIE_DEATH= registerSoundEvent("standing_zombie_death");
    public static final SoundEvent STANDING_ZOMBIE_AMB= registerSoundEvent("standing_zombie_amb");
    public static final SoundEvent STANDING_ZOMBIE_SPRINT= registerSoundEvent("standing_zombie_sprint");

    //Purchase
    public static final SoundEvent PURCHASE_ACCEPT = registerSoundEvent("purchase_accept");
    public static final SoundEvent PURCHASE_DENY = registerSoundEvent("purchase_deny");



    private static SoundEvent registerSoundEvent(String name){
        Identifier id = new Identifier(ZMCMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerModSounds(){
        ZMCMod.LOGGER.info("Registering ModSounds for " + ZMCMod.MOD_ID);
    }
}
