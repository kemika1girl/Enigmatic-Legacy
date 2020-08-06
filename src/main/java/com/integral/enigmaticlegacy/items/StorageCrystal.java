package com.integral.enigmaticlegacy.items;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.integral.enigmaticlegacy.EnigmaticLegacy;
import com.integral.enigmaticlegacy.api.items.IPermanentCrystal;
import com.integral.enigmaticlegacy.config.ConfigHandler;
import com.integral.enigmaticlegacy.helpers.ExperienceHelper;
import com.integral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.integral.enigmaticlegacy.items.generic.ItemBase;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.entity.item.ItemEntity;

public class StorageCrystal extends ItemBase implements IPermanentCrystal {

	public StorageCrystal() {
		super(ItemBase.getDefaultProperties().rarity(Rarity.EPIC).maxStackSize(1).func_234689_a_().group(null));
		this.setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, "storage_crystal"));
	}
	
	public ItemStack storeDropsOnCrystal(Collection<ItemEntity> drops, PlayerEntity player, @Nullable ItemStack embeddedSoulCrystal) {
		ItemStack crystal = new ItemStack(this);
		CompoundNBT crystalNBT = ItemNBTHelper.getNBT(crystal);
		int counter = 0;
		
		for (ItemEntity drop : drops) {
			ItemStack dropStack = drop.getItem();
			CompoundNBT nbt = dropStack.serializeNBT();
			crystalNBT.put("storedStack" + counter, nbt);

			counter++;
		}
		
		if (embeddedSoulCrystal != null) {
			CompoundNBT deserializedCrystal = new CompoundNBT();
			embeddedSoulCrystal.deserializeNBT(deserializedCrystal);
			
			crystalNBT.put("embeddedSoul", deserializedCrystal);
		}
		
		ItemNBTHelper.setInt(crystal, "storedStacks", counter);
		
		int exp = ExperienceHelper.getPlayerXP(player);
		ExperienceHelper.drainPlayerXP(player, exp);
		
		ItemNBTHelper.setInt(crystal, "storedXP", exp);
		ItemNBTHelper.setBoolean(crystal, "isStored", true);
		
		return crystal;
	}
	
	public ItemStack retrieveDropsFromCrystal(ItemStack crystal, PlayerEntity player, ItemStack retrieveSoul) {
		CompoundNBT crystalNBT = ItemNBTHelper.getNBT(crystal);
		int counter = crystalNBT.getInt("storedStacks")-1;
		int exp = crystalNBT.getInt("storedXP");
		
		for (int c = counter; c >= 0; c--) {
			CompoundNBT nbt = crystalNBT.getCompound("storedStack" + c);
			ItemStack stack = ItemStack.read((CompoundNBT)nbt);
			if (!player.inventory.addItemStackToInventory(stack)) {
				ItemEntity drop = new ItemEntity(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), stack);
				player.world.addEntity(drop);
			}
			crystalNBT.remove("storedStack" + c);
		}
		
		ExperienceHelper.addPlayerXP(player, exp);
		
		if (retrieveSoul != null) {
			EnigmaticLegacy.soulCrystal.retrieveSoulFromCrystal(player, retrieveSoul);
		} else
			player.world.playSound(null, new BlockPos(player.getPositionVec()), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.0f, 1.0f);
			
		ItemNBTHelper.setBoolean(crystal, "isStored", false);
		ItemNBTHelper.setInt(crystal, "storedStacks", 0);
		ItemNBTHelper.setInt(crystal, "storedXP", 0);
		
		return crystal;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		playerIn.setActiveHand(handIn);
		
		if (!worldIn.isRemote) {}
		
		return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
	}
	
}
