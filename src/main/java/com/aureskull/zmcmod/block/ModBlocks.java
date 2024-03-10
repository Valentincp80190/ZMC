package com.aureskull.zmcmod.block;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.custom.MapControllerBlock;
import com.aureskull.zmcmod.block.custom.SmallZombieDoorwayBlock;
import com.aureskull.zmcmod.block.custom.ZoneControllerBlock;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block MAP_CONTROLLER = registerBlock("map_controller",
            new MapControllerBlock(FabricBlockSettings.copyOf(Blocks.NETHERITE_BLOCK)));

    public static final Block ZONE_CONTROLLER = registerBlock("zone_controller",
            new ZoneControllerBlock(FabricBlockSettings.copyOf(Blocks.NETHERITE_BLOCK)));

    public static final Block SMALL_ZOMBIE_DOORWAY = registerBlock("small_zombie_doorway",
            new SmallZombieDoorwayBlock(FabricBlockSettings.copyOf(Blocks.NETHERITE_BLOCK).nonOpaque()));

    private static Block registerBlock(String name, Block block){
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(ZMCMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block){
        return Registry.register(Registries.ITEM, new Identifier(ZMCMod.MOD_ID, name),
            new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks(){
        ZMCMod.LOGGER.info("Registering MobBlocks for " + ZMCMod.MOD_ID);
    }
}
