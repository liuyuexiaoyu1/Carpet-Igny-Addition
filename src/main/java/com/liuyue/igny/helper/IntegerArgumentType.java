// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.liuyue.igny.helper;

import com.liuyue.igny.IGNYSettings;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings("unused")
public class IntegerArgumentType implements ArgumentType<Integer> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "123", "-123");

    private final int minimum;
    private final int maximum;

    private IntegerArgumentType(final int minimum, final int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static IntegerArgumentType integer() {
        return integer(Integer.MIN_VALUE);
    }

    public static IntegerArgumentType integer(final int min) {
        return integer(min, Integer.MAX_VALUE);
    }

    public static IntegerArgumentType integer(final int min, final int max) {
        return new IntegerArgumentType(min, max);
    }

    public static int getInteger(final CommandContext<?> context, final String name) {
        return context.getArgument(name, int.class);
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    @Override
    public Integer parse(final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final int result = reader.readInt();
        if (result < minimum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(reader, result, minimum);
        }
        int max = IGNYSettings.superEffectLevel ? Integer.MAX_VALUE : maximum;
        if (result > max) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().createWithContext(reader, result, maximum);
        }
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegerArgumentType that)) return false;
        return maximum == that.maximum && minimum == that.minimum;
    }

    @Override
    public int hashCode() {
        return 31 * minimum + maximum;
    }

    @Override
    public String toString() {
        if (minimum == Integer.MIN_VALUE && maximum == Integer.MAX_VALUE) {
            return "integer()";
        } else if (maximum == Integer.MAX_VALUE) {
            return "integer(" + minimum + ")";
        } else {
            return "integer(" + minimum + ", " + maximum + ")";
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
