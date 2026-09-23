package com.mrbysco.forcedemoting.network.message;

import com.mrbysco.forcedemoting.ForcedEmoting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncEmoteDataPayload(int entityID, long accumulatedTime,
                                   long lastTime, boolean stopAnimation) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, SyncEmoteDataPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			SyncEmoteDataPayload::entityID,
			ByteBufCodecs.VAR_LONG,
			SyncEmoteDataPayload::accumulatedTime,
			ByteBufCodecs.VAR_LONG,
			SyncEmoteDataPayload::lastTime,
			ByteBufCodecs.BOOL,
			SyncEmoteDataPayload::stopAnimation,
			SyncEmoteDataPayload::new);
	public static final Type<SyncEmoteDataPayload> ID = new Type<>(ForcedEmoting.modLoc("sync_emote"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
