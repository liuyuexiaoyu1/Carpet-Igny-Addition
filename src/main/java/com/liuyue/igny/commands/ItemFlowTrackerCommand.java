package com.liuyue.igny.commands;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.CommandUtil;
import com.liuyue.igny.tracker.ItemFlowTracker;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ItemFlowTrackerCommand {
    private static final DynamicCommandExceptionType BAD_HEX = new DynamicCommandExceptionType(
            value -> Component.translatable("igny.command.itemFlowTracker.bad_hex", value));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> mark = literal("mark");

        for (DyeColor color : DyeColor.values()) {
            LiteralArgumentBuilder<CommandSourceStack> node = literal(color.getName());
            addMarkVariants(node, (context, pathInterval) -> capacity -> Tracking.newMark(color, capacity, pathInterval));
            mark.then(node);
        }

        mark.then(literal("hex")
                .then(addMarkVariants(
                        argument("rgb", StringArgumentType.string()),
                        (context, pathInterval) -> {
                            int rgb = parseHex(StringArgumentType.getString(context, "rgb"));
                            return capacity -> Tracking.newMark(rgb, capacity, pathInterval);
                        })));

        dispatcher.register(literal("itemflowtracker")
                .requires(source -> CommandUtil.canUseCommand(source.getPlayer(), IGNYSettings.COMMAND_ITEM_FLOW_TRACKER.value()))
                .executes(context -> igny$disabled(context.getSource()) ? 0 : 1)
                .then(mark)
                .then(literal("status").executes(ItemFlowTrackerCommand::status))
                .then(literal("clear").executes(ItemFlowTrackerCommand::clear)));
    }

    private static boolean igny$disabled(CommandSourceStack source) {
        if (IGNYSettings.ITEM_FLOW_TRACKER.value()) {
            return false;
        }

        source.sendFailure(Component.translatable("igny.command.itemFlowTracker.disabled"));
        return true;
    }

    @FunctionalInterface
    private interface SessionFactory {
        IntFunction<TrackMark> create(CommandContext<CommandSourceStack> context, int pathInterval) throws CommandSyntaxException;
    }

    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T addMarkVariants(T node, SessionFactory factory) {
        node.executes(context -> markHeldItem(context.getSource(), factory.create(context, -1)));

        node.then(argument("track_path", IntegerArgumentType.integer(0))
                .executes(context -> markHeldItem(
                        context.getSource(),
                        factory.create(context, IntegerArgumentType.getInteger(context, "track_path")))));

        node.then(argument("targets", EntityArgument.entities())
                .executes(context -> markEntities(
                        context.getSource(),
                        factory.create(context, -1),
                        EntityArgument.getEntities(context, "targets")))
                .then(argument("track_path", IntegerArgumentType.integer(0))
                        .executes(context -> markEntities(
                                context.getSource(),
                                factory.create(context, IntegerArgumentType.getInteger(context, "track_path")),
                                EntityArgument.getEntities(context, "targets")))));

        return node;
    }

    private static int status(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (igny$disabled(source)) {
            return 0;
        }

        List<TrackMark> sessions = Tracking.activeSessions();

        if (sessions.isEmpty()) {
            source.sendSuccess(
                    () -> //?> 1.19.4
                    Component.translatable("igny.command.itemFlowTracker.status_empty"), false);
            return 0;
        }

        for (TrackMark session : sessions) {
            Component line = Component.translatable(
                    "igny.command.itemFlowTracker.status_entry",
                    label(session),
                    session.budget(),
                    session.capacity());
            source.sendSuccess(
                    () -> //?> 1.19.4
                    line, false);
        }

        return sessions.size();
    }

    private static int clear(CommandContext<CommandSourceStack> context) {
        if (igny$disabled(context.getSource())) {
            return 0;
        }

        ItemFlowTracker.clearAll();
        context.getSource().sendSuccess(
                () -> //?> 1.19.4
                Component.translatable("igny.command.itemFlowTracker.clear_success"), true);
        return 1;
    }

    private static int markHeldItem(CommandSourceStack source, IntFunction<TrackMark> session) throws CommandSyntaxException {
        if (igny$disabled(source)) {
            return 0;
        }

        ServerPlayer player = source.getPlayerOrException();
        ItemStack stack = player.getMainHandItem();

        if (stack.isEmpty()) {
            source.sendFailure(Component.translatable("igny.command.itemFlowTracker.no_held_item"));
            return 0;
        }

        TrackMark mark = session.apply(stack.getCount());
        Tracking.set(stack, mark);
        Component name = stack.getHoverName();
        source.sendSuccess(
                () -> //?> 1.19.4
                Component.translatable("igny.command.itemFlowTracker.mark_held_success", name, label(mark)), false);
        return 1;
    }

    private static int markEntities(CommandSourceStack source, IntFunction<TrackMark> session, Collection<? extends Entity> targets) {
        if (igny$disabled(source)) {
            return 0;
        }

        int marked = 0;
        TrackMark last = null;

        for (Entity entity : targets) {
            if (entity instanceof ItemEntity item && !item.getItem().isEmpty()) {
                last = session.apply(item.getItem().getCount());
                Tracking.set(item.getItem(), last);
                marked++;
            }
        }

        if (marked == 0) {
            source.sendFailure(Component.translatable("igny.command.itemFlowTracker.mark_targets_none"));
            return 0;
        }

        int count = marked;
        Component label = label(last);
        source.sendSuccess(
                () -> //?> 1.19.4
                Component.translatable("igny.command.itemFlowTracker.mark_targets_success", count, label), false);
        return count;
    }

    private static Component label(TrackMark mark) {
        if (mark == null) {
            return Component.literal("-");
        }

        String text = mark.label().startsWith("#")
                ? mark.label()
                : String.format("%s #%06X", mark.label(), mark.rgb() & 0xFFFFFF);
        return Component.literal(text);
    }

    private static int parseHex(String raw) throws CommandSyntaxException {
        String digits = raw.startsWith("#") ? raw.substring(1)
                : raw.regionMatches(true, 0, "0x", 0, 2) ? raw.substring(2)
                : raw;

        if (digits.length() != 6 || digits.chars().anyMatch(c -> Character.digit(c, 16) < 0)) {
            throw BAD_HEX.create(raw);
        }

        return Integer.parseInt(digits, 16);
    }
}
