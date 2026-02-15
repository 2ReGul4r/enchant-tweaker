package com.adibarra.enchanttweaker.mixin.server.capmod;

import com.adibarra.enchanttweaker.ETMixinPlugin;
import com.adibarra.utils.ADMath;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.text.Text;

/**
 * @description Modify enchantment level cap.
 * @environment Server
 */
@Mixin(Enchantment.class)
public abstract class LuckEnchantMixin {

    @Shadow @Final
    private Text description;

    @ModifyReturnValue(
        method = "getMaxLevel",
        at = @At("RETURN")
    )
    private int enchanttweaker$modifyMaxLevel(int orig) {
        if (this.description.getContent() instanceof TranslatableTextContent translatable) {
            String translationKey = translatable.getKey();
            String key = translationKey.substring(translationKey.lastIndexOf('.') + 1);

            int lvlCap = ETMixinPlugin.getConfig().getOrDefault(key, orig);
            if (lvlCap < 0) return orig;
            return ADMath.clamp(lvlCap, 0, 255);
        }

        return orig;
    }
}
