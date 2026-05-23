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

import com.liuyue.igny.utils.IGNYRuleCategory;
import carpet.api.settings.RuleCategory;
import carpet.utils.CommandHelper;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class RuleFactory {
    public static Builder<Boolean> of(String rule, boolean value) {
        return new Builder<>(Boolean.class, rule, value);
    }

    public static Builder<Integer> of(String rule, int value) {
        return new Builder<>(Integer.class, rule, value);
    }

    public static Builder<Long> of(String rule, long value) {
        return new Builder<>(Long.class, rule, value);
    }

    public static Builder<Float> of(String rule, float value) {
        return new Builder<>(Float.class, rule, value);
    }

    public static Builder<Double> of(String rule, double value) {
        return new Builder<>(Double.class, rule, value);
    }

    public static Builder<String> of(String rule, String value) {
        return new Builder<>(String.class, rule, value);
    }

    public static Builder<CommandPermissionLevel> of(String rule, CommandPermissionLevel value) {
        return new Builder<>(CommandPermissionLevel.class, rule, value);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> Builder<T> of(String rule, T value) {
        return new Builder<>((Class<T>) value.getClass(), rule, value);
    }

    public static final class Builder<T> {
        private final Class<T> type;
        private final String name;
        private final Collection<String> categories = new LinkedHashSet<>();
        private final Collection<String> suggestions;
        private final T value;
        private final List<ValueValidator<T>> validators = new ArrayList<>();
        private final List<SilenceValueValidator<T>> silenceValidators = new ArrayList<>();
        private final List<RuleListener<T>> listeners = new ArrayList<>();
        private boolean client = false;
        private boolean strict = true;

        private Builder(Class<T> type, String rule, T value) {
            this.categories.add(IGNYRuleCategory.IGNY);
            if (rule.isBlank()) {
                throw new IllegalArgumentException("Carpet rule name is empty");
            }
            this.type = type;
            this.value = value;
            this.name = rule;
            if (this.type == Boolean.class) {
                this.suggestions = List.of("true", "false");
            } else if (this.type.isEnum()) {
                this.suggestions = Arrays.stream(this.type.getEnumConstants())
                        .map(e -> ((Enum<?>) e).name().toLowerCase(Locale.ROOT))
                        .toList();
            } else if (this.type == CommandPermissionLevel.class) {
                this.suggestions = List.of("true", "false", "ops", "0", "1", "2", "3", "4");
            } else {
                this.suggestions = new LinkedHashSet<>();
            }
        }

        public Builder<T> addCategories(String... categories) {
            this.categories.addAll(Arrays.asList(categories));
            return this;
        }

        public Builder<T> addOptions(String... options) {
            return this.addOptions(List.of(options));
        }

        public Builder<T> addOptions(int... options) {
            return this.addOptions(Arrays.stream(options).mapToObj(Integer::toString).toList());
        }

        public Builder<T> addOptions(List<String> list) {
            if (list.isEmpty()) {
                throw new IllegalArgumentException("At least one option must be provided");
            }
            this.suggestions.addAll(list);
            return this;
        }

        public Builder<T> setCommand() {
            this.categories.add(RuleCategory.COMMAND);
            return this.addListener((source, value) -> {
                if (source != null) {
                    CommandHelper.notifyPlayersCommandsChanged(source.getServer());
                }
            });
        }

        public Builder<T> setClient() {
            this.client = true;
            this.categories.add(RuleCategory.CLIENT);
            return this;
        }

        public Builder<T> setLenient() {
            this.strict = false;
            return this;
        }

        public Builder<T> addValidator(Predicate<T> condition, Supplier<Component> errorMessage) {
            return this.addValidator(ValueValidator.of(condition, errorMessage));
        }

        public Builder<T> addValidator(ValueValidator<T> validator) {
            this.validators.add(validator);
            return this;
        }

        public Builder<T> addSilenceValidator(SilenceValueValidator<T> observer) {
            this.silenceValidators.add(observer);
            return this;
        }

        public Builder<T> addListener(RuleListener<T> listener) {
            this.listeners.add(listener);
            return this;
        }

        public RuleContext<T> build() {
            Supplier<BuiltRule<T>> supplier = () -> new BuiltRule<>(
                    this.type, this.name, this.categories, this.suggestions,
                    this.value, this.client, this.validators, this.silenceValidators,
                    this.listeners, this.strict
            );
            return new RuleContext<>(this.type, this.value, this.name, supplier,
                    this.categories, this.suggestions);
        }
    }
}
