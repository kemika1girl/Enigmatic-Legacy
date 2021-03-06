package com.integral.enigmaticlegacy.packets.clients;

import java.util.function.Supplier;

import com.integral.enigmaticlegacy.config.JsonConfigHandler;
import com.integral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.integral.enigmaticlegacy.objects.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketGenericParticleEffect {

	public enum Effect {
		NONE, GUARDIAN_CURSE;
	}

	private Vector3 pos;
	private int num;
	private boolean check;
	private Effect effect;

	public PacketGenericParticleEffect(double x, double y, double z, int number, boolean checkSettings, Effect effect) {
		this.pos = new Vector3(x, y, z);
		this.num = number;
		this.check = checkSettings;
		this.effect = effect;
	}

	public static void encode(PacketGenericParticleEffect msg, PacketBuffer buf) {
		buf.writeDouble(msg.pos.x);
		buf.writeDouble(msg.pos.y);
		buf.writeDouble(msg.pos.z);
		buf.writeInt(msg.num);
		buf.writeBoolean(msg.check);
		buf.writeString(msg.effect.toString(), 512);
	}

	public static PacketGenericParticleEffect decode(PacketBuffer buf) {


		return new PacketGenericParticleEffect(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readInt(), buf.readBoolean(), evaluateEffect(buf));
	}

	private static Effect evaluateEffect(PacketBuffer buf) {
		Effect effect;

		try {
			effect = Effect.valueOf(buf.readString(512));
		} catch (Exception ex) {
			ex.printStackTrace();
			effect = Effect.NONE;
		}

		return effect;
	}


	public static void handle(PacketGenericParticleEffect msg, Supplier<NetworkEvent.Context> ctx) {

		ctx.get().enqueueWork(() -> {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			Vector3 pos = msg.pos;

			int amount = msg.num;

			if (msg.check) {
				amount *= SuperpositionHandler.getParticleMultiplier();
			}

			if (msg.effect == Effect.GUARDIAN_CURSE) {
				double dist = 0.05;
				double distHearts = 0.5;

				for (int counter = 0; counter < 4; counter++) {
					player.world.addParticle(ParticleTypes.ANGRY_VILLAGER, true, pos.x+SuperpositionHandler.getRandomNegative()*distHearts, pos.y+SuperpositionHandler.getRandomNegative()*distHearts, pos.z+SuperpositionHandler.getRandomNegative()*distHearts, SuperpositionHandler.getRandomNegative() * 1.05, SuperpositionHandler.getRandomNegative() * 1.05, SuperpositionHandler.getRandomNegative() * 1.05);
				}

				for (int counter = 0; counter < 12; counter++) {
					player.world.addParticle(ParticleTypes.LARGE_SMOKE, true, pos.x, pos.y, pos.z, SuperpositionHandler.getRandomNegative() * dist, SuperpositionHandler.getRandomNegative() * dist, SuperpositionHandler.getRandomNegative() * dist);
				}

			}

		});
		ctx.get().setPacketHandled(true);
	}

}
