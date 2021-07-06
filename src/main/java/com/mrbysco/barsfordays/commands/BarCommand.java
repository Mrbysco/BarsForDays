package com.mrbysco.barsfordays.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mrbysco.barsfordays.Reference;
import com.mrbysco.barsfordays.storage.CustomBarManager;
import com.mrbysco.barsfordays.storage.bar.BarInfo;
import com.mrbysco.barsfordays.storage.bar.CustomServerBarInfo;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Collections;

public class BarCommand {
	private static final DynamicCommandExceptionType ERROR_ALREADY_EXISTS = new DynamicCommandExceptionType((info) -> new TranslationTextComponent("commands.barsfordays.create.failed", info));
	private static final DynamicCommandExceptionType ERROR_DOESNT_EXIST = new DynamicCommandExceptionType((info) -> new TranslationTextComponent("commands.barsfordays.unknown", info));
	private static final SimpleCommandExceptionType ERROR_NO_PLAYER_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.barsfordays.set.players.unchanged"));
	private static final SimpleCommandExceptionType ERROR_NO_NAME_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.barsfordays.set.name.unchanged"));
	private static final SimpleCommandExceptionType ERROR_NO_COLOR_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.barsfordays.set.color.unchanged"));
	private static final SimpleCommandExceptionType ERROR_NO_STYLE_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.barsfordays.set.style.unchanged"));
	private static final SimpleCommandExceptionType ERROR_NO_VALUE_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.barsfordays.set.value.unchanged"));
	private static final SimpleCommandExceptionType ERROR_NO_MAX_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.barsfordays.set.max.unchanged"));
	private static final SimpleCommandExceptionType ERROR_ALREADY_HIDDEN = new SimpleCommandExceptionType(new TranslationTextComponent("commands.barsfordays.set.visibility.unchanged.hidden"));
	private static final SimpleCommandExceptionType ERROR_ALREADY_VISIBLE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.barsfordays.set.visibility.unchanged.visible"));
	public static final SuggestionProvider<CommandSource> SUGGEST_BOSS_BAR = (commandContext, suggestionsBuilder) -> ISuggestionProvider.suggestResource(CustomBarManager.get(commandContext.getSource().getServer().getLevel(World.OVERWORLD)).getIds(), suggestionsBuilder);

