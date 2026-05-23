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

import carpet.api.settings.CarpetRule;
import carpet.api.settings.RuleHelper;
import net.minecraft.network.chat.Component;

public class StrictValueValidator<T> implements ValueValidator<T> {
    private final CarpetRule<T> rule;

    public StrictValueValidator(CarpetRule<T> rule) {
        this.rule = rule;
    }

    @Override
    public boolean validate(T newValue) {
        return this.rule.suggestions().contains(RuleHelper.toRuleString(newValue));
    }

    @Override
    public Component errorMessage() {
        return Component.literal("Valid options: " + String.join(", ", this.rule.suggestions()));
    }
}
