package com.adibarra.enchanttweaker.mixin.server.enhanced;

import com.adibarra.utils.ADMath;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * @description Scales the Binding Curse enchantment to have a chance of not dropping the item on death.
 * @environment Server
 */
@Mixin(value=PlayerInventory.class)
public abstract class MoreBindingMixin {

    @Unique
    private static final Random RAND = new Random();

    @Unique
    private static final Map<UUID, Map<Integer, ItemStack>> BOUND_ARMOR_CACHE = new HashMap<>();

    static {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            Map<Integer, ItemStack> savedItems = BOUND_ARMOR_CACHE.remove(newPlayer.getUuid());
            if (savedItems != null) {
                PlayerInventory inv = newPlayer.getInventory();
                for (Map.Entry<Integer, ItemStack> entry : savedItems.entrySet()) {
                    // In 1.21.11 armor slots are indices 36 to 39 in the main inventory logic
                    // setStack is the safest way to bypass private field access
                    inv.setStack(entry.getKey() + 36, entry.getValue());
                }
            }
        });
    }

    @Shadow @Final
    public DefaultedList<ItemStack> armor;

    @Shadow @Final
    public PlayerEntity player;

    @ModifyExpressionValue(
        method="dropAll",
        at=@At(
            ordinal=0,
            value="INVOKE",
            target="Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean enchanttweaker$moreBinding$modifyDropAll(boolean orig, @Local ItemStack stack) {
        if (orig) return true;
        if (!armor.contains(stack)) return false;

        RegistryEntry<Enchantment> bindingCurse = this.player.getRegistryManager()
            .getOrThrow(RegistryKeys.ENCHANTMENT)
            .getOrThrow(Enchantments.BINDING_CURSE);

        int bindingLevel = EnchantmentHelper.getLevel(bindingCurse, stack);

        if (RAND.nextFloat() > ADMath.clamp(1.1 - 0.1 * bindingLevel, 0.1, 1.0)) {
            Map<Integer, ItemStack> playerCache = BOUND_ARMOR_CACHE.computeIfAbsent(this.player.getUuid(), k -> new HashMap<>());
            // Store the relative index in the armor list
            playerCache.put(armor.indexOf(stack), stack.copy());
            return true;
        }
        return false;
    }
}
