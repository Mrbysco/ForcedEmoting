package com.mrbysco.forcedemoting.datagen;

import com.mrbysco.forcedemoting.datagen.data.EmoteEntityTypeTagsProvider;
import com.mrbysco.forcedemoting.registry.EmoteBootstrap;
import com.mrbysco.forcedemoting.registry.EmoteRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class ForcedDatagen {
	public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(EmoteRegistry.EMOTE_REGISTRY_KEY, EmoteBootstrap::emoteBootstrap)
			.add(EmoteRegistry.EMOTE_TRIGGER_REGISTRY_KEY, EmoteBootstrap::emoteTriggerBootstrap);

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		event.createDatapackRegistryObjects(BUILDER);

		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		ExistingFileHelper helper = event.getExistingFileHelper();

		generator.addProvider(true, new EmoteEntityTypeTagsProvider(packOutput, lookupProvider, helper));
	}
}
