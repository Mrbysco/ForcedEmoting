package com.mrbysco.forcedemoting.network;

import com.mrbysco.forcedemoting.ForcedEmoting;
import com.mrbysco.forcedemoting.network.handler.ClientPayloadHandler;
import com.mrbysco.forcedemoting.network.message.SyncEmoteDataPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber()
public class EmoteNetworking {
	@SubscribeEvent
	public static void setupPackets(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(ForcedEmoting.MOD_ID);

		registrar.playToClient(SyncEmoteDataPayload.ID, SyncEmoteDataPayload.CODEC, ClientPayloadHandler.getInstance()::handleData);
	}
}
