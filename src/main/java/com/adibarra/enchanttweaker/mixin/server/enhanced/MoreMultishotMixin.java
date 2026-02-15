package com.adibarra.enchanttweaker.mixin.server.enhanced;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * @description Scales the number of the arrows fired by Multishot enchant based on its level.
 * @environment Server
 */
@Mixin(value=CrossbowItem.class)
public abstract class MoreMultishotMixin {

    @Shadow
    private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated) { }

    @Shadow
    private static void postShoot(World world, LivingEntity entity, ItemStack stack) { }

    @Shadow
    private static List<ItemStack> getProjectiles(ItemStack stack) { return List.of(); }

    @Shadow
    private static float[] getSoundPitches(Random random) { return new float[0]; }

    @Unique
    private static int multishotLevel = 0;

    @ModifyConstant(
        method="loadProjectiles",
        constant=@Constant(intValue=3))
    private static int enchanttweaker$moreMultishot$modifyNumProjectiles(int orig, LivingEntity shooter, ItemStack stack) {
        RegistryEntry<Enchantment> multishot = shooter.getRegistryManager()
            .getOrThrow(RegistryKeys.ENCHANTMENT)
            .getOrThrow(Enchantments.MULTISHOT);

        multishotLevel = EnchantmentHelper.getLevel(multishot, stack);
        return multishotLevel * 2 + 1;
    }

    @Inject(
        method="shootAll",
        at=@At(
            ordinal=0,
            value="INVOKE",
            target="Lnet/minecraft/item/CrossbowItem;getSoundPitches(Lnet/minecraft/util/math/random/Random;)[F"),
        cancellable=true)
    private static void enchanttweaker$moreMultishot$modifyShootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence, LivingEntity shooter, CallbackInfo ci) {
        List<ItemStack> list = getProjectiles(stack);
        float[] fs = getSoundPitches(entity.getRandom());
        float range = Math.max(10.0F, list.size() * 0.2F);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemStack = list.get(i);
            boolean bl = entity instanceof PlayerEntity && ((PlayerEntity) entity).getAbilities().creativeMode;
            if (!itemStack.isEmpty()) {
                if (i == 0) {
                    shoot(world, entity, hand, stack, itemStack, fs[0], bl, speed, divergence, 0.0F);
                } else {
                    float angle = (i % 2 != 0) ? -range * ((i + 1) / 2f / list.size()) : range * (i / 2f / list.size());
                    float pitch = (i < fs.length) ? fs[i] : fs[i % 3];
                    shoot(world, entity, hand, stack, itemStack, pitch, bl, speed, divergence, angle);
                }
            }
        }

        postShoot(world, entity, stack);
        ci.cancel();
    }
}
