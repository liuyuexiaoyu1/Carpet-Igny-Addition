package com.liuyue.igny.commands;

import carpet.utils.Translations;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.CommandUtil;
import com.liuyue.igny.utils.interfaces.flyCommand.FlyState;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;

public class FlyCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("fly")
                        .requires(source -> source.isPlayer() && CommandUtil.canUseCommand(source.getPlayer(), IGNYSettings.COMMAND_FLY.value()))
                        .executes(FlyCommand::toggle)
        );
    }

    private static int toggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        Abilities abilities = player.getAbilities();
        abilities.mayfly = !abilities.mayfly;
        abilities.flying = abilities.mayfly;
        ((FlyState) player).igny$setFlyCommandOn(abilities.mayfly);
        player.onUpdateAbilities();
        boolean enabled = abilities.mayfly;
        source.sendSuccess(
                () -> //?> 1.19.4
                        Component.literal(Translations.tr(enabled
                                ? "igny.command.fly.enabled"
                                : "igny.command.fly.disabled")),
                false);
        return 1;
    }
}
