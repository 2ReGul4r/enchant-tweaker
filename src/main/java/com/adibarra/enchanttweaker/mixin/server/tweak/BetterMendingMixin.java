package com.adibarra.enchanttweaker.mixin.server.tweak;

import com.adibarra.utils.ADUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @description Lets Mending effect activate on any item in the inventory.
 * Priority order: Main-Hand -> Off-Hand -> Armor -> Hotbar -> Inventory.
 * @environment Server
 */
@Mixin(value=ExperienceOrbEntity.class)
public abstract class BetterMendingMixin {

    @Shadow
    private int amount;

    @Shadow
    protected abstract int getMendingRepairAmount(int i);

    @Shadow
    protected abstract int getMendingRepairCost(int i);

    @Shadow
    protected abstract int repairPlayerGears(PlayerEntity player, int amount);

    @Inject(
        method="repairPlayerGears",
        at=@At("HEAD"),
        cancellable=true)
    private void enchanttweaker$betterMending$modifyRepairPlayerGears(PlayerEntity player, int amount, CallbackInfoReturnable<Integer> cir) {
        PlayerInventory inv = player.getInventory();

        RegistryEntry<Enchantment> mending = player.getRegistryManager()
            .getOrThrow(RegistryKeys.ENCHANTMENT)
            .getOrThrow(Enchantments.MENDING);

        // Manually build lists to bypass private field access in 1.21.11
        List<ItemStack> armorList = new ArrayList<>();
        for (int i = 0; i < 4; i++) armorList.add(inv.getStack(36 + i));

        List<ItemStack> mainList = new ArrayList<>();
        for (int i = 0; i < 36; i++) mainList.add(inv.getStack(i));

        ItemStack repairItem = ADUtils.getMatchingItem(
            List.of(
                new ADUtils.Inventory(player.getMainHandStack()),
                new ADUtils.Inventory(player.getOffHandStack()),
                new ADUtils.Inventory(armorList),
                ADUtils.getPlayerHotbar(player),
                new ADUtils.Inventory(mainList)
            ),
            (stack) -> EnchantmentHelper.getLevel(mending, stack) > 0 && stack.isDamaged()
        );

        if (repairItem == null) {
            cir.setReturnValue(amount);
            return;
        }

        int i = Math.min(this.getMendingRepairAmount(this.amount), repairItem.getDamage());
        repairItem.setDamage(repairItem.getDamage() - i);
        int experience = amount - this.getMendingRepairCost(i);
        if (experience > 0) {
            cir.setReturnValue(this.repairPlayerGears(player, experience));
            return;
        }
        cir.setReturnValue(0);
    }
}
