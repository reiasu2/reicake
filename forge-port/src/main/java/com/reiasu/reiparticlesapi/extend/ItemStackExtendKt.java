package com.reiasu.reiparticlesapi.extend;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ItemStackExtendKt {

    private ItemStackExtendKt() {
    }

        public static boolean isOf(ItemStack stack, Item item) {
        return stack.is(item);
    }
}
