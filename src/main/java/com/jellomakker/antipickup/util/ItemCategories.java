package com.jellomakker.antipickup.util;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;

public class ItemCategories {

    public static boolean isArmor(ItemStack stack) {
        return stack.isIn(ItemTags.TRIMMABLE_ARMOR);
    }

    public static boolean isSword(ItemStack stack) {
        return stack.isIn(ItemTags.SWORDS);
    }

    public static boolean isTool(ItemStack stack) {
        return stack.isIn(ItemTags.PICKAXES) ||
               stack.isIn(ItemTags.AXES) ||
               stack.isIn(ItemTags.SHOVELS) ||
               stack.isIn(ItemTags.HOES);
    }

    public static boolean isTotem(ItemStack stack) {
        return stack.getItem() == Items.TOTEM_OF_UNDYING;
    }

    public static boolean isCrystal(ItemStack stack) {
        return stack.getItem() == Items.END_CRYSTAL;
    }

    public static boolean isAnchor(ItemStack stack) {
        return stack.getItem() == Items.RESPAWN_ANCHOR;
    }

    public static boolean isGlowstone(ItemStack stack) {
        return stack.getItem() == Items.GLOWSTONE;
    }

    public static boolean isObsidian(ItemStack stack) {
        return stack.getItem() == Items.OBSIDIAN;
    }

    public static boolean isBlock(ItemStack stack) {
        return stack.getItem() instanceof BlockItem;
    }

    /**
     * Returns true if the item is a block, excluding glowstone, obsidian, and respawn anchor
     * (those have their own dedicated toggles).
     */
    public static boolean isGenericBlock(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem)) return false;
        // Exclude blocks that have their own toggles
        if (isGlowstone(stack) || isObsidian(stack) || isAnchor(stack)) return false;
        return true;
    }
}
