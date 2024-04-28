package com.aureskull.zmcmod.networking;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.networking.packet.ExistZMCMapC2SPacket;
import com.aureskull.zmcmod.networking.packet.door.DoorUpdatePriceC2SPacket;
import com.aureskull.zmcmod.networking.packet.link.*;
import com.aureskull.zmcmod.networking.packet.TriggerInteractionC2SPacket;
import com.aureskull.zmcmod.networking.packet.mapcontroller.*;
import com.aureskull.zmcmod.networking.packet.player.PlayerUpdateOverlayStatusS2CPacket;
import com.aureskull.zmcmod.networking.packet.player.PlayerUpdateRoundHUDS2CPacket;
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
    public static final Identifier MAP_CONTROLLER_UPDATE_POS = new Identifier(ZMCMod.MOD_ID, "mapcontroller_update_pos");

    //DOOR
    public static final Identifier DOOR_UPDATE_PRICE =  new Identifier(ZMCMod.MOD_ID, "door_update_price");

    //ZONE CONTROLLER
    public static final Identifier ZONE_CONTROLLER_UPDATE_ZONE_COLOR = new Identifier(ZMCMod.MOD_ID, "zonecontroller_update_color");
    public static final Identifier ZONE_CONTROLLER_UPDATE_POS = new Identifier(ZMCMod.MOD_ID, "zonecontroller_update_pos");

    //INTERACTION
    public static final Identifier TRIGGER_INTERACTION = new Identifier(ZMCMod.MOD_ID, "trigger_interaction");


    //OTHER
    public static final Identifier EXIST_ZMC_MAP_CHECKER = new Identifier(ZMCMod.MOD_ID, "existzmcmapchecker");
    public static final Identifier EXIST_ZMC_MAP_CHECKER_RESPONSE = new Identifier(ZMCMod.MOD_ID, "existzmcmapchecker_response");


    //LINKER
    public static final Identifier REMOVE_LINK_FROM_BLOCK_ENTITY = new Identifier(ZMCMod.MOD_ID, "remove_link_from_block_entity");
    public static final Identifier REMOVE_WINDOW_LINK_FROM_ZONE = new Identifier(ZMCMod.MOD_ID, "remove_doorway_link_from_zone");
    public static final Identifier REMOVE_ZONE_LINK_FROM_WINDOW = new Identifier(ZMCMod.MOD_ID, "remove_zone_link_from_doorway");
    public static final Identifier REMOVE_ZONE_LINK_FROM_ZONE = new Identifier(ZMCMod.MOD_ID, "remove_zone_link_from_zone");
    public static final Identifier REMOVE_DOOR_LINK_FROM_ZONE = new Identifier(ZMCMod.MOD_ID, "remove_door_link_from_zone");
    public static final Identifier REMOVE_ZONE_LINK_FROM_DOOR = new Identifier(ZMCMod.MOD_ID, "remove_zone_link_from_door");


    //PLAYER
    public static final Identifier PLAYER_UPDATE_OVERLAY = new Identifier(ZMCMod.MOD_ID, "player_update_overlay");
    public static final Identifier PLAYER_UPDATE_ROUND_HUD = new Identifier(ZMCMod.MOD_ID, "player_update_round_hud");

    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(EXIST_ZMC_MAP_CHECKER, ExistZMCMapC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(MAP_CONTROLLER_UPDATE_MAP_NAME, MapControllerUpdateMapNameC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(MAP_CONTROLLER_UPDATE_START_STATE, MapControllerUpdateStartStateC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(MAP_CONTROLLER_UPDATE_POS, MapControllerUpdatePosC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(ZONE_CONTROLLER_UPDATE_ZONE_COLOR, ZoneControllerUpdateZoneColorC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ZONE_CONTROLLER_UPDATE_POS, ZoneControllerUpdatePosC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(DOOR_UPDATE_PRICE, DoorUpdatePriceC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(TRIGGER_INTERACTION, TriggerInteractionC2SPacket::receive);
    }

    public static void registerS2CPackets(){
        //LINKER
        ClientPlayNetworking.registerGlobalReceiver(REMOVE_LINK_FROM_BLOCK_ENTITY, RemoveLinkS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(REMOVE_WINDOW_LINK_FROM_ZONE, RemoveWindowLinkFromZoneS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(REMOVE_ZONE_LINK_FROM_WINDOW, RemoveZoneLinkFromWindowS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(REMOVE_ZONE_LINK_FROM_ZONE, RemoveZoneLinkFromZoneS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(REMOVE_DOOR_LINK_FROM_ZONE, RemoveDoorLinkFromZoneS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(REMOVE_ZONE_LINK_FROM_DOOR, RemoveZoneLinkFromDoorS2CPacket::receive);

        //MapController
        ClientPlayNetworking.registerGlobalReceiver(MAP_CONTROLLER_UPDATE_ROUND, MapControllerUpdateRoundS2CPacket::receive);

        //PLAYER
        ClientPlayNetworking.registerGlobalReceiver(PLAYER_UPDATE_OVERLAY, PlayerUpdateOverlayStatusS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(PLAYER_UPDATE_ROUND_HUD, PlayerUpdateRoundHUDS2CPacket::receive);
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
            ServerPlayNetworking.send(player, ModMessages.REMOVE_ZONE_LINK_FROM_WINDOW, buf);
        });
    }

    public static void sendRemoveDoorwayLinkFromZonePacket(World world, BlockPos zonePos, BlockPos doorwayPos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(zonePos);
        buf.writeBlockPos(doorwayPos);

        // Send this packet to all players in the world
        PlayerLookup.tracking((ServerWorld) world, zonePos).forEach(player -> {
            ServerPlayNetworking.send(player, ModMessages.REMOVE_WINDOW_LINK_FROM_ZONE, buf);
        });
    }

    public static void sendRemoveDoorLinkFromZonePacket(World world, BlockPos zonePos, BlockPos doorPos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(zonePos);
        buf.writeBlockPos(doorPos);

        // Send this packet to all players in the world
        PlayerLookup.tracking((ServerWorld) world, zonePos).forEach(player -> {
            ServerPlayNetworking.send(player, ModMessages.REMOVE_DOOR_LINK_FROM_ZONE, buf);
        });
    }

    public static void sendRemoveZoneLinkFromDoorPacket(World world, BlockPos zonePos, BlockPos doorPos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(zonePos);
        buf.writeBlockPos(doorPos);

        // Send this packet to all players in the world
        PlayerLookup.tracking((ServerWorld) world, zonePos).forEach(player -> {
            ServerPlayNetworking.send(player, ModMessages.REMOVE_ZONE_LINK_FROM_DOOR, buf);
        });
    }



    public static void sendUpdateDisplayOverlayPacket(ServerPlayerEntity player, boolean displayOverlay) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(displayOverlay);

        ServerPlayNetworking.send(player, ModMessages.PLAYER_UPDATE_OVERLAY, buf);
    }

    public static void sendUpdateRoundHUDPacket(ServerPlayerEntity player, int newRound) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(newRound);

        ServerPlayNetworking.send(player, ModMessages.PLAYER_UPDATE_ROUND_HUD, buf);
    }
}
