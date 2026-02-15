package com.adibarra.enchanttweaker.mixin.server.tweak;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(Enchantment.class)
public abstract class InfiniteMendingMixin {

    @Shadow @Final
    private Text description;

    @ModifyReturnValue(
        method = "canAccept",
        at = @At("RETURN")
    )
    private boolean enchanttweaker$infiniteMending$modifyCanAccept(boolean orig, RegistryEntry<Enchantment> other) {
        if (this.description.getContent() instanceof TranslatableTextContent translatable) {
            String translationKey = translatable.getKey();
            String key = translationKey.substring(translationKey.lastIndexOf('.') + 1);

            if (key.equals("mending") || key.equals("infinity")) {
                return true;
            }
        }

        return orig;
    }
}
