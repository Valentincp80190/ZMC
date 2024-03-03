package com.aureskull.zmcmod.networking;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.networking.packet.ExampleC2SPacket;
import com.aureskull.zmcmod.networking.packet.ExistZMCMapC2SPacket;
import com.aureskull.zmcmod.networking.packet.MapControllerUpdateMapNameC2SPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static final Identifier EXAMPLE_ID = new Identifier(ZMCMod.MOD_ID, "example");
    public static final Identifier MAP_CONTROLLER_UPDATE_MAP_NAME = new Identifier(ZMCMod.MOD_ID, "mapcontroller_update_mapname");

    public static final Identifier EXIST_ZMC_MAP_CHECKER = new Identifier(ZMCMod.MOD_ID, "existzmcmapchecker");
    public static final Identifier EXIST_ZMC_MAP_CHECKER_RESPONSE = new Identifier(ZMCMod.MOD_ID, "existzmcmapchecker_response");

    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(EXAMPLE_ID, ExampleC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(MAP_CONTROLLER_UPDATE_MAP_NAME, MapControllerUpdateMapNameC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(EXIST_ZMC_MAP_CHECKER, ExistZMCMapC2SPacket::receive);
    }

    public static void registerS2CPackets(){

    }

    public static void sendExistZMCMapCheckerResponse(ServerPlayerEntity player, boolean exists) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(exists);
        ServerPlayNetworking.send(player, EXIST_ZMC_MAP_CHECKER_RESPONSE, buf);
    }
}
