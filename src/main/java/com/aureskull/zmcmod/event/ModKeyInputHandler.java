package com.aureskull.zmcmod.event;

import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.screen.NoZMCMapSavedScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class ModKeyInputHandler {
    public static final String KEY_CATEGORY_ZMC = "key.category.zmcmod";
    public static final String KEY_OPEN_MAP_EDITOR = "key.zmcmod.openmapeditor";

    public static KeyBinding OPEN_MAP_EDITOR_GUI;

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
    }

    public static void register(){
        OPEN_MAP_EDITOR_GUI = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_OPEN_MAP_EDITOR,
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                KEY_CATEGORY_ZMC
        ));

        registerKeyInputs();
    }
}
