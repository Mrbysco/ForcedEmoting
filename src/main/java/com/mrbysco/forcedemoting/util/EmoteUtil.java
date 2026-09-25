package com.mrbysco.forcedemoting.util;

import com.mrbysco.forcedemoting.ForcedEmoting;
import com.mrbysco.forcedemoting.emote.EmoteData;
import com.mrbysco.forcedemoting.network.message.SyncEmoteDataPayload;
import com.mrbysco.forcedemoting.registry.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class EmoteUtil {
	private static final ResourceLocation SPEED_MODIFIER = ForcedEmoting.modLoc("freeze");

	/**
	 * Updates the movement speed of the given living entity.
	 *
	 * @param livingEntity The living entity whose speed is to be updated.
	 * @param reset        If true, the speed modifier is removed; otherwise, it is applied.
	 */
	public static void updateSpeed(LivingEntity livingEntity, boolean reset) {
		AttributeInstance speedAttribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
		if (speedAttribute == null) return;
		if (reset) {
			speedAttribute.removeModifier(SPEED_MODIFIER);
		} else {
			speedAttribute.addTransientModifier(new AttributeModifier(SPEED_MODIFIER, -10, AttributeModifier.Operation.ADD_VALUE));
		}
	}

	/**
	 * Forces the given living entity to perform the specified emote.
	 *
	 * @param livingEntity The living entity to perform the emote.
	 * @param emoteId      The ID of the emote to be performed.
	 * @param length       The duration of the emote.
	 * @param immobilize   If true, the living entity's movement is immobilized during the emote.
	 */
	public static void forceEmote(LivingEntity livingEntity, ResourceLocation emoteId, float length, boolean immobilize) {
		EmoteData emoteData = new EmoteData(emoteId, length, livingEntity.tickCount);
		livingEntity.setData(ModRegistry.EMOTING, emoteData);

		livingEntity.move(MoverType.SELF, Vec3.ZERO.add(0, -1, 0));
		if (livingEntity instanceof Mob mob) {
			mob.setTarget(null);
		}
		if (immobilize)
			updateSpeed(livingEntity, false);

		PacketDistributor.sendToAllPlayers(new SyncEmoteDataPayload(livingEntity.getId(),
				emoteData.getAccumulatedTime(), emoteData.getLastTime(), false));
	}

}
