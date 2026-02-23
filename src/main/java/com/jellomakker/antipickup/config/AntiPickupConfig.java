package com.jellomakker.antipickup.config;

import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class AntiPickupConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("anti_pickup.json");

    public boolean enabled = true;
    public boolean whitelistMode = false;
    public boolean inventoryOnly = true; // Only auto-drop when inventory screen is open (default)
    public boolean totemMode = false; // Only work if totem in offhand or totem hotbar slot
    public int totemSlot = 7; // Hotbar slot to check for totem (1-9, default 7)

    // Items
    public boolean blockArmor = false;
    public boolean blockSword = false;
    public boolean blockTools = false;
    public boolean blockTotems = false;
    public boolean blockCrystals = false;
    public boolean blockAnchors = false;
    public boolean blockGlowstone = false;
    public boolean blockObsidian = false;
    public boolean blockBlocks = false; // Block ALL blocks except glowstone, obsidian, respawn anchor
    public Set<String> customItems = new HashSet<>();

    // Blocks (exclude anchors, glowstone, obsidian from block section per user request)
    public Set<String> customBlocks = new HashSet<>();

    public static AntiPickupConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                AntiPickupConfig config = GSON.fromJson(json, AntiPickupConfig.class);
                if (config == null) config = new AntiPickupConfig();
                if (config.customItems == null) config.customItems = new HashSet<>();
                if (config.customBlocks == null) config.customBlocks = new HashSet<>();
                return config;
            } catch (Exception e) {
                e.printStackTrace();
                return new AntiPickupConfig();
            }
        }
        return new AntiPickupConfig();
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = GSON.toJson(this);
            Files.writeString(CONFIG_PATH, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
