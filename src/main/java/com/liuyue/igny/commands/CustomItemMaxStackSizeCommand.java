package com.liuyue.igny.commands;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.data.CustomItemMaxStackSizeDataManager;
import com.liuyue.igny.utils.CommandPermissions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
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
        dispatcher.register(Commands.literal("customItemMaxStackSize")
                .requires(source -> CommandPermissions.canUseCommand(source, IGNYSettings.commandCustomItemMaxStackSize))
                .then(Commands.literal("set")
                        .then(Commands.argument("item", ItemArgument.item(commandBuildContext))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1, 99))
                                        .executes(context -> {
                                            Item item = ItemArgument.getItem(context, "item").getItem();
                                            int count = IntegerArgumentType.getInteger(context, "count");
                                            CustomItemMaxStackSizeDataManager.set(item, count);
                                            context.getSource().sendSuccess(
                                                    //#if MC > 11904
                                                    () ->
                                                    //#endif
                                                            Component.translatable("igny.command.CustomItemMaxStackSize.set_success", Component.translatable(item.getDescriptionId()), count), true
                                            );
                                            return 1;
                                        }))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("item", ItemArgument.item(commandBuildContext))
                                .executes(context -> {
                                    Item item = ItemArgument.getItem(context, "item").getItem();
                                    CustomItemMaxStackSizeDataManager.remove(item);
                                    context.getSource().sendSuccess(
                                            //#if MC > 11904
                                            () ->
                                                    //#endif
                                                    Component.translatable("igny.command.CustomItemMaxStackSize.remove_success", Component.translatable(item.getDescriptionId())), true);
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
                            stacks.forEach((id, size) -> {
                                ResourceLocation res = ResourceLocation.tryParse(id);
                                if (res == null) return;
                                Component deleteButton = Component.literal("[X] ")
                                        .withStyle(ChatFormatting.RED)
                                        .withStyle(style -> style
                                                //#if MC >= 12105
                                                //$$ .withClickEvent(new ClickEvent.RunCommand("/customItemMaxStackSize remove " + id))
                                                //$$ .withHoverEvent(new HoverEvent.ShowText(Component.translatable("igny.command.CustomItemMaxStackSize.click_to_remove"))));
                                                //#else
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/customItemMaxStackSize remove " + id))
                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("igny.command.CustomItemMaxStackSize.click_to_remove"))));
                                //#endif
                                BuiltInRegistries.ITEM.getOptional(res).ifPresent(holder -> {
                                    MutableComponent nameComponent = Component.translatable(holder.getDescriptionId())
                                            .withStyle(ChatFormatting.AQUA)
                                            .withStyle(style -> style
                                                            //#if MC >= 12105
                                                            //$$ .withHoverEvent(new HoverEvent.ShowText(Component.literal(id)))
                                                            //#else
                                                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(id)))
                                                    //#endif
                                            );
                                    MutableComponent sizeComponent = Component.literal(": " + size)
                                            .withStyle(ChatFormatting.WHITE);
                                    MutableComponent entry = Component.empty()
                                            .append(nameComponent)
                                            .append(sizeComponent);
                                    context.getSource().sendSuccess(
                                            //#if MC > 11904
                                            () ->
                                                    //#endif
                                                    Component.empty().append(deleteButton).append(entry), false);
                                });
                            });

                            return 1;
                        }))
        );
    }
}