package com.mrbysco.forcedemoting.registry;

import com.mrbysco.forcedemoting.ForcedEmoting;
import com.mrbysco.forcedemoting.emote.EmoteData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModRegistry {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ForcedEmoting.MOD_ID);

	public static final Supplier<AttachmentType<EmoteData>> EMOTING = ATTACHMENT_TYPES.register(
			"emoting", () -> AttachmentType.serializable(() -> new EmoteData(EmoteBootstrap.WAVE.location(), 1.0F, 0)).sync(
					EmoteData.STREAM_CODEC
			).build());
}
