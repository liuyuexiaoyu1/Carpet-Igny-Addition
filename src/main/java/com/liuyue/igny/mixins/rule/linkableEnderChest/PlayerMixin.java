package com.liuyue.igny.mixins.rule.linkableEnderChest;

import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.ViewingChest;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin implements ViewingChest {
    @Unique private String igny$linkedKey = null;
    @Unique private EnderChestBlockEntity igny$contextChest = null;

    @Override public void igny$setLinkedKey(String key) { this.igny$linkedKey = key; }
    @Override public String igny$getLinkedKey() { return this.igny$linkedKey; }
    @Override public void igny$setContextChest(EnderChestBlockEntity chest) { this.igny$contextChest = chest; }
    @Override public EnderChestBlockEntity igny$getContextChest() { return this.igny$contextChest; }

    @Inject(method = "getEnderChestInventory", at = @At("HEAD"), cancellable = true)
    private void getEnderChestInventory(CallbackInfoReturnable<PlayerEnderChestContainer> cir) {
        if (LinkedContainerManager.isRuleEnabled() && this.igny$linkedKey != null) {
            cir.setReturnValue(LinkedContainerManager.get(this.igny$linkedKey));
        }
    }
}
