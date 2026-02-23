package com.jellomakker.antipickup;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import com.jellomakker.antipickup.config.AntiPickupConfig;
import com.jellomakker.antipickup.event.ItemPickupHandler;

public class AntiPickupMod implements ClientModInitializer {
    public static final String MOD_ID = "antipickup";
    public static AntiPickupConfig CONFIG;

    @Override
    public void onInitializeClient() {
        CONFIG = AntiPickupConfig.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.interactionManager != null) {
                ItemPickupHandler.tick(client);
            }
        });

        System.out.println("[AntiPickup] Mod initialized!");
    }
}
