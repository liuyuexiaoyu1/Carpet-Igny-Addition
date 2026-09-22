package com.liuyue.igny.mixins.rule.linkableEnderChest.compat.minihud;

import com.liuyue.igny.helper.linkableEnderChest.MiniHudPreview;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.LinkedEnderChest;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.minihud.renderer.InventoryOverlayHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#if >= 1.21.11
/*$$import fi.dy.masa.malilib.render.InventoryOverlayContext;
import fi.dy.masa.malilib.util.data.tag.CompoundData;$$*/
//#else
import net.minecraft.nbt.CompoundTag;
//#endif

@Restriction(require = @Condition("minihud"))
@Mixin(InventoryOverlayHandler.class)
//?>= 1.21.11 ? @SuppressWarnings("deprecation")
public abstract class InventoryOverlayHandlerMixin {

    //#if >= 1.21.11
    /*$$@Inject(method = "getTargetInventoryFromBlock", at = @At("HEAD"), cancellable = true)
    private void igny$linkedChestPreview(Level level, BlockPos pos, BlockEntity blockEntity, CompoundData data, CallbackInfoReturnable<InventoryOverlayContext> cir) {
        MinecraftServer server = level.getServer();
        if (server == null || !server.isSingleplayer() || !(blockEntity instanceof LinkedEnderChest linked)) {
            return;
        }
        Container container = linked.igny$getContainer();
        if (container == null) {
            return;
        }
        cir.setReturnValue(MiniHudPreview.build((InventoryOverlayHandler) (Object) this, container, blockEntity, data));
    }$$*/
    //#else
    @Inject(method = "getTargetInventoryFromBlock", at = @At("HEAD"), cancellable = true)
    private void igny$linkedChestPreview(Level level, BlockPos pos, BlockEntity blockEntity, CompoundTag data, CallbackInfoReturnable<InventoryOverlay.Context> cir) {
        MinecraftServer server = level.getServer();
        if (server == null || !server.isSingleplayer() || !(blockEntity instanceof LinkedEnderChest linked)) {
            return;
        }
        Container container = linked.igny$getContainer();
        if (container == null) {
            return;
        }
        cir.setReturnValue(MiniHudPreview.build((InventoryOverlayHandler) (Object) this, container, blockEntity, data));
    }
    //#endif
}
