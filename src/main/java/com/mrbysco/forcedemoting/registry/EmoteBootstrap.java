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

	private static ResourceKey<Emote> emoteKey(String name) {
		return ResourceKey.create(EmoteRegistry.EMOTE_REGISTRY_KEY, ForcedEmoting.modLoc(name));
	}

	public static void emoteBootstrap(BootstrapContext<Emote> context) {
		context.register(WAVE, new Emote(ForcedEmoting.modLoc("wave")));
		context.register(CINEMA, new Emote(ForcedEmoting.modLoc("cinema")));
	}

	public static final ResourceKey<EmoteTrigger> WAVE_TRIGGER = emoteTriggerKey("wave");
	public static final ResourceKey<EmoteTrigger> CINEMA_TRIGGER = emoteTriggerKey("cinema");

	public static void emoteTriggerBootstrap(BootstrapContext<EmoteTrigger> context) {
		HolderGetter<EntityType<?>> entities = context.lookup(Registries.ENTITY_TYPE);
		HolderSet.Named<EntityType<?>> humanoid = entities.getOrThrow(ForcedEmoting.HUMANOID);

		List<String> greetings = List.of("hi", "hello");
		context.register(WAVE_TRIGGER, new EmoteTrigger(greetings, humanoid, 2.0F, WAVE.location(), false, false));

		context.register(CINEMA_TRIGGER, new EmoteTrigger(List.of("cinema"), humanoid, 3.0F, CINEMA.location(), true, false));
	}

	private static ResourceKey<EmoteTrigger> emoteTriggerKey(String name) {
		return ResourceKey.create(EmoteRegistry.EMOTE_TRIGGER_REGISTRY_KEY, ForcedEmoting.modLoc(name));
	}
}
