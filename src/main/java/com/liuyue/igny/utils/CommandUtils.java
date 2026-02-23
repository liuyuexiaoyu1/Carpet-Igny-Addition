package com.liuyue.igny.utils;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.commands.CommandSourceStack;
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.server.level.ServerPlayer;

//#if MC >= 12111
//$$ import net.minecraft.commands.Commands;
//#endif

import java.util.Locale;

public class CommandUtils {
    public static boolean canDropEnderChest(CommandSourceStack source, ServerPlayer targetPlayer) {
        if (source == null) {
            return true;
        }

        if (!(targetPlayer instanceof EntityPlayerMPFake)) {
            //#if MC >= 12111
            //$$ return Commands.LEVEL_GAMEMASTERS.check(source.permissions());
            //#else
            return source.hasPermission(2);
            //#endif
        }

        String ruleValue = IGNYSettings.commandPlayerEnderChestDrop;

        return switch (ruleValue.toLowerCase()) {
            case "true" -> true;
            case "false" -> false;
            //#if MC >= 12111
            //$$ case "0" -> Commands.LEVEL_ALL.check(source.permissions());
            //$$ case "1" -> Commands.LEVEL_MODERATORS.check(source.permissions());
            //$$ case "3" -> Commands.LEVEL_ADMINS.check(source.permissions());
            //$$ case "4" -> Commands.LEVEL_OWNERS.check(source.permissions());
            //$$ default -> Commands.LEVEL_GAMEMASTERS.check(source.permissions());
            //#else
            case "0" -> source.hasPermission(0);
            case "1" -> source.hasPermission(1);
            case "3" -> source.hasPermission(3);
            case "4" -> source.hasPermission(4);
            default -> source.hasPermission(2);
            //#endif
        };
    }
    public static boolean canUseCommand(ServerPlayer source, Object commandLevel) {
        if (commandLevel instanceof Boolean) {
            return (Boolean) commandLevel;
        }

        if (commandLevel instanceof String) {
            final String levelStr = ((String) commandLevel).toLowerCase(Locale.ENGLISH);

            switch (levelStr) {
                case "true": return true;
                case "false": return false;
                case "ops": return hasPermissionLevel(source, 2);
            }

            if (levelStr.length() == 1) {
                char c = levelStr.charAt(0);
                if (c >= '0' && c <= '4') {
                    return hasPermissionLevel(source, c - '0');
                }
            }
        }

        return false;
    }

    public static boolean hasPermissionLevel(ServerPlayer source, int level) {
        //#if MC >= 12111
        //$$ return switch (level) {
        //$$     case 0 -> Commands.LEVEL_ALL.check(source.permissions());
        //$$     case 1 -> Commands.LEVEL_MODERATORS.check(source.permissions());
        //$$     case 2 -> Commands.LEVEL_GAMEMASTERS.check(source.permissions());
        //$$     case 3 -> Commands.LEVEL_ADMINS.check(source.permissions());
        //$$     case 4 -> Commands.LEVEL_OWNERS.check(source.permissions());
        //$$     default -> false;
        //$$  };
        //#else
        return source.hasPermissions(level);
        //#endif
    }

    public static boolean canDropEnderChest(CommandSourceStack source) {
        //#if MC >= 12111
        //$$ return source == null || Commands.LEVEL_GAMEMASTERS.check(source.permissions());
        //#else
        return source == null || source.hasPermission(2);
        //#endif
    }

}