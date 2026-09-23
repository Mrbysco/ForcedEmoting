package com.mrbysco.forcedemoting.network.handler;

import com.mrbysco.forcedemoting.client.AnimationHandler;
import com.mrbysco.forcedemoting.emote.EmoteData;
import com.mrbysco.forcedemoting.emote.EmoteState;
import com.mrbysco.forcedemoting.network.message.SyncEmoteDataPayload;
import com.mrbysco.forcedemoting.registry.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
	private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

	public static ClientPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleData(final SyncEmoteDataPayload data, final IPayloadContext context) {
		context.enqueueWork(() -> {
					Minecraft mc = Minecraft.getInstance();
					if (mc.level == null)
						return;
					Entity entity = mc.level.getEntity(data.entityID());
					if (entity != null) {
						EmoteData emoteData = entity.getData(ModRegistry.EMOTING);
						if (emoteData != null) {
							EmoteState state = emoteData.state;
							state.setAccumulatedTime(data.accumulatedTime());
							state.setLastTime(data.lastTime());

							if (!data.stopAnimation())
								state.start(entity.tickCount);
							else {
								state.stop();

								//noinspection rawtypes
								if (mc.getEntityRenderDispatcher().getRenderer(entity) instanceof HumanoidMobRenderer renderer &&
										renderer.getModel() instanceof HumanoidModel<?> model) {
									AnimationHandler.resetModel(model);
								}
							}
						}
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("forced_emoting.networking.sync_emote.failed", e.getMessage()));
					return null;
				});
	}
}
