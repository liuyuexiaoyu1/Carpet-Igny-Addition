package com.liuyue.igny.mixins.rule.linkableEnderChest.compat.minihud;

//#if >= 1.21
import com.liuyue.igny.client.LinkedChestPreviewCache;
import fi.dy.masa.minihud.event.RenderHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if >= 1.21.11
/*$$import fi.dy.masa.malilib.render.GuiContext;$$*/
//#else
import net.minecraft.client.gui.GuiGraphics;
//#endif
//#else
/*$$import com.liuyue.igny.utils.compat.DummyClass;
import org.spongepowered.asm.mixin.Mixin;$$*/
//#endif

//#if >= 1.21
@Restriction(require = @Condition("minihud"))
@Mixin(RenderHandler.class)
public class RenderHandlerMixin {

    //#if >= 1.21.11
    /*$$@Inject(method = "onRenderTooltipLast", at = @At("HEAD"))
    private void igny$beginLinkedPreview(GuiContext context, ItemStack stack, int x, int y, CallbackInfo ci) {
        LinkedChestPreviewCache.beginPreview(stack);
    }

    @Inject(method = "onRenderTooltipLast", at = @At("RETURN"))
    private void igny$endLinkedPreview(GuiContext context, ItemStack stack, int x, int y, CallbackInfo ci) {
        LinkedChestPreviewCache.endPreview();
    }$$*/
    //#else
    @Inject(method = "onRenderTooltipLast", at = @At("HEAD"))
    private void igny$beginLinkedPreview(GuiGraphics gui, ItemStack stack, int x, int y, CallbackInfo ci) {
        LinkedChestPreviewCache.beginPreview(stack);
    }

    @Inject(method = "onRenderTooltipLast", at = @At("RETURN"))
    private void igny$endLinkedPreview(GuiGraphics gui, ItemStack stack, int x, int y, CallbackInfo ci) {
        LinkedChestPreviewCache.endPreview();
    }
    //#endif
}
//#else
/*$$@Mixin(DummyClass.class)
public class RenderHandlerMixin {
}$$*/
//#endif
