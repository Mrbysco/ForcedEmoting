package com.mrbysco.forcedemoting.emote;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class EmoteData implements INBTSerializable<CompoundTag> {
	public static final StreamCodec<FriendlyByteBuf, EmoteData> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC,
			EmoteData::emoteId,
			ByteBufCodecs.FLOAT,
			EmoteData::length,
			ByteBufCodecs.VAR_LONG,
			EmoteData::startAge,
			EmoteData::new
	);

	public ResourceLocation emoteId;
	public float length;
	public EmoteState state = new EmoteState();
	public long startAge;

	public ResourceLocation emoteId() {
		return emoteId;
	}

	public float length() {
		return length;
	}

	public long startAge() {
		return startAge;
	}

	public long getAccumulatedTime() {
		return this.state.getAccumulatedTime();
	}

	public long getLastTime() {
		return this.state.getLastTime();
	}

	public EmoteData(ResourceLocation emoteId, float length, long startAge) {
		this.emoteId = emoteId;
		this.length = length;
		this.startAge = startAge;
	}

	public boolean isDone(long currentTime) {
		return currentTime - this.startAge >= (this.length * 20);
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		tag.putString("EmoteId", emoteId.toString());
		tag.putFloat("Length", length);
		tag.put("State", state.serializeNBT(provider));
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		this.emoteId = ResourceLocation.tryParse(tag.getString("EmoteId"));
		this.length = tag.getFloat("Length");
		this.state.deserializeNBT(provider, tag.getCompound("State"));
	}
}
