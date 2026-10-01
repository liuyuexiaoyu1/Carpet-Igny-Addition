package com.liuyue.igny.mixins.rule.spectatorCanOperateContainer;

//#if >= 1.20.4
import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.inventory.CrafterScreen;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//#endif

@Mixin(CrafterScreen.class)
public class CrafterScreenMixin {
    @WrapOperation(method = "slotClicked", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z"
    ))
    private boolean igny$spectatorToggleCrafterSlot(Player instance, Operation<Boolean> original) {
        if (IGNYSettings.SPECTATOR_CAN_OPERATE_CONTAINER.value() && instance.isSpectator()) {
            return false;
        }
        return original.call(instance);
    }
}
