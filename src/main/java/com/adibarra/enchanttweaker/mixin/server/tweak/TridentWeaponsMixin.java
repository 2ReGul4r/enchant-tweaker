package com.adibarra.enchanttweaker.mixin.server.tweak;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(Enchantment.class)
public abstract class TridentWeaponsMixin {

    @ModifyReturnValue(
        method = "canAccept",
        at = @At("RETURN")
    )
    private boolean enchanttweaker$tridentWeapons$modifyAccept(boolean orig, RegistryEntry<Enchantment> other) {
        return other.getKeyOrValue().map(
            key -> {
                String path = key.getValue().getPath();

                if (path.equals("sharpness") || path.equals("smite") || path.equals("fire_aspect") ||
                    path.equals("knockback") || path.equals("looting")) {
                    return false;
                }
                return orig;
            },
            value -> orig
        );
    }
}
