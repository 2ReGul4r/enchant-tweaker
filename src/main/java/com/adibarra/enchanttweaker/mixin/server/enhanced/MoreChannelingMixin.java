package com.adibarra.enchanttweaker.mixin.server.enhanced;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @description Lets Channeling enchant work in rain at level 2.
 * @environment Server
 */
@Mixin(value=TridentEntity.class)
public abstract class MoreChannelingMixin extends PersistentProjectileEntity {

    @Shadow
    public abstract World getWorld();

    protected MoreChannelingMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyExpressionValue(
        method="onEntityHit",
        at=@At(
            ordinal=0,
            value="INVOKE",
            target="Lnet/minecraft/world/World;isThundering()Z"))
    private boolean enchanttweaker$moreChanneling$modifyOnHit(boolean orig) {
        World world = this.getWorld();

        RegistryEntry<Enchantment> channeling = world.getRegistryManager()
            .getOrThrow(RegistryKeys.ENCHANTMENT)
            .getOrThrow(Enchantments.CHANNELING);

        boolean isChannelingII = EnchantmentHelper.getLevel(channeling, this.getItemStack()) > 1;
        return orig || (isChannelingII && world.isRaining());
    }
}
