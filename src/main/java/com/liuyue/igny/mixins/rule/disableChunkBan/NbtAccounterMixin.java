package com.liuyue.igny.mixins.rule.disableChunkBan;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.nbt.NbtAccounter;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NbtAccounter.class)
public class NbtAccounterMixin {
    @Inject(method = "accountBytes(J)V", at = @At(value = "FIELD", target = "Lnet/minecraft/nbt/NbtAccounter;quota:J", ordinal = 0, opcode = Opcodes.GETFIELD), cancellable = true, require = 0)
    private void accountBytes(long bytes, CallbackInfo ci) {
        if (IGNYSettings.DISABLE_CHUNK_BAN.value()) {
            ci.cancel();
        }
    }
}
