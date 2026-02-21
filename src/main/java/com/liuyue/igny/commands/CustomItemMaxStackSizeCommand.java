package com.liuyue.igny.commands;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.data.CustomItemMaxStackSizeDataManager;
import com.liuyue.igny.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;

public class CustomItemMaxStackSizeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
        //#if MC >= 12006
        dispatcher.register(Commands.literal("customItemMaxStackSize")
                .requires(source -> CommandUtils.canUseCommand(source, IGNYSettings.commandCustomItemMaxStackSize))
                .then(Commands.literal("set")
                        .then(Commands.argument("predicate", ItemPredicateArgument.itemPredicate(commandBuildContext))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1, 99))
                                        .executes(context -> {
                                            String pattern = getRawArgumentString(context, "predicate");
                                            int count = IntegerArgumentType.getInteger(context, "count");
                                            CustomItemMaxStackSizeDataManager.set(pattern, count, commandBuildContext);
                                            Component nameDisplay = getNameComponent(pattern);
                                            context.getSource().sendSuccess(
                                                    //#if MC > 11904
                                                    () ->
                                                            //#endif
                                                     Component.translatable("igny.command.CustomItemMaxStackSize.set_success", nameDisplay, count), true
                                            );
                                            return 1;
                                        }))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("pattern", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(CustomItemMaxStackSizeDataManager.getCustomStacks().keySet(), builder))
                                .executes(context -> {
                                    String pattern = StringArgumentType.getString(context, "pattern");
                                    CustomItemMaxStackSizeDataManager.remove(pattern, commandBuildContext);
                                    Component nameDisplay = getNameComponent(pattern);
                                    context.getSource().sendSuccess(
                                            //#if MC > 11904
                                            () ->
                                                    //#endif
                                             Component.translatable("igny.command.CustomItemMaxStackSize.remove_success", nameDisplay), true);
                                    return 1;
                                })))
                .then(Commands.literal("clear")
                        .executes(context -> {
                            CustomItemMaxStackSizeDataManager.clear();
                            context.getSource().sendSuccess(
                                    //#if MC > 11904
                                    () ->
                                            //#endif
                                     Component.translatable("igny.command.CustomItemMaxStackSize.clear_success"), true);
                            return 1;
                        }))
                .then(Commands.literal("list")
                        .executes(context -> {
                            Map<String, Integer> stacks = CustomItemMaxStackSizeDataManager.getCustomStacks();
                            if (stacks.isEmpty()) {
                                context.getSource().sendSuccess(
                                        //#if MC > 11904
                                        () ->
                                                //#endif
                                         Component.translatable("igny.command.CustomItemMaxStackSize.list_empty"), false);
                                return 1;
                            }
                            context.getSource().sendSuccess(
                                    //#if MC > 11904
                                    () ->
                                            //#endif
                                     Component.translatable("igny.command.CustomItemMaxStackSize.list_header").withStyle(ChatFormatting.GOLD), false);
                            stacks.forEach((pattern, size) -> {
                                Component deleteButton = Component.literal("[X] ")
                                        .withStyle(ChatFormatting.RED)
                                        .withStyle(style -> style
                                                //#if MC >= 12105
                                                //$$ .withClickEvent(new ClickEvent.RunCommand("/customItemMaxStackSize remove " + pattern))
                                                //$$ .withHoverEvent(new HoverEvent.ShowText(Component.translatable("igny.command.CustomItemMaxStackSize.click_to_remove"))));
                                                //#else
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/customItemMaxStackSize remove " + "\"" + pattern + "\""))
                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("igny.command.CustomItemMaxStackSize.click_to_remove"))));
                                                //#endif
                                ChatFormatting color = pattern.startsWith("#") || pattern.startsWith("*") ? ChatFormatting.YELLOW : ChatFormatting.AQUA;
                                MutableComponent nameComponent = getNameComponent(pattern).withStyle(color)
                                            .withStyle(style ->
                                                    //#if MC >= 12105
                                                    //$$ style.withHoverEvent(new HoverEvent.ShowText(Component.literal(pattern)))
                                                    //#else
                                                    style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(pattern)))
                                                    //#endif
                                            );
                                MutableComponent sizeComponent = Component.literal(": " + size).withStyle(ChatFormatting.WHITE);
                                context.getSource().sendSuccess(
                                        //#if MC > 11904
                                        () ->
                                        //#endif
                                        Component.empty().append(deleteButton).append(nameComponent).append(sizeComponent), false);
                            });
                            return 1;
                        }))
        );
        //#endif
    }

    private static MutableComponent getNameComponent(String pattern) {
        if (!pattern.startsWith("#") || pattern.startsWith("*")) {
            ResourceLocation res = ResourceLocation.tryParse(pattern);
            if (res != null) {
                Item item = BuiltInRegistries.ITEM.
                        //#if MC >= 12102
                        //$$ getValue(res);
                        //#else
                        get(res);
                        //#endif
                if (item != BuiltInRegistries.ITEM.
                        //#if MC >= 12102
                        //$$ getValue(BuiltInRegistries.ITEM.getDefaultKey())
                        //#else
                        get(BuiltInRegistries.ITEM.getDefaultKey())
                        //#endif
                ) {
                    return Component.translatable(item.getDescriptionId());
                }
            }
        }
        return Component.literal(pattern);
    }

    public static String getRawArgumentString(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context, String argumentName) {
        String raw = context.getNodes().stream()
                .filter(node -> node.getNode().getName().equals(argumentName))
                .findFirst()
                .map(node -> context.getInput().substring(node.getRange().getStart(), node.getRange().getEnd()))
                .orElse("");

        if (!raw.isEmpty() && !raw.contains(":")) {
            if (raw.startsWith("#")) {
                return "#minecraft:" + raw.substring(1);
            } else {
                return "minecraft:" + raw;
            }
        }
        return raw;
    }
}