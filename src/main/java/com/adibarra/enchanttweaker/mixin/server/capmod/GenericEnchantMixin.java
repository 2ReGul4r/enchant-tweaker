package com.adibarra.enchanttweaker.mixin.server.capmod;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import java.util.Map;
import java.util.HashMap;

@Mixin(Enchantment.class)
public class GenericEnchantMixin {

    private static final Map<String, String> ENCHANTS = new HashMap<>();

    static {
        ENCHANTS.put("channeling", "channeling");
        ENCHANTS.put("depth_strider", "depth_strider");
        ENCHANTS.put("efficiency", "efficiency");
        ENCHANTS.put("fire_aspect", "fire_aspect");
        ENCHANTS.put("frost_walker", "frost_walker");
        ENCHANTS.put("impaling", "impaling");
        ENCHANTS.put("knockback", "knockback");
        ENCHANTS.put("loyalty", "loyalty");
        ENCHANTS.put("lure", "lure");
        ENCHANTS.put("piercing", "piercing");
        ENCHANTS.put("power", "power");
        ENCHANTS.put("punch", "punch");
        ENCHANTS.put("quick_charge", "quick_charge");
        ENCHANTS.put("respiration", "respiration");
        ENCHANTS.put("riptide", "riptide");
        ENCHANTS.put("soul_speed", "soul_speed");
        ENCHANTS.put("sweeping_edge", "sweeping_edge");
        ENCHANTS.put("swift_sneak", "swift_sneak");
        ENCHANTS.put("thorns", "thorns");
        ENCHANTS.put("unbreaking", "unbreaking");
    }

    /**
     * @author Adibarra
     * @description Modifiziert die Akzeptanz von Verzauberungskombinationen.
     * In 1.21.11 akzeptiert canAccept ein RegistryEntry anstelle einer Enchantment-Instanz.
     */
    @ModifyReturnValue(
        method = "canAccept(Lnet/minecraft/registry/entry/RegistryEntry;)Z",
        at = @At("RETURN")
    )
    private boolean enchanttweaker$generic$modifyAccept(boolean orig, RegistryEntry<Enchantment> other) {
        return other.getKeyOrValue().map(
            key -> {
                String path = key.getValue().getPath();

                if (ENCHANTS.containsKey(path)) {
                    return false;
                }
                return orig;
            },
            value -> orig
        );
    }
}
