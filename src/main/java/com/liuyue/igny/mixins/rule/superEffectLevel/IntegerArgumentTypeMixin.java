package com.liuyue.igny.mixins.rule.superEffectLevel;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.interfaces.superEffectLevel.IArgument;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IntegerArgumentType.class)
public class IntegerArgumentTypeMixin implements IArgument {
    @Mutable
    @Shadow
    @Final
    private int maximum;
    @Unique
    private boolean useDynamicMax = false;

    @Override
    public void carpet_Igny_Addition$setUseDynamicMax(boolean value) {
        this.useDynamicMax = value;
    }

    @Override
    public boolean carpet_Igny_Addition$useDynamicMax() {
        return this.useDynamicMax;
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void integer(int minimum, int maximum, CallbackInfo ci) {
        if (maximum == 255 && IGNYSettings.effectCommandRegistering.get()) {
            this.carpet_Igny_Addition$setUseDynamicMax(true);
        }
    }

    @WrapMethod(method = "parse(Lcom/mojang/brigadier/StringReader;)Ljava/lang/Integer;")
    private Integer parse(StringReader reader, Operation<Integer> original) {
        if (IGNYSettings.superEffectLevel && this.carpet_Igny_Addition$useDynamicMax()) {
            int max = this.maximum;
            try {
                this.maximum = Integer.MAX_VALUE;
                return original.call(reader);
            } finally {
                this.maximum = max;
            }
        }
        return original.call(reader);
    }
}
