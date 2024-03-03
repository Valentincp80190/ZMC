package com.aureskull.zmcmod.item;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.item.custom.Linker;
import com.aureskull.zmcmod.item.custom.ZoneStick;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final ZoneStick ZONESTICK = Registry.register(Registries.ITEM,
            new Identifier(ZMCMod.MOD_ID, "zone_stick"),
            new ZoneStick(new FabricItemSettings()));
    public static final Linker LINKER = Registry.register(Registries.ITEM,
            new Identifier(ZMCMod.MOD_ID, "linker"),
            new Linker(new FabricItemSettings()));

    private static Item registerItem(String name,Item item){//Utile pour des objets n'ayant pas de fonctionnalité à apporter
        return Registry.register(Registries.ITEM, new Identifier(ZMCMod.MOD_ID, name), item);
    }

    private static void addToItemGroup(RegistryKey<ItemGroup> group, Item item){
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
    }

    public static void registerModItems(){
        ZMCMod.LOGGER.info("Registering Mod Items for " + ZMCMod.MOD_ID);
    }
}
