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

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.InvalidRuleValueException;
import carpet.api.settings.RuleHelper;
import carpet.api.settings.SettingsManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class BuiltRule<T> implements CarpetRule<T> {
    public static final ThreadLocal<Boolean> RULE_UNCHANGED = ThreadLocal.withInitial(() -> false);
    private final String name;
    private final Collection<String> categories;
    private final Collection<String> suggestions;
    private T value;
    private final T defaultValue;
    private final boolean canBeToggledClientSide;
    private final Class<T> type;
    private final Function<String, T> parser;
    private final List<ValueValidator<T>> valueValidators = new ArrayList<>();
    private final List<SilenceValueValidator<T>> silenceValidators = new ArrayList<>();
    private final List<RuleListener<T>> listeners = new ArrayList<>();
    private final boolean strict;
    private boolean enable = false;

    public BuiltRule(
            Class<T> type,
            String name,
            Collection<String> categories,
            Collection<String> suggestions,
            T value,
            boolean canBeToggledClientSide,
            List<ValueValidator<T>> valueValidators,
            List<SilenceValueValidator<T>> silenceValidators,
            List<RuleListener<T>> listeners,
            boolean strict
    ) {
        this.name = name;
        this.categories = categories;
        this.suggestions = suggestions;
        this.value = value;
        this.defaultValue = value;
        this.canBeToggledClientSide = canBeToggledClientSide;
        this.type = type;
        this.valueValidators.addAll(valueValidators);
        this.silenceValidators.addAll(silenceValidators);
        this.listeners.addAll(listeners);
        this.strict = strict;
        this.parser = createParser();
        if (this.strict()) {
            this.valueValidators.addFirst(new StrictValueValidator<>(this));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Function<String, T> createParser() {
        if (this.type == String.class) {
            return s -> this.type.cast(s.toLowerCase(Locale.ROOT));
        } else if (this.type == Boolean.class) {
            return s -> {
                String lower = s.toLowerCase(Locale.ROOT);
                if ("true".equals(lower)) return this.type.cast(Boolean.TRUE);
                if ("false".equals(lower)) return this.type.cast(Boolean.FALSE);
                throw new IllegalArgumentException("Invalid boolean: " + s);
            };
        } else if (this.type == Integer.class) {
            return s -> this.type.cast(Integer.parseInt(s));
        } else if (this.type == Long.class) {
            return s -> this.type.cast(Long.parseLong(s));
        } else if (this.type == Double.class) {
            return s -> this.type.cast(Double.parseDouble(s));
        } else if (this.type == Float.class) {
            return s -> this.type.cast(Float.parseFloat(s));
        } else if (this.type.isEnum()) {
            return s -> {
                Class<? extends Enum> clazz = (Class<? extends Enum>) this.type;
                return (T) Enum.valueOf(clazz, s.toUpperCase(Locale.ROOT));
            };
        } else if (this.type == CommandPermissionLevel.class) {
            return s -> this.type.cast(CommandPermissionLevel.of(s));
        } else {
            throw new UnsupportedOperationException("Unsupported type: " + this.type);
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public List<Component> extraInfo() {
        return List.of();
    }

    @Override
    public Collection<String> categories() {
        return this.categories;
    }

    @Override
    public Collection<String> suggestions() {
        return this.suggestions;
    }

    @Override
    public SettingsManager settingsManager() {
        return CarpetServer.settingsManager;
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public boolean canBeToggledClientSide() {
        return this.canBeToggledClientSide;
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    @Override
    public T defaultValue() {
        return this.defaultValue;
    }

    @Override
    public boolean strict() {
        return this.strict && !this.suggestions().isEmpty();
    }

    @Override
    public void set(@Nullable CommandSourceStack source, String value) throws InvalidRuleValueException {
        T parsed;
        try {
            parsed = this.parser.apply(value);
        } catch (RuntimeException e) {
            throw new InvalidRuleValueException("Invalid value for rule " + this.name + ": " + value);
        }
        this.set(source, parsed, value);
    }

    @Override
    public void set(@Nullable CommandSourceStack source, T value) throws InvalidRuleValueException {
        this.set(source, value, RuleHelper.toRuleString(value));
    }

    private void set(@Nullable CommandSourceStack source, T value, String userInput) throws InvalidRuleValueException {
        for (ValueValidator<T> valueValidator : this.valueValidators) {
            if (!valueValidator.validate(value)) {
                if (source != null && source.getEntity() instanceof net.minecraft.server.level.ServerPlayer) {
                    valueValidator.notifyFailure(source, this, userInput);
                    throw new InvalidRuleValueException("Invalid value for rule " + this.name + ": " + userInput);
                }
                value = this.defaultValue;
                userInput = RuleHelper.toRuleString(this.defaultValue);
                break;
            }
        }
        if (value.equals(this.value()) && source != null) {
            return;
        }
        T finalValue = value;
        boolean canChange = this.silenceValidators.stream().allMatch(observer -> observer.validate(source, finalValue));
        if (canChange) {
            this.value = value;
            this.enable = value instanceof Boolean b ? b : !this.value.equals(this.defaultValue);
            if (source != null) {
                this.settingsManager().notifyRuleChanged(source, this, userInput);
            }
            T finalValue1 = value;
            this.listeners.forEach(listener -> listener.onChanged(source, finalValue1));
        } else {
            RULE_UNCHANGED.set(true);
        }
    }

    public boolean isEnable() {
        return this.enable;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BuiltRule<?> that = (BuiltRule<?>) o;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return this.name + ": " + RuleHelper.toRuleString(value());
    }
}