	public static void initializeCommands(CommandDispatcher<CommandSource> commandDispatcher) {
		commandDispatcher.register(Commands.literal(Reference.MOD_ID).requires((commandContext) -> commandContext.hasPermission(2))
		.then(Commands.literal("add")
		.then(Commands.argument("id", ResourceLocationArgument.id())
		.then(Commands.argument("name", ComponentArgument.textComponent()).executes((commandContext) ->
			createBar(commandContext.getSource(), ResourceLocationArgument.getId(commandContext, "id"), ComponentArgument.getComponent(commandContext, "name"))))))
		.then(Commands.literal("remove").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).executes((commandContext) ->
			removeBar(commandContext.getSource(), getBossBar(commandContext, getBossBarManager(commandContext.getSource()))))))
				.then(Commands.literal("list").executes((commandContext) ->
					listBars(commandContext.getSource())))
		.then(Commands.literal("set")
			.then(Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR)
				.then(Commands.literal("name")
					.then(Commands.argument("name", ComponentArgument.textComponent()).executes((commandContext) ->
						setName(commandContext, ComponentArgument.getComponent(commandContext, "name")))))
				.then(Commands.literal("color")
					.then(Commands.literal("pink").executes((commandContext) ->
						setColor(commandContext, BarInfo.Color.PINK)))
					.then(Commands.literal("blue").executes((commandContext) ->
						setColor(commandContext, BarInfo.Color.BLUE)))
					.then(Commands.literal("red").executes((commandContext) ->
						setColor(commandContext, BarInfo.Color.RED)))
					.then(Commands.literal("green").executes((commandContext) ->
						setColor(commandContext, BarInfo.Color.GREEN)))
					.then(Commands.literal("yellow").executes((commandContext) ->
						setColor(commandContext, BarInfo.Color.YELLOW)))
					.then(Commands.literal("purple").executes((commandContext) ->
						setColor(commandContext, BarInfo.Color.PURPLE)))
					.then(Commands.literal("white").executes((commandContext) ->
						setColor(commandContext, BarInfo.Color.WHITE))))
				.then(Commands.literal("style")
					.then(Commands.literal("progress").executes((commandContext) ->
						setStyle(commandContext, BarInfo.Overlay.PROGRESS)))
							.then(Commands.literal("notched_6").executes((commandContext) ->
								setStyle(commandContext, BarInfo.Overlay.NOTCHED_6)))
									.then(Commands.literal("notched_10").executes((commandContext) ->
											setStyle(commandContext, BarInfo.Overlay.NOTCHED_10)))
									.then(Commands.literal("notched_12").executes((commandContext) ->
											setStyle(commandContext, BarInfo.Overlay.NOTCHED_12)))
									.then(Commands.literal("notched_20").executes((commandContext) ->
											setStyle(commandContext, BarInfo.Overlay.NOTCHED_20))))
				.then(Commands.literal("value")
					.then(Commands.argument("value", IntegerArgumentType.integer(0)).executes((commandContext) ->
							setValue(commandContext, IntegerArgumentType.getInteger(commandContext, "value")))))
				.then(Commands.literal("scale")
						.then(Commands.argument("scale", DoubleArgumentType.doubleArg(0.1, 10.0)).executes((commandContext) ->
								setScale(commandContext, DoubleArgumentType.getDouble(commandContext, "scale")))))
				.then(Commands.literal("pos_x")
						.then(Commands.argument("pos_x", IntegerArgumentType.integer(0)).executes((commandContext) ->
						setXPos(commandContext, IntegerArgumentType.getInteger(commandContext, "pos_x")))))
				.then(Commands.literal("pos_y")
						.then(Commands.argument("pos_y", IntegerArgumentType.integer(0)).executes((commandContext) ->
								setYPos(commandContext, IntegerArgumentType.getInteger(commandContext, "pos_y")))))
				.then(Commands.literal("max")
					.then(Commands.argument("max", IntegerArgumentType.integer(1)).executes((commandContext) ->
						setMax(commandContext, IntegerArgumentType.getInteger(commandContext, "max")))))
				.then(Commands.literal("visible")
						.then(Commands.argument("visible", BoolArgumentType.bool()).executes((commandContext) ->
						setVisible(commandContext, BoolArgumentType.getBool(commandContext, "visible")))))
				.then(Commands.literal("center_x").then(Commands.argument("center_x", BoolArgumentType.bool()).executes((commandContext) ->
						setCenterX(commandContext, BoolArgumentType.getBool(commandContext, "center_x")))))
				.then(Commands.literal("center_y")
						.then(Commands.argument("center_y", BoolArgumentType.bool()).executes((commandContext) ->
								setCenterY(commandContext, BoolArgumentType.getBool(commandContext, "center_y")))))
				.then(Commands.literal("invert_x")
						.then(Commands.argument("invert_x", BoolArgumentType.bool()).executes((commandContext) ->
								setXInverted(commandContext, BoolArgumentType.getBool(commandContext, "invert_x")))))
				.then(Commands.literal("invert_y")
						.then(Commands.argument("invert_y", BoolArgumentType.bool()).executes((commandContext) ->
								setYInverted(commandContext, BoolArgumentType.getBool(commandContext, "invert_y")))))
				.then(Commands.literal("players").executes((commandContext) ->
						setPlayers(commandContext, Collections.emptyList()))
						.then(Commands.argument("targets", EntityArgument.players()).executes((commandContext) ->
								setPlayers(commandContext, EntityArgument.getOptionalPlayers(commandContext, "targets")))))))
		.then(Commands.literal("get")
				.then(Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR)
						.then(Commands.literal("value").executes((commandContext) ->
								getValue(commandContext.getSource(), getBossBar(commandContext, getBossBarManager(commandContext.getSource())))))
						.then(Commands.literal("max").executes((commandContext) ->
								getMax(commandContext.getSource(), getBossBar(commandContext, getBossBarManager(commandContext.getSource())))))
		.then(Commands.literal("visible").executes((commandContext) ->
				getVisible(commandContext.getSource(), getBossBar(commandContext, getBossBarManager(commandContext.getSource())))))
		.then(Commands.literal("players").executes((commandContext) ->
				getPlayers(commandContext.getSource(), getBossBar(commandContext, getBossBarManager(commandContext.getSource()))))))));
	}

	private static int getValue(CommandSource source, CustomServerBarInfo info) {
		source.sendSuccess(new TranslationTextComponent("commands.barsfordays.get.value", info.getDisplayName(), info.getValue()), true);
		return info.getValue();
	}

	private static int getMax(CommandSource source, CustomServerBarInfo info) {
		source.sendSuccess(new TranslationTextComponent("commands.barsfordays.get.max", info.getDisplayName(), info.getMax()), true);
		return info.getMax();
	}

	private static int getVisible(CommandSource source, CustomServerBarInfo info) {
		if (info.isVisible()) {
			source.sendSuccess(new TranslationTextComponent("commands.barsfordays.get.visible.visible", info.getDisplayName()), true);
			return 1;
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.barsfordays.get.visible.hidden", info.getDisplayName()), true);
			return 0;
		}
	}

	private static int getPlayers(CommandSource source, CustomServerBarInfo info) {
		if (info.getPlayers().isEmpty()) {
			source.sendSuccess(new TranslationTextComponent("commands.barsfordays.get.players.none", info.getDisplayName()), true);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.barsfordays.get.players.some", info.getDisplayName(), info.getPlayers().size(), TextComponentUtils.formatList(info.getPlayers(), PlayerEntity::getDisplayName)), true);
		}

		return info.getPlayers().size();
	}

	private static int setVisible(CommandContext<CommandSource> commandContext, boolean visible) throws CommandSyntaxException {
		CustomBarManager barManager = getBossBarManager(commandContext.getSource());
		CustomServerBarInfo info = getBossBar(commandContext, barManager);
		if (info.isVisible() == visible) {
			if (visible) {
				throw ERROR_ALREADY_VISIBLE.create();
			} else {
				throw ERROR_ALREADY_HIDDEN.create();
			}
		} else {
			info.setVisible(visible);
			barManager.setDirty(true);
			if (visible) {
				commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.visible.success.visible", info.getDisplayName()), true);
			} else {
				commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.visible.success.hidden", info.getDisplayName()), true);
			}

			return 0;
		}
	}

	private static int setCenterX(CommandContext<CommandSource> commandContext, boolean center) throws CommandSyntaxException {
		CustomBarManager barManager = getBossBarManager(commandContext.getSource());
		CustomServerBarInfo info = getBossBar(commandContext, barManager);
		if (info.isCenterX() == center) {
			if (center) {
				throw ERROR_ALREADY_VISIBLE.create();
			} else {
				throw ERROR_ALREADY_HIDDEN.create();
			}
		} else {
			info.setCenterX(center);
			barManager.setDirty(true);
			if (center) {
				commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.center_x.success.center", info.getDisplayName()), true);
			} else {
				commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.center_x.success.not", info.getDisplayName()), true);
			}

			return 0;
		}
	}

	private static int setCenterY(CommandContext<CommandSource> commandContext, boolean center) throws CommandSyntaxException {
		CustomBarManager barManager = getBossBarManager(commandContext.getSource());
		CustomServerBarInfo info = getBossBar(commandContext, barManager);
		if (info.isCenterY() == center) {
			if (center) {
				throw ERROR_ALREADY_VISIBLE.create();
			} else {
				throw ERROR_ALREADY_HIDDEN.create();
			}
		} else {
			info.setCenterY(center);
			barManager.setDirty(true);
			if (center) {
				commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.center_y.success.center", info.getDisplayName()), true);
			} else {
				commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.center_y.success.not", info.getDisplayName()), true);
			}

			return 0;
		}
	}

	private static int setXInverted(CommandContext<CommandSource> commandContext, boolean invertX) throws CommandSyntaxException {
		CustomBarManager barManager = getBossBarManager(commandContext.getSource());
		CustomServerBarInfo info = getBossBar(commandContext, barManager);
		if (info.isXInverted() == invertX) {
			if (invertX) {
				throw ERROR_ALREADY_VISIBLE.create();
			} else {
				throw ERROR_ALREADY_HIDDEN.create();
			}
		} else {
			info.setXInverted(invertX);
			barManager.setDirty(true);
			if (invertX) {
				commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.invert_x.success.visible", info.getDisplayName()), true);
			} else {
				commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.invert_x.success.hidden", info.getDisplayName()), true);
			}

			return 0;
		}
	}

	private static int setYInverted(CommandContext<CommandSource> commandContext, boolean invertY) throws CommandSyntaxException {
		CustomBarManager barManager = getBossBarManager(commandContext.getSource());
		CustomServerBarInfo info = getBossBar(commandContext, barManager);
		if (info.isYInverted() == invertY) {
			if (invertY) {
				throw ERROR_ALREADY_VISIBLE.create();
			} else {
				throw ERROR_ALREADY_HIDDEN.create();
			}
		} else {
			info.setYInverted(invertY);
			barManager.setDirty(true);
			if (invertY) {
				commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.invert_y.success.visible", info.getDisplayName()), true);
			} else {
				commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.invert_y.success.hidden", info.getDisplayName()), true);
			}

			return 0;
		}
	}

	private static int setValue(CommandContext<CommandSource> commandContext, int value) throws CommandSyntaxException {
		CustomBarManager barManager = getBossBarManager(commandContext.getSource());
		CustomServerBarInfo info = getBossBar(commandContext, barManager);
		if (info.getValue() == value) {
			throw ERROR_NO_VALUE_CHANGE.create();
		} else {
			info.setValue(value);
			barManager.setDirty(true);
			commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.value.success", info.getDisplayName(), value), true);
			return value;
		}
	}

	private static int setScale(CommandContext<CommandSource> commandContext, double value) throws CommandSyntaxException {
		CustomBarManager barManager = getBossBarManager(commandContext.getSource());
		CustomServerBarInfo info = getBossBar(commandContext, barManager);
		if (info.getScale() == value) {
			throw ERROR_NO_VALUE_CHANGE.create();
		} else {
			info.setScale(value);
			barManager.setDirty(true);
			commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.scale.success", info.getDisplayName(), value), true);
			return 0;
		}
	}

	private static int setXPos(CommandContext<CommandSource> commandContext, int value) throws CommandSyntaxException {
		CustomBarManager barManager = getBossBarManager(commandContext.getSource());
		CustomServerBarInfo info = getBossBar(commandContext, barManager);
		if (info.getXPos() == value) {
			throw ERROR_NO_VALUE_CHANGE.create();
		} else {
			info.setXPos(value);
			barManager.setDirty(true);
			commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.pos_x.success", info.getDisplayName(), value), true);
			return 0;
		}
	}

	private static int setYPos(CommandContext<CommandSource> commandContext, int value) throws CommandSyntaxException {
		CustomBarManager barManager = getBossBarManager(commandContext.getSource());
		CustomServerBarInfo info = getBossBar(commandContext, barManager);
		if (info.getYPos() == value) {
			throw ERROR_NO_VALUE_CHANGE.create();
		} else {
			info.setYPos(value);
			barManager.setDirty(true);
			commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.pos_y.success", info.getDisplayName(), value), true);
			return 0;
		}
	}

	private static int setMax(CommandContext<CommandSource> commandContext, int max) throws CommandSyntaxException {
		CustomBarManager barManager = getBossBarManager(commandContext.getSource());
		CustomServerBarInfo info = getBossBar(commandContext, barManager);
		if (info.getMax() == max) {
			throw ERROR_NO_MAX_CHANGE.create();
		} else {
			info.setMax(max);
			barManager.setDirty(true);
			commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.max.success", info.getDisplayName(), max), true);
			return max;
		}
	}

	private static int setColor(CommandContext<CommandSource> commandContext, BarInfo.Color color) throws CommandSyntaxException {
		CustomBarManager barManager = getBossBarManager(commandContext.getSource());
		CustomServerBarInfo info = getBossBar(commandContext, barManager);
		if (info.getColor().equals(color)) {
			throw ERROR_NO_COLOR_CHANGE.create();
		} else {
			info.setColor(color);
			barManager.setDirty(true);
			commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.color.success", info.getDisplayName()), true);
			return 0;
		}
	}

	private static int setStyle(CommandContext<CommandSource> commandContext, BarInfo.Overlay overlay) throws CommandSyntaxException {
		CustomBarManager barManager = getBossBarManager(commandContext.getSource());
		CustomServerBarInfo info = getBossBar(commandContext, barManager);
		if (info.getOverlay().equals(overlay)) {
			throw ERROR_NO_STYLE_CHANGE.create();
		} else {
			info.setOverlay(overlay);
			barManager.setDirty(true);
			commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.style.success", info.getDisplayName()), true);
			return 0;
		}
	}

	private static int setName(CommandContext<CommandSource> commandContext, ITextComponent textComponent) throws CommandSyntaxException {
		CustomBarManager barManager = getBossBarManager(commandContext.getSource());
		CustomServerBarInfo info = getBossBar(commandContext, barManager);
		ITextComponent itextcomponent = TextComponentUtils.updateForEntity(commandContext.getSource(), textComponent, (Entity)null, 0);
		if (info.getName().equals(itextcomponent)) {
			throw ERROR_NO_NAME_CHANGE.create();
		} else {
			info.setName(itextcomponent);
			barManager.setDirty(true);
			commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.name.success", info.getDisplayName()), true);
			return 0;
		}
	}

	private static int setPlayers(CommandContext<CommandSource> commandContext, Collection<ServerPlayerEntity> playerEntities) throws CommandSyntaxException {
		CustomBarManager barManager = getBossBarManager(commandContext.getSource());
		CustomServerBarInfo info = getBossBar(commandContext, barManager);
		boolean flag = info.setPlayers(playerEntities);
		if (!flag) {
			throw ERROR_NO_PLAYER_CHANGE.create();
		} else {
			if (info.getPlayers().isEmpty()) {
				commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.players.success.none", info.getDisplayName()), true);
			} else {
				commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.barsfordays.set.players.success.some", info.getDisplayName(), playerEntities.size(), TextComponentUtils.formatList(playerEntities, PlayerEntity::getDisplayName)), true);
			}

			return info.getPlayers().size();
		}
	}

	private static int listBars(CommandSource source) {
		CustomBarManager barManager = CustomBarManager.get(source.getServer().getLevel(World.OVERWORLD));
		Collection<CustomServerBarInfo> collection = barManager.getEvents();
		if (collection.isEmpty()) {
			source.sendSuccess(new TranslationTextComponent("commands.barsfordays.list.bars.none"), false);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.barsfordays.list.bars.some", collection.size(), TextComponentUtils.formatList(collection, CustomServerBarInfo::getDisplayName)), false);
		}

		return collection.size();
	}

	private static int createBar(CommandSource source, ResourceLocation location, ITextComponent textComponent) throws CommandSyntaxException {
		CustomBarManager barManager = CustomBarManager.get(source.getServer().getLevel(World.OVERWORLD));
		if (barManager.get(location) != null) {
			throw ERROR_ALREADY_EXISTS.create(location.toString());
		} else {
			CustomServerBarInfo customServerBarInfo = barManager.create(location, TextComponentUtils.updateForEntity(source, textComponent, (Entity)null, 0));
			barManager.setDirty(true);
			source.sendSuccess(new TranslationTextComponent("commands.barsfordays.create.success", customServerBarInfo.getDisplayName()), true);
			return barManager.getEvents().size();
		}
	}

	private static int removeBar(CommandSource source, CustomServerBarInfo info) {
		CustomBarManager barManager = CustomBarManager.get(source.getServer().getLevel(World.OVERWORLD));
		info.removeAllPlayers();
		barManager.remove(info);
		barManager.setDirty(true);
		source.sendSuccess(new TranslationTextComponent("commands.barsfordays.remove.success", info.getDisplayName()), true);
		return barManager.getEvents().size();
	}


	public static CustomBarManager getBossBarManager(CommandSource source) {
		return CustomBarManager.get(source.getServer().getLevel(World.OVERWORLD));
	}

	public static CustomServerBarInfo getBossBar(CommandContext<CommandSource> commandContext, CustomBarManager barManager) throws CommandSyntaxException {
		ResourceLocation resourcelocation = ResourceLocationArgument.getId(commandContext, "id");
		CustomServerBarInfo customServerBarInfo = barManager.get(resourcelocation);
		if (customServerBarInfo == null) {
			throw ERROR_DOESNT_EXIST.create(resourcelocation.toString());
		} else {
			return customServerBarInfo;
		}
	}
}
