package com.mrbysco.forcedemoting;

import com.mojang.logging.LogUtils;
import com.mrbysco.forcedemoting.registry.ModRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ForcedEmoting.MOD_ID)
public class ForcedEmoting {
	public static final String MOD_ID = "forced_emoting";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final TagKey<EntityType<?>> ARMED = TagKey.create(Registries.ENTITY_TYPE, modLoc("armed"));
	public static final TagKey<EntityType<?>> PLAYERS = TagKey.create(Registries.ENTITY_TYPE, modLoc("players"));

	public ForcedEmoting(IEventBus eventBus) {
		ModRegistry.ATTACHMENT_TYPES.register(eventBus);
	}

	public static ResourceLocation modLoc(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
