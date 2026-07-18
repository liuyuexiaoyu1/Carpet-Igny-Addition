package com.liuyue.igny.mixins.rule.spectatorClickPortalTeleport;

//#if MC >= 12101
import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
//$$ import com.liuyue.igny.utils.compat.DummyClass;
//#endif
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12101
@Mixin(NetherPortalBlock.class)
//#else
//$$ @Mixin(DummyClass.class)
//#endif
public class NetherPortalBlockMixin {
    //#if MC >= 12101
    @Inject(method = "getExitPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getOptionalValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/util/Optional;"), cancellable = true)
    private void getExitPortal(ServerLevel level, Entity entity, BlockPos pos, BlockPos exitPos, boolean isNether, WorldBorder worldBorder, CallbackInfoReturnable<DimensionTransition> cir) {
        if (IGNYSettings.SPECTATOR_CLICK_PORTAL_TELEPORT.value() && entity.isSpectator()) {
            cir.setReturnValue(null);
        }
    }
    //#endif
}
