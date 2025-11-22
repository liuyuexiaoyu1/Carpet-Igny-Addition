package com.liuyue.igny.utils;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.commands.CommandSourceStack;
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.server.level.ServerPlayer;

public class PlayerCommandPermissions {
    public static boolean canDropEnderChest(CommandSourceStack source, ServerPlayer targetPlayer) {
        if (source == null) {
            return true;
        }

        if (!(targetPlayer instanceof EntityPlayerMPFake)) {
            return source.hasPermission(2);
        }

        String ruleValue = IGNYSettings.CommandPlayerEnderChestDrop;

        switch (ruleValue.toLowerCase()) {
            case "true":
                return true;
            case "false":
                return false;
            case "ops":
                return source.hasPermission(2);
            case "0":
                return source.hasPermission(0);
            case "1":
                return source.hasPermission(1);
            case "2":
                return source.hasPermission(2);
            case "3":
                return source.hasPermission(3);
            case "4":
                return source.hasPermission(4);
            default:
                return source.hasPermission(2);
        }
    }

    public static boolean canDropEnderChest(CommandSourceStack source) {
        return source == null || source.hasPermission(2);
    }
}