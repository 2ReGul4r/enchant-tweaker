package com.adibarra.enchanttweaker.mixin.server.enhanced;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @description Scales the burn time of the Flame enchant based on its level.
 * Adds 2 seconds of burn time per level above 1.
 * @environment Server
 */
@Mixin(value=PersistentProjectileEntity.class)
public abstract class MoreFlameMixin {

    @Shadow
    public abstract ItemStack getItemStack();

    @Shadow
    public abstract World getWorld();

    @Unique
    private int flameLevel = 0;

    @Inject(
        method="onEntityHit(Lnet/minecraft/util/hit/EntityHitResult;)V",
        at=@At("HEAD"))
    private void enchanttweaker$moreFlame$captureFlameLevel(EntityHitResult entityHitResult, CallbackInfo ci) {
        World world = this.getWorld();

        RegistryEntry<Enchantment> flame = world.getRegistryManager()
            .getOrThrow(RegistryKeys.ENCHANTMENT)
            .getOrThrow(Enchantments.FLAME);

        flameLevel = EnchantmentHelper.getLevel(flame, this.getItemStack());
    }

    @ModifyConstant(
        method="onEntityHit(Lnet/minecraft/util/hit/EntityHitResult;)V",
        constant=@Constant(intValue=5))
    private int enchanttweaker$moreFlame$modifyBurnTime(int orig) {
        if (flameLevel > 1) {
            return 2 * (flameLevel - 1) + orig;
        }
        return orig;
    }
}
