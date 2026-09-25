package com.mrbysco.forcedemoting.handler;

import com.mrbysco.forcedemoting.ForcedEmoting;
import com.mrbysco.forcedemoting.emote.EmoteData;
import com.mrbysco.forcedemoting.emote.EmoteTrigger;
import com.mrbysco.forcedemoting.network.message.SyncEmoteDataPayload;
import com.mrbysco.forcedemoting.registry.ModRegistry;
import com.mrbysco.forcedemoting.util.EmoteUtil;
import com.mrbysco.forcedemoting.util.TriggerUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber
public class EmoteHandler {
	private static final Map<ResourceLocation, TargetingConditions> CONDITIONS_MAP = new HashMap<>();
	private static final TargetingConditions BASE_CONDITIONS = TargetingConditions.forNonCombat()
			.ignoreLineOfSight();

	@SubscribeEvent
	public static void onChatMessage(ServerChatEvent event) {
		ServerPlayer player = event.getPlayer();
		Component message = event.getMessage();
		RegistryAccess registryAccess = player.getServer().registryAccess();

		Pair<ResourceLocation, EmoteTrigger> matchingPair = TriggerUtil.getMatchingTrigger(registryAccess, message.getString());
		if (matchingPair != null) {
			ResourceLocation triggerKey = matchingPair.getKey();
			EmoteTrigger trigger = matchingPair.getValue();
			Level level = player.level();
			TargetingConditions condition = CONDITIONS_MAP.computeIfAbsent(triggerKey, (key) ->
					BASE_CONDITIONS.range(trigger.range()).selector(entity -> entity.getType().is(trigger.entityTypes()))
			);
			List<LivingEntity> humanoidEntities = level.getNearbyEntities(LivingEntity.class, condition, player, player.getBoundingBox()
					.inflate(trigger.range()));
			humanoidEntities.forEach(livingEntity -> {
				if (!trigger.targetPlayers() && livingEntity.getType().is(ForcedEmoting.PLAYERS)) return;
				EmoteUtil.forceEmote(livingEntity, trigger.emoteId(), trigger.length(), trigger.immobilize());
			});
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
				EmoteUtil.updateSpeed(livingEntity, true);
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
