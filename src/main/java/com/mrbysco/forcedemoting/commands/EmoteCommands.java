package com.mrbysco.forcedemoting.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrbysco.forcedemoting.emote.Emote;
import com.mrbysco.forcedemoting.registry.EmoteRegistry;
import com.mrbysco.forcedemoting.util.EmoteUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

@EventBusSubscriber
public class EmoteCommands {

	@SubscribeEvent
	public static void onCommandRegister(RegisterCommandsEvent event) {
		EmoteCommands.initializeCommands(event.getDispatcher(), event.getBuildContext());
	}

	public static void initializeCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
		final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("forcedemoting");
		HolderLookup.RegistryLookup<Emote> emoteRegistry = buildContext.lookupOrThrow(EmoteRegistry.EMOTE_REGISTRY_KEY);
		List<ResourceLocation> emoteIds = emoteRegistry.listElements().map(key -> key.key().location()).toList();

		root.requires(cs -> cs.hasPermission(2))
				.then(Commands.literal("force_emote")
						.then(Commands.argument("entity", EntityArgument.entity())
								.then(Commands.argument("emote", ResourceLocationArgument.id())
										.suggests((cs, builder) ->
												SharedSuggestionProvider.suggestResource(emoteIds.stream(), builder))
										.then(Commands.argument("seconds", FloatArgumentType.floatArg(0, 60))
												.then(Commands.argument("immobilize", BoolArgumentType.bool())
														.executes(EmoteCommands::forceEmote)
												)
										)
								)
						)
				);

		dispatcher.register(root);
	}

	private static int forceEmote(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		final Entity entity = EntityArgument.getEntity(ctx, "entity");
		if (entity instanceof LivingEntity livingEntity) {
			ResourceLocation emoteId = ResourceLocationArgument.getId(ctx, "emote");
			float length = FloatArgumentType.getFloat(ctx, "seconds");
			boolean immobilize = BoolArgumentType.getBool(ctx, "immobilize");
			EmoteUtil.forceEmote(livingEntity, emoteId, length, immobilize);
		} else {
			ctx.getSource().sendFailure(
					Component.translatable("commands.forcedemoting.force.invalid_entity").withStyle(ChatFormatting.RED)
			);
		}
		return 0;
	}
}
