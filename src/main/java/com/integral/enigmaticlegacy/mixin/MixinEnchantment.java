package com.integral.enigmaticlegacy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.integral.enigmaticlegacy.items.generic.ItemBaseCurio;

import net.minecraft.enchantment.BindingCurseEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.type.capability.ICurio;

@Mixin(Enchantment.class)
public class MixinEnchantment {

	@Inject(at = @At("HEAD"), method = "canApply", cancellable = true)
	public void allowEnchantment(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		Object forgottenObject = this;

		if (Enchantments.BINDING_CURSE == forgottenObject) {
			if (stack != null && stack.getItem() instanceof ItemBaseCurio) {
				info.setReturnValue(true);
				return;
			}
		}
	}

}
