// The MIT License (MIT)
//
// Copyright (c) 2020 comp500
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

package com.liuyue.igny.mixins.rule.showClassMixinList;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.TraceUtil;
import net.minecraft.CrashReport;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrashReport.class)
public abstract class CrashReportMixin {
    @Shadow
    private StackTraceElement[] uncategorizedStackTrace;

    @Inject(method = "getDetails(Ljava/lang/StringBuilder;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/CrashReport;details:Ljava/util/List;", opcode = Opcodes.GETFIELD))
    private void getDetails(StringBuilder builder, CallbackInfo ci) {
        if (IGNYSettings.showClassMixinList) {
            int trailingNewlineCount = 0;
            if (builder.charAt(builder.length() - 1) == '\n') {
                builder.deleteCharAt(builder.length() - 1);
                trailingNewlineCount++;
            }
            if (builder.charAt(builder.length() - 1) == '\n') {
                builder.deleteCharAt(builder.length() - 1);
                trailingNewlineCount++;
            }
            TraceUtil.printTrace(uncategorizedStackTrace, builder);
            builder.append("\n".repeat(trailingNewlineCount));
        }
    }
}