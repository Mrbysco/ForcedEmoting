package com.mrbysco.forcedemoting.client;

import com.mrbysco.forcedemoting.mixin.client.AgeableListModelAccessor;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class KeyframeAnimationHandler {
	public static void animate(EntityModel<?> entityModel, AnimationDefinition animationDefinition, long accumulatedTime, float scale, Vector3f animationVecCache) {
		if (entityModel instanceof HierarchicalModel<?> hierarchicalModel) {
			KeyframeAnimations.animate(hierarchicalModel, animationDefinition, accumulatedTime, scale, animationVecCache);
		} else if (entityModel instanceof HumanoidModel<?> humanoidModel) {
			animateHumanoid(humanoidModel, animationDefinition, accumulatedTime, scale, animationVecCache);
		} else if (entityModel instanceof AgeableListModel<?> listModel) {
			animateList(listModel, animationDefinition, accumulatedTime, scale, animationVecCache);
		}
	}

	public static void animateHumanoid(HumanoidModel<?> humanoidModel, AnimationDefinition animationDefinition, long accumulatedTime, float scale, Vector3f animationVecCache) {
		float f = getElapsedSeconds(animationDefinition, accumulatedTime);
		for (Map.Entry<String, List<AnimationChannel>> entry : animationDefinition.boneAnimations().entrySet()) {
			Optional<List<ModelPart>> optionalParts = getParts(humanoidModel, entry.getKey());

			List<AnimationChannel> list = entry.getValue();
			optionalParts.ifPresent((parts) -> {
				parts.forEach(part -> {
					part.resetPose();
					list.forEach((animationChannel) -> {
						Keyframe[] akeyframe = animationChannel.keyframes();
						int i = Math.max(0, Mth.binarySearch(0, akeyframe.length, p_232315_ -> f <= akeyframe[p_232315_].timestamp()) - 1);
						int j = Math.min(akeyframe.length - 1, i + 1);
						Keyframe keyframe = akeyframe[i];
						Keyframe keyframe1 = akeyframe[j];
						float f1 = f - keyframe.timestamp();
						float f2;
						if (j != i) {
							f2 = Mth.clamp(f1 / (keyframe1.timestamp() - keyframe.timestamp()), 0.0F, 1.0F);
						} else {
							f2 = 0.0F;
						}

						keyframe1.interpolation().apply(animationVecCache, f2, akeyframe, i, j, scale);
						animationChannel.target().apply(part, animationVecCache);
					});
				});
			});
		}
	}

	private static Optional<List<ModelPart>> getParts(HumanoidModel<?> humanoidModel, String key) {
		if (humanoidModel instanceof PlayerModel<?> playerModel) {
			return switch (key) {
				case "head" -> Optional.of(List.of(playerModel.head));
				case "hat" -> Optional.of(List.of(playerModel.hat));
				case "body" -> Optional.of(List.of(playerModel.body, playerModel.jacket));
				case "right_arm" -> Optional.of(List.of(playerModel.rightArm, playerModel.rightSleeve));
				case "left_arm" -> Optional.of(List.of(playerModel.leftArm, playerModel.leftSleeve));
				case "right_leg" -> Optional.of(List.of(playerModel.rightLeg, playerModel.rightPants));
				case "left_leg" -> Optional.of(List.of(playerModel.leftLeg, playerModel.leftPants));

				default -> Optional.empty();
			};
		} else {
			return switch (key) {
				case "head" -> Optional.of(List.of(humanoidModel.head));
				case "hat" -> Optional.of(List.of(humanoidModel.hat));
				case "body" -> Optional.of(List.of(humanoidModel.body));
				case "right_arm" -> Optional.of(List.of(humanoidModel.rightArm));
				case "left_arm" -> Optional.of(List.of(humanoidModel.leftArm));
				case "right_leg" -> Optional.of(List.of(humanoidModel.rightLeg));
				case "left_leg" -> Optional.of(List.of(humanoidModel.leftLeg));

				default -> Optional.empty();
			};
		}
	}

	public static void animateList(AgeableListModel<?> listModel, AnimationDefinition animationDefinition, long accumulatedTime, float scale, Vector3f animationVecCache) {
		float f = getElapsedSeconds(animationDefinition, accumulatedTime);

		for (Map.Entry<String, List<AnimationChannel>> entry : animationDefinition.boneAnimations().entrySet()) {
			Optional<ModelPart> optional = getAnyDescendantWithName(listModel, entry.getKey());
			List<AnimationChannel> list = entry.getValue();
			optional.ifPresent(part -> list.forEach(animationChannel -> {
				Keyframe[] akeyframe = animationChannel.keyframes();
				int i = Math.max(0, Mth.binarySearch(0, akeyframe.length, p_232315_ -> f <= akeyframe[p_232315_].timestamp()) - 1);
				int j = Math.min(akeyframe.length - 1, i + 1);
				Keyframe keyframe = akeyframe[i];
				Keyframe keyframe1 = akeyframe[j];
				float f1 = f - keyframe.timestamp();
				float f2;
				if (j != i) {
					f2 = Mth.clamp(f1 / (keyframe1.timestamp() - keyframe.timestamp()), 0.0F, 1.0F);
				} else {
					f2 = 0.0F;
				}

				keyframe1.interpolation().apply(animationVecCache, f2, akeyframe, i, j, scale);
				animationChannel.target().apply(part, animationVecCache);
			}));
		}
	}

	public static Optional<ModelPart> getAnyDescendantWithName(AgeableListModel<?> listModel, String name) {
		AgeableListModelAccessor accessor = ((AgeableListModelAccessor) listModel);
		Iterable<ModelPart> headParts = accessor.forcedemoting$headParts();
		Iterable<ModelPart> bodyParts = accessor.forcedemoting$bodyParts();
		Stream<ModelPart> partStream = Stream.concat(
				StreamSupport.stream(headParts.spliterator(), false),
				StreamSupport.stream(bodyParts.spliterator(), false)
		);
		return partStream
				.flatMap(ModelPart::getAllParts)
				.filter(child -> child.hasChild(name))
				.findFirst()
				.map(child -> child.getChild(name));
	}

	private static float getElapsedSeconds(AnimationDefinition animationDefinition, long accumulatedTime) {
		float f = (float) accumulatedTime / 1000.0F;
		return animationDefinition.looping() ? f % animationDefinition.lengthInSeconds() : f;
	}
}
