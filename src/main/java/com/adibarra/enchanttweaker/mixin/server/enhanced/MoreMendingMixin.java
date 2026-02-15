package com.adibarra.enchanttweaker.mixin.server.enhanced;

import com.adibarra.utils.ADMath;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @description Scales the efficiency of the Mending enchant based on its level.
 * @environment Server
 */
@Mixin(value=ExperienceOrbEntity.class)
public abstract class MoreMendingMixin {

    @Unique
    private int mendingLevel = 0;

    @Inject(
        method="repairPlayerGears",
        at=@At(
            value="INVOKE",
            target="Lnet/minecraft/enchantment/EnchantmentHelper;chooseEquipmentWith(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Optional;"))
    private void enchanttweaker$moreMending$captureMendingLevel(PlayerEntity player, int amount, CallbackInfoReturnable<Integer> cir) {
        RegistryEntry<Enchantment> mending = player.getRegistryManager()
            .getOrThrow(RegistryKeys.ENCHANTMENT)
            .getOrThrow(Enchantments.MENDING);

        // In 1.21.11, the method to get the level from all equipment is getEquipmentLevel
        this.mendingLevel = EnchantmentHelper.getEquipmentLevel(mending, player);
    }

    @Inject(
        method="getMendingRepairCost",
        at=@At("HEAD"),
        cancellable=true)
    private void enchanttweaker$moreMending$modifyRepairCost(int repairAmount, CallbackInfoReturnable<Integer> cir) {
        if (this.mendingLevel > 0) {
            cir.setReturnValue((int) Math.round(repairAmount * ADMath.clamp(0.6 - 0.05 * mendingLevel, 0.1, 0.6)));
        }
    }
}
