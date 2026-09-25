package com.mrbysco.forcedemoting.client;

import com.mrbysco.forcedemoting.emote.EmoteData;
import com.mrbysco.forcedemoting.registry.ModRegistry;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.entity.animation.json.AnimationHolder;
import net.neoforged.neoforge.client.entity.animation.json.AnimationLoader;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class AnimationHandler {
	private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();
	private static final Map<ResourceLocation, AnimationHolder> animationHolderMap = new HashMap<>();

	public static void animateHumanoid(LivingEntity livingEntity, HumanoidModel<?> humanoidModel, float ageInTicks) {
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
