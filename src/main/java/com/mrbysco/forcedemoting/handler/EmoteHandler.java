package com.mrbysco.forcedemoting.handler;

import com.mrbysco.forcedemoting.ForcedEmoting;
import com.mrbysco.forcedemoting.emote.EmoteData;
import com.mrbysco.forcedemoting.emote.EmoteTrigger;
import com.mrbysco.forcedemoting.network.message.SyncEmoteDataPayload;
import com.mrbysco.forcedemoting.registry.ModRegistry;
import com.mrbysco.forcedemoting.util.TriggerUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber
public class EmoteHandler {
	private static final int RANGE = 16;
	private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forNonCombat()
			.ignoreLineOfSight().range(RANGE).selector(entity -> entity.getType().is(ForcedEmoting.HUMANOID) || entity.getType().is(ForcedEmoting.PLAYERS));
	private static final ResourceLocation SPEED_MODIFIER = ForcedEmoting.modLoc("freeze");

	@SubscribeEvent
	public static void onChatMessage(ServerChatEvent event) {
		ServerPlayer player = event.getPlayer();
		Component message = event.getMessage();

		EmoteTrigger matching = TriggerUtil.getMatchingTrigger(player.getServer().registryAccess(), message.getString());
		if (matching != null) {
			Level level = player.level();
			List<LivingEntity> humanoidEntities = level.getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, player, player.getBoundingBox().inflate(RANGE));
			humanoidEntities.forEach(livingEntity -> {
				if (!matching.targetPlayers() && livingEntity.getType().is(ForcedEmoting.PLAYERS)) return;

				EmoteData emoteData = new EmoteData(matching.emoteId(), matching.length(), livingEntity.tickCount);
				livingEntity.setData(ModRegistry.EMOTING, emoteData);

				livingEntity.move(MoverType.SELF, Vec3.ZERO.add(0, -1, 0));
				if (livingEntity instanceof Mob mob) {
					mob.setTarget(null);
				}
				if (matching.immobilize())
					updateSpeed(livingEntity, false);

				PacketDistributor.sendToAllPlayers(new SyncEmoteDataPayload(livingEntity.getId(),
						emoteData.getAccumulatedTime(), emoteData.getLastTime(), false));
			});
		}
	}

	private static void updateSpeed(LivingEntity livingEntity, boolean reset) {
		AttributeInstance speedAttribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
		if (speedAttribute == null) return;
		if (reset) {
			speedAttribute.removeModifier(SPEED_MODIFIER);
		} else {
			speedAttribute.addTransientModifier(new AttributeModifier(SPEED_MODIFIER, -10, AttributeModifier.Operation.ADD_VALUE));
		}
	}

	@SubscribeEvent
	public static void onTarget(LivingChangeTargetEvent event) {
		if (event.getTargetType() == LivingChangeTargetEvent.LivingTargetType.MOB_TARGET) {
			LivingEntity livingEntity = event.getEntity();
			if (livingEntity.hasData(ModRegistry.EMOTING)) {
				event.setNewAboutToBeSetTarget(null);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingUpdate(EntityTickEvent.Pre event) {
		Entity entity = event.getEntity();
		Level level = entity.level();
		if (!level.isClientSide && entity instanceof LivingEntity livingEntity && livingEntity.hasData(ModRegistry.EMOTING)) {
			EmoteData data = livingEntity.getData(ModRegistry.EMOTING);
			if (data.isDone(livingEntity.tickCount)) {
				PacketDistributor.sendToAllPlayers(new SyncEmoteDataPayload(livingEntity.getId(),
						data.getAccumulatedTime(), data.getLastTime(), true));
				livingEntity.removeData(ModRegistry.EMOTING);
				updateSpeed(livingEntity, true);
			} else {
				livingEntity.move(MoverType.SELF, Vec3.ZERO.add(0, -1, 0));
				if (entity.tickCount % 20 == 0) {
					PacketDistributor.sendToAllPlayers(new SyncEmoteDataPayload(livingEntity.getId(),
							data.getAccumulatedTime(), data.getLastTime(), false));
				}
			}
		}
	}

}
