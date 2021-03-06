package com.integral.enigmaticlegacy.triggers;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.integral.enigmaticlegacy.EnigmaticLegacy;

import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

/**
 * Special trigger that activates if player successfully beheads a mob with Axe of Executioner.
 * @author Integral
 */

public class BeheadingTrigger extends AbstractCriterionTrigger<BeheadingTrigger.Instance> {
	public static final ResourceLocation ID = new ResourceLocation(EnigmaticLegacy.MODID, "forbidden_axe_beheading");
	public static final BeheadingTrigger INSTANCE = new BeheadingTrigger();

	private BeheadingTrigger() {}

	@Nonnull
	@Override
	public ResourceLocation getId() {
		return BeheadingTrigger.ID;
	}

	@Nonnull
	@Override
	public BeheadingTrigger.Instance deserializeTrigger(@Nonnull JsonObject json, @Nonnull EntityPredicate.AndPredicate playerPred, ConditionArrayParser conditions) {
		return new BeheadingTrigger.Instance(playerPred);
	}

	public void trigger(ServerPlayerEntity player) {
		this.triggerListeners(player, instance -> instance.test());
	}

	static class Instance extends CriterionInstance {
		Instance(EntityPredicate.AndPredicate playerPred) {
			super(BeheadingTrigger.ID, playerPred);
		}

		@Nonnull
		@Override
		public ResourceLocation getId() {
			return BeheadingTrigger.ID;
		}

		boolean test() {
			return true;
		}
	}

}