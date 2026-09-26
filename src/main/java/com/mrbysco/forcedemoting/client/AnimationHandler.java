package com.mrbysco.forcedemoting.client;

import com.mrbysco.forcedemoting.emote.EmoteData;
import com.mrbysco.forcedemoting.registry.ModRegistry;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.entity.animation.json.AnimationHolder;
import net.neoforged.neoforge.client.entity.animation.json.AnimationLoader;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(Dist.CLIENT)
public class AnimationHandler {
	private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();
	private static final Map<ResourceLocation, AnimationHolder> animationHolderMap = new HashMap<>();

	/**
	 * Animates the given humanoid model based on the emote data of the living entity.
	 *
	 * @param livingEntity  The living entity to animate.
	 * @param humanoidModel The humanoid model to animate.
	 * @param ageInTicks    The age in ticks for the animation.
	 */
	public static void animateEntity(LivingEntity livingEntity, HumanoidModel<?> humanoidModel, float ageInTicks) {
		if (livingEntity.hasData(ModRegistry.EMOTING)) {
			EmoteData data = livingEntity.getData(ModRegistry.EMOTING);
			AnimationHolder animationHolder = animationHolderMap.computeIfAbsent(data.emoteId, AnimationLoader.INSTANCE::getAnimationHolder);
			AnimationDefinition definition = animationHolder.getOrNull();
			if (definition != null) {
				data.state.updateTime(ageInTicks, 1.0F);
				data.state.ifStarted((state) -> {
					//Reset pos
					resetModel(humanoidModel);

					KeyframeAnimationHandler.animate(humanoidModel, definition, state.getAccumulatedTime(), 1.0F, ANIMATION_VECTOR_CACHE);
				});
			}
		}
	}

	/**
	 * Register a reload listener to clear the animation holder map.
	 *
	 * @param event The event to register the reload listener with.
	 */
	@SubscribeEvent
	public static void onRegisterClientReload(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) manager -> {
			animationHolderMap.clear();
		});
	}

	/**
	 * Resets the pose of all parts of the given humanoid model.
	 *
	 * @param humanoidModel The humanoid model to reset.
	 */
	public static void resetModel(HumanoidModel<? extends LivingEntity> humanoidModel) {
		humanoidModel.head.resetPose();
		humanoidModel.hat.resetPose();
		humanoidModel.body.resetPose();
		humanoidModel.rightArm.resetPose();
		humanoidModel.leftArm.resetPose();
		humanoidModel.rightLeg.resetPose();
		humanoidModel.leftLeg.resetPose();
	}
}
