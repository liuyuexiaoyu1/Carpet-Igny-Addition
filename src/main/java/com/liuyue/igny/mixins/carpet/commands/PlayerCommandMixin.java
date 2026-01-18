package com.liuyue.igny.mixins.carpet.commands;

import carpet.commands.PlayerCommand;
import carpet.helpers.EntityPlayerActionPack;
import com.liuyue.igny.utils.CommandPermissions;
import carpet.utils.Messenger;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mixin(PlayerCommand.class)
public abstract class PlayerCommandMixin {


    @Shadow
    private static ServerPlayer getPlayer(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        MinecraftServer server = context.getSource().getServer();
        return server.getPlayerList().getPlayerByName(playerName);
    }

    @Shadow
    private static Command<CommandSourceStack> manipulation(Consumer<EntityPlayerActionPack> action) {
        return null;
    }

    @Shadow
    private static int manipulate(CommandContext<CommandSourceStack> context, Consumer<EntityPlayerActionPack> action) {
        return 0;
    }

    @Inject(method = "makeDropCommand", at = @At("HEAD"), cancellable = true,remap = false)
    private static void onMakeDropCommand(String actionName, boolean dropAll, CallbackInfoReturnable<LiteralArgumentBuilder<CommandSourceStack>> cir) {
        LiteralArgumentBuilder<CommandSourceStack> command = literal(actionName);

        command.then(literal("all").executes(context -> {
            ServerPlayer targetPlayer = getPlayer(context);
            if (targetPlayer == null) return 0;

            boolean canDropEnderChest = CommandPermissions.canDropEnderChest(context.getSource(), targetPlayer);
            int slot = canDropEnderChest ? -2 : -3;

            if (!canDropEnderChest && !(targetPlayer instanceof EntityPlayerMPFake)) {
                Messenger.m(context.getSource(),
                        Component.translatable("igny.command.enderchest.real_player_op_only"));
                return 0;
            }

            return manipulation(ap -> ap.drop(slot, dropAll)).run(context);

        }));

        command.then(literal("inventory").executes(manipulation(ap -> ap.drop(-3, dropAll))));

        command.then(literal("enderchest")
                .requires(CommandPermissions::canDropEnderChest)
                .executes(context -> {
                    ServerPlayer targetPlayer = getPlayer(context);
                    if (targetPlayer == null) return 0;

                    if (!CommandPermissions.canDropEnderChest(context.getSource(), targetPlayer)) {
                        Component message;
                        if (!(targetPlayer instanceof EntityPlayerMPFake)) {
                            message = Component.translatable("igny.command.enderchest.real_player_op_only");
                        } else {
                            message = Component.translatable("igny.command.enderchest.no_permission");
                        }
                        Messenger.m(context.getSource(), message);
                        return 0;
                    }

                    return manipulation(ap -> ap.drop(-4, dropAll)).run(context);

                }));

        command.then(literal("mainhand").executes(manipulation(ap -> ap.drop(-1, dropAll))))
                .then(literal("offhand").executes(manipulation(ap -> ap.drop(40, dropAll))))
                .then(argument("slot", IntegerArgumentType.integer(0, 226))
                        .executes(context -> {
                            int slot = IntegerArgumentType.getInteger(context, "slot");
                            ServerPlayer targetPlayer = getPlayer(context);
                            if (targetPlayer == null) return 0;

                            if (slot >= 200 && slot <= 226) {
                                if (!CommandPermissions.canDropEnderChest(context.getSource(), targetPlayer)) {
                                    Component message;
                                    if (!(targetPlayer instanceof EntityPlayerMPFake)) {
                                        message = Component.translatable("igny.command.enderchest.real_player_op_only");
                                    } else {
                                        message = Component.translatable("igny.command.enderchest.no_permission");
                                    }
                                    Messenger.m(context.getSource(), message);
                                    return 0;
                                }
                            }
                            return manipulate(context, ap -> ap.drop(slot, dropAll));
                        }));

        cir.setReturnValue(command);
    }
}