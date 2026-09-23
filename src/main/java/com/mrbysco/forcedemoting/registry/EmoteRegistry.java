package com.mrbysco.forcedemoting.registry;

import com.mrbysco.forcedemoting.ForcedEmoting;
import com.mrbysco.forcedemoting.emote.Emote;
import com.mrbysco.forcedemoting.emote.EmoteTrigger;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@SuppressWarnings("removal")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class EmoteRegistry {
	public static final ResourceKey<Registry<EmoteTrigger>> EMOTE_TRIGGER_REGISTRY_KEY = ResourceKey.createRegistryKey(
			ForcedEmoting.modLoc("emote_trigger"));
	public static final ResourceKey<Registry<Emote>> EMOTE_REGISTRY_KEY = ResourceKey.createRegistryKey(
			ForcedEmoting.modLoc("emote"));

	@SubscribeEvent
	public static void onNewRegistry(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(EMOTE_TRIGGER_REGISTRY_KEY,
				EmoteTrigger.DIRECT_CODEC, EmoteTrigger.DIRECT_CODEC);
		event.dataPackRegistry(EMOTE_REGISTRY_KEY,
				Emote.DIRECT_CODEC, Emote.DIRECT_CODEC);
	}
}
