package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.ISignBlockEntity;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin implements ISignBlockEntity {
    @Unique
    private boolean igny$pendingWaxed;

    @Override
    public boolean igny$isPendingWaxed() {
        return this.igny$pendingWaxed;
    }

    @Override
    public void igny$setPendingWaxed(boolean pending) {
        this.igny$pendingWaxed = pending;
    }

    @Inject(method = "updateSignText", at = @At(value = "RETURN"))
    private void igny_applyPendingWaxed(Player player, boolean front, List<FilteredText> lines, CallbackInfo ci) {
        if (this.igny$pendingWaxed) {
            SignBlockEntity self = (SignBlockEntity) (Object) this;
            self.setWaxed(true);
            this.igny$pendingWaxed = false;
        }
    }
}
