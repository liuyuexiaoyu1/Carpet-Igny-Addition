package com.liuyue.igny.commands;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.data.CustomPickupDataManager;
import com.liuyue.igny.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

//#if MC >= 12111
//$$ import net.minecraft.commands.Commands;
//#endif

public class CustomPlayerPickupItemCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(
                Commands.literal("customPlayerPickupItem")
                        .requires(source -> CommandUtils.canUseCommand(source.getPlayer(), IGNYSettings.commandCustomPlayerPickupItem))
                        .then(Commands.argument("target", StringArgumentType.string())
                                .suggests(CustomPlayerPickupItemCommand::suggestPlayers)
                                .then(Commands.literal("get")
                                        .executes(CustomPlayerPickupItemCommand::executeGet)
                                )
                                .then(Commands.literal("mode")
                                        .then(Commands.argument("mode", StringArgumentType.string())
                                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(Arrays.asList("disable", "whitelist", "blacklist"), builder))
                                                .executes(CustomPlayerPickupItemCommand::executeMode)
                                        )
                                )
                                .then(Commands.literal("items")
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("item", ItemArgument.item(context))
                                                        .executes(ctx -> executeItemsUpdate(ctx, "add")))
                                        )
                                        .then(Commands.literal("remove")
                                                .then(Commands.argument("item", ItemArgument.item(context))
                                                        .executes(ctx -> executeItemsUpdate(ctx, "remove")))
                                        )
                                        .then(Commands.literal("clear")
                                                .executes(ctx -> executeItemsUpdate(ctx, "clear"))
                                        )
                                )
                        )
        );
    }

    private static int executeGet(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String targetName = StringArgumentType.getString(ctx, "target");
        if (!checkPermission(source, targetName)) return 0;

        CustomPickupDataManager.PlayerSetting setting = CustomPickupDataManager.getOrCreate(targetName);

        MutableComponent message = Component.translatable("igny.command.customPlayerPickupItem.header", targetName)
                .withStyle(ChatFormatting.GOLD).append("\n");

        message.append(Component.translatable("igny.command.customPlayerPickupItem.mode").withStyle(ChatFormatting.YELLOW))
                .append(": ").append(Component.literal(setting.getMode().toString().toLowerCase()).withStyle(ChatFormatting.AQUA)).append("\n");

        message.append(Component.translatable("igny.command.customPlayerPickupItem.items").withStyle(ChatFormatting.YELLOW)).append(":");

        java.util.List<String> items = setting.getItems().stream().sorted().toList();
        if (items.isEmpty()) {
            message.append(" ").append(Component.translatable("igny.command.customPlayerPickupItem.none").withStyle(ChatFormatting.GRAY));
        } else {
            for (String id : items) {
                ResourceLocation res = ResourceLocation.tryParse(id);
                message.append("\n");
                if (res != null) {
                    BuiltInRegistries.ITEM.getOptional(res).ifPresent(holder -> {
                        MutableComponent deleteBtn = Component.literal("[x] ")
                                .withStyle(style -> style.withColor(ChatFormatting.RED).withBold(true)
                                        //#if MC >= 12105
                                        //$$ .withClickEvent(new ClickEvent.RunCommand("/customPlayerPickupItem " + targetName + " items remove " + id))
                                        //$$ .withHoverEvent(new HoverEvent.ShowText(Component.literal("Click to remove " + id))));
                                        //#else
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/customPlayerPickupItem " + targetName + " items remove " + id))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to remove " + id))));
                                        //#endif

                        message.append(Component.literal("  ")).append(deleteBtn)
                                .append(Component.translatable(holder.getDescriptionId()).withStyle(ChatFormatting.AQUA))
                                .append(Component.literal(" (" + id + ")").withStyle(ChatFormatting.GRAY));
                    });
                }
            }
        }

        source.sendSuccess(
                //#if MC > 11904
                () ->
                //#endif
                        message, false);
        return 1;
    }

    private static int executeMode(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String targetName = StringArgumentType.getString(ctx, "target");
        if (!checkPermission(source, targetName)) return 0;

        String modeStr = StringArgumentType.getString(ctx, "mode").toLowerCase();
        CustomPickupDataManager.Mode mode;
        switch (modeStr) {
            case "whitelist" -> mode = CustomPickupDataManager.Mode.WHITELIST;
            case "blacklist" -> mode = CustomPickupDataManager.Mode.BLACKLIST;
            case "disable" -> mode = CustomPickupDataManager.Mode.DISABLED;
            default -> {
                source.sendFailure(Component.translatable("igny.command.customPlayerPickupItem.invalid_mode", modeStr)
                        .withStyle(ChatFormatting.RED));
                return 0;
            }
        }

        CustomPickupDataManager.PlayerSetting setting = CustomPickupDataManager.getOrCreate(targetName);
        setting.setMode(mode);
        CustomPickupDataManager.updateAndSave(targetName, setting);

        source.sendSuccess(
                //#if MC > 11904
                () ->
                //#endif
                        Component.translatable("igny.command.customPlayerPickupItem.mode_success", targetName, modeStr)
                                .withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static int executeItemsUpdate(CommandContext<CommandSourceStack> ctx, String action) {
        CommandSourceStack source = ctx.getSource();
        String targetName = StringArgumentType.getString(ctx, "target");
        if (!checkPermission(source, targetName)) return 0;

        CustomPickupDataManager.PlayerSetting setting = CustomPickupDataManager.getOrCreate(targetName);
        Set<String> currentItems = new HashSet<>(setting.getItems());

        if (action.equals("clear")) {
            currentItems.clear();
        } else {
            Item item = ItemArgument.getItem(ctx, "item")
                    //#if MC >= 26.1
                    //$$ .item().value();
                    //#else
                    .getItem();
                    //#endif
            String itemId = BuiltInRegistries.ITEM.getKey(item).toString();

            if (action.equals("add")) {
                currentItems.add(itemId);
            } else if (action.equals("remove")) {
                currentItems.remove(itemId);
            }
        }

        setting.setItems(currentItems);
        CustomPickupDataManager.updateAndSave(targetName, setting);

        source.sendSuccess(
                //#if MC > 11904
                () ->
                //#endif
                        Component.translatable("igny.command.customPlayerPickupItem.items_success", targetName, currentItems.stream().sorted().collect(Collectors.joining(", "))).withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static boolean checkPermission(CommandSourceStack source, String targetName) {
        CustomPickupDataManager.setServer(source.getServer());
        if (
            //#if MC >= 12111
            //$$ !Commands.LEVEL_GAMEMASTERS.check(source.permissions())
            //#else
                !source.hasPermission(2)
            //#endif
        ) {
            if (source.getPlayer() == null || (!source.getPlayer().getGameProfile()
                    //#if MC >= 12110
                    //$$ .name()
                    //#else
                    .getName()
                    //#endif
                    .equalsIgnoreCase(targetName) && !(source.getPlayer() instanceof EntityPlayerMPFake))) {
                source.sendFailure(Component.translatable("igny.command.customPlayerPickupItem.no_permission")
                        .withStyle(ChatFormatting.RED));
                return false;
            }
        }
        return true;
    }

    private static CompletableFuture<Suggestions> suggestPlayers(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        CommandSourceStack source = context.getSource();
        Collection<String> players;
        if (
            //#if MC >= 12111
            //$$ Commands.LEVEL_GAMEMASTERS.check(source.permissions())
            //#else
                source.hasPermission(2)
            //#endif
        ) {
            players = Arrays.asList(source.getServer().getPlayerNames());
        } else if (source.getPlayer() != null) {
            players = Set.of(source.getPlayer().getGameProfile()
                            //#if MC >= 12110
                            //$$ .name()
                            //#else
                            .getName()
                    //#endif
            );
        } else {
            players = Set.of();
        }
        return SharedSuggestionProvider.suggest(players, builder);
    }
}