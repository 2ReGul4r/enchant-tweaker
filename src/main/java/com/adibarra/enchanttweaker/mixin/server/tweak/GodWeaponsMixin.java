package com.adibarra.enchanttweaker.mixin.server.tweak;

import com.adibarra.enchanttweaker.ETMixinPlugin;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public abstract class GodWeaponsMixin {

    @Shadow @Final
    private Text description;

    @ModifyReturnValue(
        method = "getMaxLevel",
        at = @At("RETURN")
    )
    private int enchanttweaker$godWeapons$modifyMaxLevel(int orig) {
        if (this.description.getContent() instanceof TranslatableTextContent translatable) {
            String translationKey = translatable.getKey();
            String key = translationKey.substring(translationKey.lastIndexOf('.') + 1);

            if (!key.equals("sharpness") && !key.equals("smite") && !key.equals("bane_of_arthropods")) {
                return orig;
            }

            int lvlCap = ETMixinPlugin.getConfig().getOrDefault(key, orig);
            if (lvlCap < 0) return orig;
            return Math.max(0, Math.min(lvlCap, 255));
        }

        return orig;
    }
}
