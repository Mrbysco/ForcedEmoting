package com.mrbysco.forcedemoting.emote;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.Optional;

public record Emote(ResourceLocation animation) {
	public static final Codec<Emote> DIRECT_CODEC = ExtraCodecs.catchDecoderException(RecordCodecBuilder.create(inst -> inst.group(
			ResourceLocation.CODEC.fieldOf("aniamtion").forGetter(Emote::animation)
	).apply(inst, Emote::new)));

	public static final Codec<Optional<WithConditions<Emote>>> CONDITIONAL_CODEC = ConditionalOps.createConditionalCodecWithConditions(DIRECT_CODEC);

}
