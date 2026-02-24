package com.reiasu.reiparticleskill.enchantments;

import com.reiasu.reiparticleskill.ReiParticleSkillForge;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.IEventBus;

public final class SkillEnchantments {

    public static final ResourceKey<Enchantment> SWORD_LIGHT =
            ResourceKey.create(Registries.ENCHANTMENT,
                    ResourceLocation.fromNamespaceAndPath(ReiParticleSkillForge.MOD_ID, "sword_light"));

    private static Holder<Enchantment> cachedSwordLight;

    private SkillEnchantments() {
    }

    public static void register(IEventBus bus) {
        // No-op: enchantments are data-driven in 1.21+
    }

    public static int getSwordLightLevel(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        Holder<Enchantment> holder = getSwordLightHolder(stack);
        if (holder == null) {
            return 0;
        }
        return EnchantmentHelper.getItemEnchantmentLevel(holder, stack);
    }

    private static Holder<Enchantment> getSwordLightHolder(ItemStack stack) {
        if (cachedSwordLight != null) {
            return cachedSwordLight;
        }
        // Walk the item's enchantments to find our key
        for (Holder<Enchantment> h : EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet()) {
            if (h.is(SWORD_LIGHT)) {
                cachedSwordLight = h;
                return h;
            }
        }
        return null;
    }
}
