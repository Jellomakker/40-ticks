package com.jellomakker.antipickup.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import com.jellomakker.antipickup.AntiPickupMod;
import com.jellomakker.antipickup.config.AntiPickupConfig;
import com.jellomakker.antipickup.util.ItemCategories;
import org.lwjgl.glfw.GLFW;

public class ItemPickupHandler {

    private enum State { IDLE, MOVED_CURSOR, DROP }

    private static State state = State.IDLE;
    private static int cooldown = 0;
    private static int targetSlotId = -1;
    private static double savedMouseX = 0;
    private static double savedMouseY = 0;
    private static boolean savedMousePos = false;
    private static final int DROP_DELAY = 3; // ticks between drops

    /**
     * Called every client tick.
     */
    public static void tick(MinecraftClient client) {
        AntiPickupConfig config = AntiPickupMod.CONFIG;
        if (!config.enabled) {
            resetState();
            return;
        }

        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null) {
            resetState();
            return;
        }

        // Totem mode check: only work if totem in offhand or configured hotbar slot
        if (config.totemMode && !hasTotemReady(player, config)) {
            resetState();
            return;
        }

        // Check if we should only work in inventory
        if (config.inventoryOnly) {
            if (!(client.currentScreen instanceof HandledScreen<?>)) {
                resetState();
                return;
            }
            tickInventoryDrop(client, player, config);
        } else {
            if (client.currentScreen != null) return;
            tickDirectDrop(client, player, config);
        }
    }

    /**
     * Check if player has a totem in offhand or the configured hotbar slot.
     */
    private static boolean hasTotemReady(ClientPlayerEntity player, AntiPickupConfig config) {
        // Check offhand
        ItemStack offhand = player.getOffHandStack();
        if (offhand.getItem() == Items.TOTEM_OF_UNDYING) return true;

        // Check configured hotbar slot (config is 1-9, inventory is 0-8)
        int slotIndex = config.totemSlot - 1;
        if (slotIndex >= 0 && slotIndex < 9) {
            ItemStack hotbarStack = player.getInventory().getStack(slotIndex);
            if (hotbarStack.getItem() == Items.TOTEM_OF_UNDYING) return true;
        }

        return false;
    }

    /**
     * Inventory-open mode: moves cursor to slot visually, then drops via clickSlot.
     */
    private static void tickInventoryDrop(MinecraftClient client, ClientPlayerEntity player, AntiPickupConfig config) {
        HandledScreen<?> screen = (HandledScreen<?>) client.currentScreen;

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        switch (state) {
            case IDLE -> {
                // Find the next unwanted item slot
                var handler = screen.getScreenHandler();
                for (Slot slot : handler.slots) {
                    ItemStack stack = slot.getStack();
                    if (stack.isEmpty()) continue;

                    if (shouldDrop(stack, config)) {
                        targetSlotId = slot.id;

                        // Calculate screen position of the slot center
                        // Access-widened x/y fields on HandledScreen (background offset)
                        int bgX = screen.x;
                        int bgY = screen.y;
                        // Slot x/y are relative to background top-left, +8 to center on 16x16 slot
                        double guiX = bgX + slot.x + 8;
                        double guiY = bgY + slot.y + 8;

                        // Convert scaled GUI coords to raw GLFW window pixels
                        double scaleX = (double) client.getWindow().getWidth() / client.getWindow().getScaledWidth();
                        double scaleY = (double) client.getWindow().getHeight() / client.getWindow().getScaledHeight();
                        double windowX = guiX * scaleX;
                        double windowY = guiY * scaleY;

                        // Save mouse position to restore later
                        if (!savedMousePos) {
                            savedMouseX = client.mouse.getX();
                            savedMouseY = client.mouse.getY();
                            savedMousePos = true;
                        }

                        // Move cursor to the slot
                        GLFW.glfwSetCursorPos(client.getWindow().getHandle(), windowX, windowY);

                        state = State.MOVED_CURSOR;
                        return;
                    }
                }
                // No more items to drop — restore cursor
                restoreCursor(client);
            }
            case MOVED_CURSOR -> {
                // Wait one tick for visual effect, then drop
                state = State.DROP;
            }
            case DROP -> {
                // Use clickSlot with THROW — sends the same packet as pressing Q on the slot
                // This works reliably regardless of focusedSlot state
                if (targetSlotId >= 0) {
                    var handler = screen.getScreenHandler();
                    client.interactionManager.clickSlot(
                        handler.syncId,
                        targetSlotId,
                        1, // button 1 = drop entire stack
                        SlotActionType.THROW,
                        player
                    );
                }

                // Restore cursor after the drop
                restoreCursor(client);

                state = State.IDLE;
                targetSlotId = -1;
                cooldown = DROP_DELAY;
            }
        }
    }

    /**
     * Direct drop mode when inventoryOnly is off (no screen needed).
     */
    private static void tickDirectDrop(MinecraftClient client, ClientPlayerEntity player, AntiPickupConfig config) {
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        var screenHandler = player.playerScreenHandler;
        for (Slot slot : screenHandler.slots) {
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;

            if (shouldDrop(stack, config)) {
                client.interactionManager.clickSlot(
                    screenHandler.syncId,
                    slot.id,
                    1, // button 1 = drop entire stack
                    SlotActionType.THROW,
                    player
                );
                cooldown = DROP_DELAY;
                return;
            }
        }
    }

    private static void restoreCursor(MinecraftClient client) {
        if (savedMousePos) {
            GLFW.glfwSetCursorPos(client.getWindow().getHandle(), savedMouseX, savedMouseY);
            savedMousePos = false;
        }
    }

    private static void resetState() {
        state = State.IDLE;
        targetSlotId = -1;
        cooldown = 0;
        savedMousePos = false;
    }

    private static boolean shouldDrop(ItemStack stack, AntiPickupConfig config) {
        String itemId = getItemId(stack);

        if (config.whitelistMode) {
            return !isInList(stack, itemId, config);
        } else {
            return isInList(stack, itemId, config);
        }
    }

    private static boolean isInList(ItemStack stack, String itemId, AntiPickupConfig config) {
        if (config.blockArmor && ItemCategories.isArmor(stack)) return true;
        if (config.blockSword && ItemCategories.isSword(stack)) return true;
        if (config.blockTools && ItemCategories.isTool(stack)) return true;
        if (config.blockTotems && ItemCategories.isTotem(stack)) return true;
        if (config.blockCrystals && ItemCategories.isCrystal(stack)) return true;
        if (config.blockAnchors && ItemCategories.isAnchor(stack)) return true;
        if (config.blockGlowstone && ItemCategories.isGlowstone(stack)) return true;
        if (config.blockObsidian && ItemCategories.isObsidian(stack)) return true;
        if (config.blockBlocks && ItemCategories.isGenericBlock(stack)) return true;
        if (config.customItems.contains(itemId)) return true;
        if (ItemCategories.isBlock(stack) && config.customBlocks.contains(itemId)) return true;
        return false;
    }

    private static String getItemId(ItemStack stack) {
        Identifier id = Registries.ITEM.getId(stack.getItem());
        return id != null ? id.toString() : "unknown";
    }
}
