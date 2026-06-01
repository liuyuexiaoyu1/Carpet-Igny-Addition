package com.liuyue.igny.mixins.rule.enderDragonRemoveSkipReintroduce;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EnderDragon.class)
public class EnderDragonMixin {
    @Shadow
    public int dragonDeathTime;

    @WrapOperation(
            method = "tickDeath",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"
                    )
            ),
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;dragonDeathTime:I",
                    ordinal = 0,
                    opcode = Opcodes.GETFIELD
            )
    )
    private int wrapDragonDeathTime(EnderDragon instance, Operation<Integer> original) {
        if (IGNYSettings.ENDER_DRAGON_REMOVE_SKIP_REINTRODUCE.value() && this.dragonDeathTime > 200) {
            return 199;
        }
        return original.call(instance);
    }
}
