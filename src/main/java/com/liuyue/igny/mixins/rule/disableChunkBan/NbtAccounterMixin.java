package com.liuyue.igny.mixins.rule.disableChunkBan;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.NbtAccounter;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NbtAccounter.class)
public class NbtAccounterMixin {
    @WrapOperation(method = "accountBytes(J)V", at = @At(value = "FIELD", target = "Lnet/minecraft/nbt/NbtAccounter;quota:J", ordinal = 0, opcode = Opcodes.GETFIELD), require = 0)
    private long accountBytes(NbtAccounter instance, Operation<Long> original) {
        return IGNYSettings.DISABLE_CHUNK_BAN.value() ? Long.MAX_VALUE : original.call(instance);
    }
}
