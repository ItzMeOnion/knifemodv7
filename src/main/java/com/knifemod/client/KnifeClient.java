package com.knifemod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import org.lwjgl.glfw.GLFW;

public class KnifeClient implements ClientModInitializer {
    private static final String CATEGORY = "category.knifemod";

    private static KeyBinding openMenu;
    private static KeyBinding drawKey;
    private static KeyBinding inspectKey;

    @Override
    public void onInitializeClient() {
        // All three are rebindable under Options -> Controls -> Knife Cases
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.knifemod.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, CATEGORY));
        drawKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.knifemod.spin", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY));
        inspectKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.knifemod.inspect", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(KnifeClient::tick);
    }

    private static void tick(MinecraftClient client) {
        while (openMenu.wasPressed()) {
            client.setScreen(new CaseScreen());
        }
        if (client.player == null) return;

        PlayerInventory inv = client.player.getInventory();
        KnifeState.tick(inv);

        // G: draw the equipped knife from an empty hand, or put it away
        if (drawKey.wasPressed()) {
            KnifeState.toggle(inv);
        }

        // H: inspect. The inspect move is the next step; for now it also draws / puts away
        if (inspectKey.wasPressed()) {
            KnifeState.toggle(inv);
        }
    }
}
