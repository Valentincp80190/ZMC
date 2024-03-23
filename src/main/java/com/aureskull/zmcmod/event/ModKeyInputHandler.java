package com.aureskull.zmcmod.event;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.screen.NoZMCMapSavedScreen;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModKeyInputHandler {
    public static final String KEY_CATEGORY_ZMC = "key.category.zmcmod";
    public static final String KEY_OPEN_MAP_EDITOR = "key.zmcmod.openmapeditor";
    public static final String KEY_INTERACT = "key.zmcmod.interact";
    public static final String KEY_SHOW_HIDE_ZONES = "key.zmcmod.showhidezones";

    public static KeyBinding OPEN_MAP_EDITOR_GUI;
    public static KeyBinding INTERACT;
    public static KeyBinding SHOW_HIDE_ZONES;
    private static long lastActionTime = 0;

    public static boolean showZoneArea = false;

    public static void registerKeyInputs(){
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            while (OPEN_MAP_EDITOR_GUI.wasPressed()) {
                ClientPlayNetworking.send(ModMessages.EXIST_ZMC_MAP_CHECKER, PacketByteBufs.create());

                // Enregistrer un récepteur pour la réponse du serveur
                ClientPlayNetworking.registerGlobalReceiver(ModMessages.EXIST_ZMC_MAP_CHECKER_RESPONSE, (client2, handler, buf, responseSender) -> {
                    boolean exists = buf.readBoolean();
                    if (exists) {
                        // Si une map ZMC existe, ouvrir le GUI
                        MinecraftClient.getInstance().execute(() -> {//Pourquoi cette ligne ? => s'assurer que le code à l'intérieur du bloc execute() est exécuté sur le thread de rendu principal de Minecraft

                        });
                    }else{
                        MinecraftClient.getInstance().execute(() -> {
                            MinecraftClient.getInstance().setScreen(new NoZMCMapSavedScreen(MinecraftClient.getInstance().currentScreen));
                        });
                    }
                });
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (INTERACT.wasPressed()) {
                if (client.player != null) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastActionTime >= 1000) {
                        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                        buf.writeBlockPos(client.player.getBlockPos());
                        ClientPlayNetworking.send(ModMessages.TRIGGER_INTERACTION, buf);

                        lastActionTime = currentTime;
                    }
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (SHOW_HIDE_ZONES.wasPressed()) {
                if (client.player != null) {
                    showZoneArea = !showZoneArea;

                    if(showZoneArea)
                        client.player.sendMessage(Text.literal("State of zones: ")
                                .append(Text.literal("Displayed")
                                        .formatted(Formatting.GREEN)), true);
                    else
                        client.player.sendMessage(Text.literal("State of zones: ")
                                .append(Text.literal("Hidden")
                                        .formatted(Formatting.RED)), true);
                }
            }
        });
    }

    public static void register(){
        ZMCMod.LOGGER.info("Registering Key Binding for " + ZMCMod.MOD_ID);

        OPEN_MAP_EDITOR_GUI = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_OPEN_MAP_EDITOR,
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                KEY_CATEGORY_ZMC
        ));

        INTERACT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_INTERACT,
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                KEY_CATEGORY_ZMC
        ));

        SHOW_HIDE_ZONES = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_SHOW_HIDE_ZONES,
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                KEY_CATEGORY_ZMC
        ));

        registerKeyInputs();
    }
}
