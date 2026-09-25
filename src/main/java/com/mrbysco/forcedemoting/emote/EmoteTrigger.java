package com.mrbysco.forcedemoting.emote;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public record EmoteTrigger(List<String> triggerWords, HolderSet<EntityType<?>> entityTypes, float length,
                           ResourceLocation emoteId, boolean immobilize, boolean targetPlayers, int range,
                           boolean caseSensitive) {

	public static final Codec<EmoteTrigger> DIRECT_CODEC = ExtraCodecs.catchDecoderException(RecordCodecBuilder.create(inst -> inst.group(
			Codec.list(Codec.STRING).fieldOf("triggerWords").forGetter(EmoteTrigger::triggerWords),
			RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("entityType").forGetter(EmoteTrigger::entityTypes),
			Codec.FLOAT.fieldOf("length").forGetter(EmoteTrigger::length),
			ResourceLocation.CODEC.fieldOf("emoteId").forGetter(EmoteTrigger::emoteId),
			Codec.BOOL.optionalFieldOf("immobilize", false).forGetter(EmoteTrigger::immobilize),
			Codec.BOOL.optionalFieldOf("targetPlayers", false).forGetter(EmoteTrigger::targetPlayers),
			Codec.INT.optionalFieldOf("range", 16).forGetter(EmoteTrigger::range),
			Codec.BOOL.optionalFieldOf("caseSensitive", false).forGetter(EmoteTrigger::caseSensitive)
	).apply(inst, EmoteTrigger::new)));

	public static final Codec<Optional<WithConditions<EmoteTrigger>>> CONDITIONAL_CODEC = ConditionalOps.createConditionalCodecWithConditions(DIRECT_CODEC);

	public boolean matches(String message) {
		for (String triggerWord : triggerWords) {
			if (caseSensitive) {
				if (message.contains(triggerWord)) {
					return true;
				}
			} else {
				if (message.toLowerCase(Locale.ROOT).contains(triggerWord.toLowerCase(Locale.ROOT))) {
					return true;
				}
			}
		}
		return false;
	}
}
