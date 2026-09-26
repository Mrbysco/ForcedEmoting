package com.mrbysco.forcedemoting.registry;

import com.mrbysco.forcedemoting.ForcedEmoting;
import com.mrbysco.forcedemoting.emote.Emote;
import com.mrbysco.forcedemoting.emote.EmoteTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class EmoteBootstrap {
	public static final ResourceKey<Emote> WAVE = emoteKey("wave");
	public static final ResourceKey<Emote> CINEMA = emoteKey("cinema");
	public static final ResourceKey<Emote> SIX_SEVEN = emoteKey("six_seven");
	public static final ResourceKey<Emote> GANGNAM = emoteKey("gangnam");

	private static ResourceKey<Emote> emoteKey(String name) {
		return ResourceKey.create(EmoteRegistry.EMOTE_REGISTRY_KEY, ForcedEmoting.modLoc(name));
	}

	public static void emoteBootstrap(BootstrapContext<Emote> context) {
		context.register(WAVE, new Emote(ForcedEmoting.modLoc("wave")));
		context.register(CINEMA, new Emote(ForcedEmoting.modLoc("cinema")));
		context.register(SIX_SEVEN, new Emote(ForcedEmoting.modLoc("six_seven")));
		context.register(GANGNAM, new Emote(ForcedEmoting.modLoc("gangnam")));
	}

	public static final ResourceKey<EmoteTrigger> WAVE_TRIGGER = emoteTriggerKey("wave");
	public static final ResourceKey<EmoteTrigger> CINEMA_TRIGGER = emoteTriggerKey("cinema");
	public static final ResourceKey<EmoteTrigger> SIX_SEVEN_TRIGGER = emoteTriggerKey("six_seven");
	public static final ResourceKey<EmoteTrigger> GANGNAM_TRIGGER = emoteTriggerKey("gangnam");

	public static void emoteTriggerBootstrap(BootstrapContext<EmoteTrigger> context) {
		HolderGetter<EntityType<?>> entities = context.lookup(Registries.ENTITY_TYPE);
		HolderSet.Named<EntityType<?>> humanoid = entities.getOrThrow(ForcedEmoting.ARMED);

		List<String> greetings = List.of("hi", "hello");
		context.register(WAVE_TRIGGER, new EmoteTrigger(greetings, humanoid, 2.0F, WAVE.location(), false, false, 16, false));

		context.register(CINEMA_TRIGGER, new EmoteTrigger(List.of("cinema"), humanoid, 3.0F, CINEMA.location(), true, false, 16, false));

		context.register(SIX_SEVEN_TRIGGER, new EmoteTrigger(List.of("67", "6 7", "six seven"), humanoid, 3.0F, SIX_SEVEN.location(), false, true, 16, false));

		context.register(GANGNAM_TRIGGER, new EmoteTrigger(List.of("gangnam style"), humanoid, 4.0F, GANGNAM.location(), false, false, 16, false));
	}

	private static ResourceKey<EmoteTrigger> emoteTriggerKey(String name) {
		return ResourceKey.create(EmoteRegistry.EMOTE_TRIGGER_REGISTRY_KEY, ForcedEmoting.modLoc(name));
	}
}
