package com.liuyue.igny.mixins.rule.wardenNeverDig;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Warden.class)
public class WardenMixin{
    @Inject(method="tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci){
        if(IGNYSettings.wardenNeverDig) {
            Warden warden = (Warden) (Object) this;
            warden.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE,1200L);
        }
    }
}
