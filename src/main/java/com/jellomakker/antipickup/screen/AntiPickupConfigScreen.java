package com.jellomakker.antipickup.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import com.jellomakker.antipickup.AntiPickupMod;
import com.jellomakker.antipickup.config.AntiPickupConfig;

public class AntiPickupConfigScreen extends Screen {
    private final Screen parent;
    private AntiPickupConfig config;

    private TextFieldWidget itemInputField;
    private TextFieldWidget blockInputField;
    private TextFieldWidget totemSlotField;

    public AntiPickupConfigScreen(Screen parent) {
        super(Text.literal("Anti-Pickup Configuration"));
        this.parent = parent;
        this.config = AntiPickupMod.CONFIG;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 155;
        int y = 30;

        // === HEADER ===
        y += 5;

        // Enable/Disable
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Enabled: " + (config.enabled ? "ON" : "OFF")),
            button -> {
                config.enabled = !config.enabled;
                button.setMessage(Text.literal("Enabled: " + (config.enabled ? "ON" : "OFF")));
                config.save();
            }).dimensions(x, y, 150, 20).build());

        // Whitelist/Blacklist mode
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Mode: " + (config.whitelistMode ? "WHITELIST" : "BLACKLIST")),
            button -> {
                config.whitelistMode = !config.whitelistMode;
                button.setMessage(Text.literal("Mode: " + (config.whitelistMode ? "WHITELIST" : "BLACKLIST")));
                config.save();
            }).dimensions(x + 160, y, 150, 20).build());
        y += 25;

        // Inventory Only toggle
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Inventory Only: " + (config.inventoryOnly ? "ON" : "OFF")),
            button -> {
                config.inventoryOnly = !config.inventoryOnly;
                button.setMessage(Text.literal("Inventory Only: " + (config.inventoryOnly ? "ON" : "OFF")));
                config.save();
            }).dimensions(x, y, 310, 20).build());
        y += 25;

        // Totem Mode toggle
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Totem Mode: " + (config.totemMode ? "ON" : "OFF")),
            button -> {
                config.totemMode = !config.totemMode;
                button.setMessage(Text.literal("Totem Mode: " + (config.totemMode ? "ON" : "OFF")));
                config.save();
            }).dimensions(x, y, 150, 20).build());

        // Totem Slot input (1-9)
        totemSlotField = new TextFieldWidget(this.textRenderer, x + 160, y, 40, 20, Text.literal("Slot"));
        totemSlotField.setMaxLength(1);
        totemSlotField.setText(String.valueOf(config.totemSlot));
        totemSlotField.setChangedListener(text -> {
            try {
                int slot = Integer.parseInt(text);
                if (slot >= 1 && slot <= 9) {
                    config.totemSlot = slot;
                    config.save();
                }
            } catch (NumberFormatException ignored) {}
        });
        addSelectableChild(totemSlotField);
        addDrawableChild(totemSlotField);

        addDrawableChild(ButtonWidget.builder(Text.literal("Totem Slot (1-9)"), button -> {})
                .dimensions(x + 210, y, 100, 20).build());
        y += 25;

        // === ITEM TOGGLES ===
        addDrawableChild(ButtonWidget.builder(Text.literal("--- Item Filters ---"), button -> {})
                .dimensions(x, y, 310, 20).build());
        y += 22;

        y = addToggle(x, y, "Armor", config.blockArmor, v -> config.blockArmor = v);
        y = addToggle(x, y, "Sword", config.blockSword, v -> config.blockSword = v);
        y = addToggle(x, y, "Tools", config.blockTools, v -> config.blockTools = v);
        y = addToggle(x, y, "Totems", config.blockTotems, v -> config.blockTotems = v);
        y = addToggle(x, y, "Crystals", config.blockCrystals, v -> config.blockCrystals = v);
        y = addToggle(x, y, "Anchors", config.blockAnchors, v -> config.blockAnchors = v);
        y = addToggle(x, y, "Glowstone", config.blockGlowstone, v -> config.blockGlowstone = v);
        y = addToggle(x, y, "Obsidian", config.blockObsidian, v -> config.blockObsidian = v);
        y = addToggle(x, y, "Blocks", config.blockBlocks, v -> config.blockBlocks = v);

        // === CUSTOM ITEM INPUT ===
        y += 5;
        addDrawableChild(ButtonWidget.builder(Text.literal("--- Custom Items ---"), button -> {})
                .dimensions(x, y, 310, 20).build());
        y += 22;

        itemInputField = new TextFieldWidget(this.textRenderer, x, y, 200, 20, Text.literal("Item ID"));
        itemInputField.setMaxLength(100);
        itemInputField.setPlaceholder(Text.literal("minecraft:diamond"));
        addSelectableChild(itemInputField);
        addDrawableChild(itemInputField);

        addDrawableChild(ButtonWidget.builder(Text.literal("Add"), button -> {
            String id = itemInputField.getText().trim();
            if (!id.isEmpty()) {
                if (!id.contains(":")) id = "minecraft:" + id;
                config.customItems.add(id);
                config.save();
                itemInputField.setText("");
            }
        }).dimensions(x + 210, y, 45, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Clear"), button -> {
            config.customItems.clear();
            config.save();
        }).dimensions(x + 260, y, 50, 20).build());
        y += 25;

        // === BLOCK SECTION (exclude anchors, glowstone, obsidian per request) ===
        addDrawableChild(ButtonWidget.builder(Text.literal("--- Custom Blocks ---"), button -> {})
                .dimensions(x, y, 310, 20).build());
        y += 22;

        blockInputField = new TextFieldWidget(this.textRenderer, x, y, 200, 20, Text.literal("Block ID"));
        blockInputField.setMaxLength(100);
        blockInputField.setPlaceholder(Text.literal("minecraft:chest"));
        addSelectableChild(blockInputField);
        addDrawableChild(blockInputField);

        addDrawableChild(ButtonWidget.builder(Text.literal("Add"), button -> {
            String id = blockInputField.getText().trim();
            if (!id.isEmpty()) {
                if (!id.contains(":")) id = "minecraft:" + id;
                config.customBlocks.add(id);
                config.save();
                blockInputField.setText("");
            }
        }).dimensions(x + 210, y, 45, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Clear"), button -> {
            config.customBlocks.clear();
            config.save();
        }).dimensions(x + 260, y, 50, 20).build());
        y += 30;

        // Back button
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> {
            config.save();
            this.client.setScreen(parent);
        }).dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build());
    }

    private int addToggle(int x, int y, String label, boolean currentValue, java.util.function.Consumer<Boolean> setter) {
        addDrawableChild(ButtonWidget.builder(
            Text.literal(label + ": " + (currentValue ? "ON" : "OFF")),
            button -> {
                boolean newVal = !label.equals(button.getMessage().getString().split(":")[0].trim()) || !currentValue;
                // Re-read from config to get current state
                boolean cur = getConfigValue(label);
                boolean next = !cur;
                setConfigValue(label, next);
                button.setMessage(Text.literal(label + ": " + (next ? "ON" : "OFF")));
                config.save();
            }).dimensions(x, y, 310, 20).build());
        return y + 22;
    }

    private boolean getConfigValue(String label) {
        return switch (label) {
            case "Armor" -> config.blockArmor;
            case "Sword" -> config.blockSword;
            case "Tools" -> config.blockTools;
            case "Totems" -> config.blockTotems;
            case "Crystals" -> config.blockCrystals;
            case "Anchors" -> config.blockAnchors;
            case "Glowstone" -> config.blockGlowstone;
            case "Obsidian" -> config.blockObsidian;
            case "Blocks" -> config.blockBlocks;
            default -> false;
        };
    }

    private void setConfigValue(String label, boolean value) {
        switch (label) {
            case "Armor" -> config.blockArmor = value;
            case "Sword" -> config.blockSword = value;
            case "Tools" -> config.blockTools = value;
            case "Totems" -> config.blockTotems = value;
            case "Crystals" -> config.blockCrystals = value;
            case "Anchors" -> config.blockAnchors = value;
            case "Glowstone" -> config.blockGlowstone = value;
            case "Obsidian" -> config.blockObsidian = value;
            case "Blocks" -> config.blockBlocks = value;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        // Draw custom items list
        int yPos = this.height / 2 + 60;
        if (!config.customItems.isEmpty()) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Items: " + String.join(", ", config.customItems)), 10, yPos, 0xCCCCCC);
            yPos += 12;
        }
        if (!config.customBlocks.isEmpty()) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("Blocks: " + String.join(", ", config.customBlocks)), 10, yPos, 0xCCCCCC);
        }

    }

    @Override
    public void close() {
        config.save();
        this.client.setScreen(parent);
    }
}