// MIT License
//
// Copyright (c) 2024 fcsailboat
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.liuyue.igny.rule;

import com.liuyue.igny.utils.CommandUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class CommandPermissionLevel {
    private static final Map<String, CommandPermissionLevel> LEVELS = new HashMap<>();
    public static final CommandPermissionLevel TRUE = of("true");
    public static final CommandPermissionLevel FALSE = of("false");
    public static final CommandPermissionLevel OPS = of("ops");

    private final String option;

    private CommandPermissionLevel(String option) {
        this.option = option;
    }

    public static CommandPermissionLevel of(String option) {
        String lowerCase = option.toLowerCase(Locale.ROOT);
        return LEVELS.computeIfAbsent(lowerCase, level -> switch (lowerCase) {
            case "true", "false", "ops", "0", "1", "2", "3", "4" -> new CommandPermissionLevel(lowerCase);
            default -> throw new IllegalArgumentException("Invalid command permission level: " + option);
        });
    }

    public boolean canExecute(CommandSourceStack source) {
        if (this == TRUE) return true;
        if (this == FALSE) return false;
        if (source == null) return false;
        //#if MC >= 12111
        //$$ return switch (this.option) {
        //$$     case "0" -> net.minecraft.commands.Commands.LEVEL_ALL.check(source.permissions());
        //$$     case "1" -> net.minecraft.commands.Commands.LEVEL_MODERATORS.check(source.permissions());
        //$$     case "3" -> net.minecraft.commands.Commands.LEVEL_ADMINS.check(source.permissions());
        //$$     case "4" -> net.minecraft.commands.Commands.LEVEL_OWNERS.check(source.permissions());
        //$$     default -> net.minecraft.commands.Commands.LEVEL_GAMEMASTERS.check(source.permissions());
        //$$ };
        //#else
        return switch (this.option) {
            case "ops", "2" -> source.hasPermission(2);
            case "0" -> source.hasPermission(0);
            case "1" -> source.hasPermission(1);
            case "3" -> source.hasPermission(3);
            case "4" -> source.hasPermission(4);
            default -> false;
        };
        //#endif
    }

    public boolean canExecute(ServerPlayer player) {
        if (this == TRUE) return true;
        if (this == FALSE) return false;
        return switch (this.option) {
            case "ops", "2" -> CommandUtil.hasPermissionLevel(player, 2);
            case "0" -> CommandUtil.hasPermissionLevel(player, 0);
            case "1" -> CommandUtil.hasPermissionLevel(player, 1);
            case "3" -> CommandUtil.hasPermissionLevel(player, 3);
            case "4" -> CommandUtil.hasPermissionLevel(player, 4);
            default -> false;
        };
    }

    public boolean isTrue() {
        return this == TRUE;
    }

    public boolean isFalse() {
        return this == FALSE;
    }

    @Override
    public String toString() {
        return this.option;
    }
}
