package com.mrbysco.forcedemoting.datagen.data;

import com.mrbysco.forcedemoting.ForcedEmoting;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class EmoteEntityTypeTagsProvider extends EntityTypeTagsProvider {
	public EmoteEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
		super(output, provider, ForcedEmoting.MOD_ID, helper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(ForcedEmoting.ARMED).add(
				EntityType.ENDERMAN,
				EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED,
				EntityType.SKELETON, EntityType.BOGGED, EntityType.STRAY,
				EntityType.ZOMBIFIED_PIGLIN, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE,
				EntityType.EVOKER, EntityType.ILLUSIONER, EntityType.VINDICATOR, EntityType.PILLAGER,
				EntityType.WARDEN
		);
		this.tag(ForcedEmoting.PLAYERS).add(
				EntityType.PLAYER
		);
	}
}
