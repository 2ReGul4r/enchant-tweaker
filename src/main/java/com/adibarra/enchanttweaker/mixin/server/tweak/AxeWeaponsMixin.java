package com.adibarra.enchanttweaker.mixin.server.tweak;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @description Allow axes to be enchanted with fire aspect, knockback, and looting.
 * @environment Server
 */
@Mixin(value=Enchantment.class)
public abstract class AxeWeaponsMixin {

    @Shadow @Final
    private Text description;

    @Inject(
        method="isAcceptableItem",
        at=@At("HEAD"),
        cancellable=true)
    private void enchanttweaker$axeWeapons$allowFireAspectKnockbackLooting(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(stack.getItem() instanceof AxeItem)) return;

        if (this.description.getContent() instanceof TranslatableTextContent translatable) {
            String key = translatable.getKey();

            // Check for the specific enchantment paths in 1.21.11
            if (key.endsWith(".fire_aspect") ||
                key.endsWith(".knockback") ||
                key.endsWith(".looting")) {
                cir.setReturnValue(true);
            }
        }
    }
}
