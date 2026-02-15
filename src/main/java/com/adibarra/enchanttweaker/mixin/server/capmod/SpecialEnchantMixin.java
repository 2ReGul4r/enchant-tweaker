package com.adibarra.enchanttweaker.mixin.server.capmod;

import com.adibarra.enchanttweaker.ETMixinPlugin;
import com.adibarra.utils.ADMath;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

/**
 * @description Modify special enchantment level cap (Flame, Infinity, Mending, etc.)
 * @environment Server
 */
@Mixin(Enchantment.class)
public abstract class SpecialEnchantMixin {

    @Shadow @Final
    private Text description;

    private static final Set<String> SPECIAL_ENCHANTS = Set.of(
        "flame", "infinity", "mending", "multishot", "silk_touch",
        "vanishing_curse", "aqua_affinity", "binding_curse", "channeling"
    );

    @ModifyReturnValue(
        method = "getMaxLevel",
        at = @At("RETURN")
    )
    private int enchanttweaker$modifySpecialMaxLevel(int orig) {
        if (this.description.getContent() instanceof TranslatableTextContent translatable) {
            String translationKey = translatable.getKey();
            String key = translationKey.substring(translationKey.lastIndexOf('.') + 1);

            if (!SPECIAL_ENCHANTS.contains(key)) {
                return orig;
            }

            int lvlCap = ETMixinPlugin.getConfig().getOrDefault(key, orig);
            if (lvlCap < 0) return orig;
            return ADMath.clamp(lvlCap, 0, 255);
        }

        return orig;
    }
}
