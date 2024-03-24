package com.aureskull.zmcmod.networking;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.networking.packet.ExampleC2SPacket;
import com.aureskull.zmcmod.networking.packet.ExistZMCMapC2SPacket;
import com.aureskull.zmcmod.networking.packet.link.RemoveDoorwayLinkFromZoneS2CPacket;
import com.aureskull.zmcmod.networking.packet.link.RemoveLinkS2CPacket;
import com.aureskull.zmcmod.networking.packet.TriggerInteractionC2SPacket;
import com.aureskull.zmcmod.networking.packet.link.RemoveZoneLinkFromDoorwayS2CPacket;
import com.aureskull.zmcmod.networking.packet.link.RemoveZoneLinkFromZoneS2CPacket;
import com.aureskull.zmcmod.networking.packet.mapcontroller.MapControllerUpdateMapNameC2SPacket;
import com.aureskull.zmcmod.networking.packet.mapcontroller.MapControllerUpdateStartStateC2SPacket;
import com.aureskull.zmcmod.networking.packet.mapcontroller.UpdateMapControllerRoundS2CPacket;
import com.aureskull.zmcmod.networking.packet.zonecontroller.*;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModMessages {

    //MAP CONTROLLER
    public static final Identifier MAP_CONTROLLER_UPDATE_MAP_NAME = new Identifier(ZMCMod.MOD_ID, "mapcontroller_update_mapname");
    public static final Identifier MAP_CONTROLLER_UPDATE_START_STATE = new Identifier(ZMCMod.MOD_ID, "mapcontroller_update_start_state");
    public static final Identifier MAP_CONTROLLER_UPDATE_ROUND = new Identifier(ZMCMod.MOD_ID, "mapcontroller_update_round");

    //ZONE CONTROLLER
    public static final Identifier ZONE_CONTROLLER_UPDATE_ZONE_COLOR = new Identifier(ZMCMod.MOD_ID, "zonecontroller_update_color");
    public static final Identifier ZONE_CONTROLLER_UPDATE_POS = new Identifier(ZMCMod.MOD_ID, "zonecontroller_update_pos");

    //INTERACTION
    public static final Identifier TRIGGER_INTERACTION = new Identifier(ZMCMod.MOD_ID, "trigger_interaction");


    //OTHER
    public static final Identifier EXAMPLE_ID = new Identifier(ZMCMod.MOD_ID, "example");
    public static final Identifier EXIST_ZMC_MAP_CHECKER = new Identifier(ZMCMod.MOD_ID, "existzmcmapchecker");
    public static final Identifier EXIST_ZMC_MAP_CHECKER_RESPONSE = new Identifier(ZMCMod.MOD_ID, "existzmcmapchecker_response");


    //LINKER
    public static final Identifier REMOVE_LINK_FROM_BLOCK_ENTITY = new Identifier(ZMCMod.MOD_ID, "remove_link_from_block_entity");
    public static final Identifier REMOVE_DOORWAY_LINK_FROM_ZONE = new Identifier(ZMCMod.MOD_ID, "remove_doorway_link_from_zone");
    public static final Identifier REMOVE_ZONE_LINK_FROM_DOORWAY = new Identifier(ZMCMod.MOD_ID, "remove_zone_link_from_doorway");
    public static final Identifier REMOVE_ZONE_LINK_FROM_ZONE = new Identifier(ZMCMod.MOD_ID, "remove_zone_link_from_zone");

    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(EXAMPLE_ID, ExampleC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(EXIST_ZMC_MAP_CHECKER, ExistZMCMapC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(MAP_CONTROLLER_UPDATE_MAP_NAME, MapControllerUpdateMapNameC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(MAP_CONTROLLER_UPDATE_START_STATE, MapControllerUpdateStartStateC2SPacket::receive);


        ServerPlayNetworking.registerGlobalReceiver(ZONE_CONTROLLER_UPDATE_ZONE_COLOR, ZoneControllerUpdateZoneColorC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ZONE_CONTROLLER_UPDATE_POS, ZoneControllerUpdatePosC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(TRIGGER_INTERACTION, TriggerInteractionC2SPacket::receive);
    }

    public static void registerS2CPackets(){
        //LINKER
        ClientPlayNetworking.registerGlobalReceiver(REMOVE_LINK_FROM_BLOCK_ENTITY, RemoveLinkS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(REMOVE_DOORWAY_LINK_FROM_ZONE, RemoveDoorwayLinkFromZoneS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(REMOVE_ZONE_LINK_FROM_DOORWAY, RemoveZoneLinkFromDoorwayS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(REMOVE_ZONE_LINK_FROM_ZONE, RemoveZoneLinkFromZoneS2CPacket::receive);

        //MapController
        ClientPlayNetworking.registerGlobalReceiver(MAP_CONTROLLER_UPDATE_ROUND, UpdateMapControllerRoundS2CPacket::receive);
    }

    public static void sendExistZMCMapCheckerResponse(ServerPlayerEntity player, boolean exists) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(exists);
        ServerPlayNetworking.send(player, EXIST_ZMC_MAP_CHECKER_RESPONSE, buf);
    }

    public static void sendRemoveLinkPacket(World world, BlockPos blockPos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(blockPos);

        // Send this packet to all players in the world
        PlayerLookup.tracking((ServerWorld) world, blockPos).forEach(player -> {
            ServerPlayNetworking.send(player, ModMessages.REMOVE_LINK_FROM_BLOCK_ENTITY, buf);
        });
    }

    public static void sendRemoveZoneLinkFromDoorwayPacket(World world, BlockPos blockPos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(blockPos);

        // Send this packet to all players in the world
        PlayerLookup.tracking((ServerWorld) world, blockPos).forEach(player -> {
            ServerPlayNetworking.send(player, ModMessages.REMOVE_ZONE_LINK_FROM_DOORWAY, buf);
        });
    }

    public static void sendRemoveDoorwayLinkFromZonePacket(World world, BlockPos zonePos, BlockPos doorwayPos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(zonePos);
        buf.writeBlockPos(doorwayPos);

        // Send this packet to all players in the world
        PlayerLookup.tracking((ServerWorld) world, zonePos).forEach(player -> {
            ServerPlayNetworking.send(player, ModMessages.REMOVE_DOORWAY_LINK_FROM_ZONE, buf);
        });
    }

    public static void sendRemoveZoneLinkFromZonePacket(World world, BlockPos zonePos, BlockPos zonePosToRemove) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(zonePos);
        buf.writeBlockPos(zonePosToRemove);

        // Send this packet to all players in the world
        PlayerLookup.tracking((ServerWorld) world, zonePos).forEach(player -> {
            ServerPlayNetworking.send(player, ModMessages.REMOVE_ZONE_LINK_FROM_ZONE, buf);
        });
    }
}
