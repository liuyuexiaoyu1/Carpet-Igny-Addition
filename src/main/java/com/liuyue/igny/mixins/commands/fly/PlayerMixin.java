package com.liuyue.igny.mixins.commands.fly;

import com.liuyue.igny.utils.interfaces.flyCommand.FlyState;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
//? < 1.20.1 ?import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin implements FlyState {
    @Unique
    private boolean igny$flyCommandOn = false;

    @Override
    public boolean igny$isFlyCommandOn() {
        return this.igny$flyCommandOn;
    }

    @Override
    public void igny$setFlyCommandOn(boolean value) {
        this.igny$flyCommandOn = value;
    }

    //#if >= 1.20.1
    @WrapOperation(
            method = "getDestroySpeed",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z")
    )
    //#else
    /*$$@WrapOperation(
            method = "getDestroySpeed",
            at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/entity/player/Player;onGround:Z")
    )$$*/
    //#endif
    private boolean igny$skipAirMiningPenalty(Player instance, Operation<Boolean> original) {
        Player self = (Player) (Object) this;
        if (self.getAbilities().mayfly && self.getAbilities().flying) {
            return true;
        }
        return original.call(instance);
    }
}
