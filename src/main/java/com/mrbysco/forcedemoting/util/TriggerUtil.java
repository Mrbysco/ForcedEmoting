package com.mrbysco.forcedemoting.util;

import com.mrbysco.forcedemoting.emote.EmoteTrigger;
import com.mrbysco.forcedemoting.registry.EmoteRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Nullable;

public class TriggerUtil {
	/**
	 * Gets the first EmoteTrigger that matches the given message.
	 *
	 * @param registryAccess The registry access to use for looking up triggers.
	 * @param message        The message to match against triggers.
	 * @return The first matching EmoteTrigger, or null if none match.
	 */
	@Nullable
	public static EmoteTrigger getMatchingTrigger(RegistryAccess registryAccess, String message) {
		Registry<EmoteTrigger> triggerLookup = registryAccess.registryOrThrow(EmoteRegistry.EMOTE_TRIGGER_REGISTRY_KEY);
		for (EmoteTrigger emoteTrigger : triggerLookup) {
			if (emoteTrigger.matches(message)) {
				return emoteTrigger;
			}
		}
		return null;
	}
}
