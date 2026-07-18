package com.liuyue.igny.mixins.rule.scalableLuxCompatible;

import ca.spottedleaf.starlight.common.light.StarLightInterface;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.RuleUtil;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 12101
@Restriction(require = @Condition("scalablelux"))
//#else
//$$ @Restriction(require = @Condition("starlight"))
//#endif
@Mixin(StarLightInterface.class)
public class StarLightInterfaceMixin {
    @Inject(method = "blockChange", at = @At(value = "HEAD"), cancellable = true)
    private void blockChange(BlockPos pos, CallbackInfoReturnable<StarLightInterface.LightQueue.ChunkTasks> cir) {
        if (!shouldEnqueueLightTask()) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "sectionChange", at = @At(value = "HEAD"), cancellable = true)
    private void sectionChange(SectionPos pos, boolean newEmptyValue, CallbackInfoReturnable<StarLightInterface.LightQueue.ChunkTasks> cir) {
        if (!shouldEnqueueLightTask()) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "propagateChanges", at = @At(value = "HEAD"), cancellable = true)
    private void propagateChanges(CallbackInfo ci) {
        if (!shouldExecuteLightTask()) {
            ci.cancel();
        }
    }

    @Unique
    @SuppressWarnings("all")
    private boolean shouldEnqueueLightTask() {
        if (!IGNYSettings.SCALABLELUX_COMPATIBLE.value()) return true;
        if (RuleUtil.getCarpetRulesValue("carpet-tis-addition", "lightUpdates") == null) return true;
        return !"ignored".equalsIgnoreCase(((Enum<?>) RuleUtil.getCarpetRulesValue("carpet-tis-addition", "lightUpdates")).name()) &&
                !"off".equalsIgnoreCase(((Enum<?>) RuleUtil.getCarpetRulesValue("carpet-tis-addition", "lightUpdates")).name());
    }

    @Unique
    private boolean shouldExecuteLightTask() {
        if (!IGNYSettings.SCALABLELUX_COMPATIBLE.value()) return true;
        if (RuleUtil.getCarpetRulesValue("carpet-tis-addition", "lightUpdates") == null) return true;
        return !"suppressed".equalsIgnoreCase(((Enum<?>) RuleUtil.getCarpetRulesValue("carpet-tis-addition", "lightUpdates")).name());
    }
}
