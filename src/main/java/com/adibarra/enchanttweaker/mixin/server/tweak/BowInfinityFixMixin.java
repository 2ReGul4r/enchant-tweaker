package com.adibarra.enchanttweaker.mixin.server.tweak;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @description Lets bows with Infinity enchant shoot without arrows.
 * @environment Server
 */
@Mixin(value=BowItem.class)
public abstract class BowInfinityFixMixin {

    @ModifyExpressionValue(
        method="use",
        at=@At(
            ordinal=0,
            value="INVOKE",
            target="Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean enchanttweaker$bowInfinityFix$fireNoArrow(boolean orig, @Local PlayerEntity player, @Local(argsOnly = true) ItemStack stack) {
        // Resolve Infinity RegistryKey to RegistryEntry for 1.21.11
        RegistryEntry<Enchantment> infinity = player.getRegistryManager()
            .getOrThrow(RegistryKeys.ENCHANTMENT)
            .getOrThrow(Enchantments.INFINITY);

        if (EnchantmentHelper.getLevel(infinity, stack) > 0) {
            return false;
        }
        return orig;
    }
}
