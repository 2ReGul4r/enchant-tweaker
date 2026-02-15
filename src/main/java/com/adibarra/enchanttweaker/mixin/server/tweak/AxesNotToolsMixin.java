package com.adibarra.enchanttweaker.mixin.server.tweak;

import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AxeItem.class)
public class AxesNotToolsMixin {

    @Redirect(
        method = "getMiningSpeed(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/BlockState;)F",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
        )
    )
    private Item enchanttweaker$axesNotTools(ItemStack stack) {
        return stack.getItem();
    }
}
