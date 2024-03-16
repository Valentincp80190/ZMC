package com.aureskull.zmcmod.item;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static final RegistryKey<ItemGroup> ZMC = RegistryKey.of(RegistryKeys.ITEM_GROUP,
            new Identifier(ZMCMod.MOD_ID, "zmc"));

    public static void registerItemGroups(){
        ZMCMod.LOGGER.info("Registering Item Groups for " + ZMCMod.MOD_ID);

        Registry.register(Registries.ITEM_GROUP, ZMC, FabricItemGroup.builder()
            .icon(() -> new ItemStack(Items.STICK)).entries(((displayContext, entries) -> {
                entries.add(ModItems.ZONESTICK);
                entries.add(ModItems.LINKER);
                entries.add(ModBlocks.MAP_CONTROLLER);
                entries.add(ModBlocks.ZONE_CONTROLLER);
                entries.add(ModBlocks.SMALL_ZOMBIE_DOORWAY);
                entries.add(ModBlocks.ZOMBIE_SPAWNER);
            }))
            .displayName(Text.translatable("itemgroup.zmc"))
            .build());
    }
}
