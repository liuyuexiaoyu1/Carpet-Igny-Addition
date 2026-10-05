package com.liuyue.igny.rule.validators;

import carpet.utils.Translations;
import com.liuyue.igny.rule.ValueValidator;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class ConfirmRuleValidator {

    private static final long TIMEOUT_MS = 60_000L;

    private static final Map<ServerPlayer, Pending> PENDING =
            Collections.synchronizedMap(new WeakHashMap<>());

    private record Pending(String ruleName, String value, long stamp) {
    }

    public static <T> ValueValidator<T> create(String ruleName) {
        return new ValueValidator<>() {
            @Override
            public boolean validate(T newValue) {
                return false;
            }

            @Override
            public boolean validate(CommandSourceStack source, T newValue) {
                ServerPlayer player = source == null ? null : source.getPlayer();
                if (player == null) {
                    return true;
                }
                String raw = String.valueOf(newValue);
                long now = System.currentTimeMillis();
                Pending pending = PENDING.get(player);
                if (pending != null
                        && pending.ruleName().equals(ruleName)
                        && pending.value().equals(raw)
                        && now - pending.stamp() <= TIMEOUT_MS) {
                    PENDING.remove(player);
                    return true;
                }
                PENDING.put(player, new Pending(ruleName, raw, now));
                source.sendSystemMessage(Component.literal(String.format(
                        Translations.tr("igny.rule.confirm.pending"),
                        ruleName, raw)).withStyle(ChatFormatting.YELLOW));
                return false;
            }

            @Override
            public boolean shouldSendDetail() {
                return false;
            }

            @Override
            public Component errorMessage() {
                return Component.empty();
            }
        };
    }
}
