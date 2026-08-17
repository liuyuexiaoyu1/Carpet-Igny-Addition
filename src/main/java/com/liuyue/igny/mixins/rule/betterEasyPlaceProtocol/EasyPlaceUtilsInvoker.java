package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

//#if MC >= 12101
import fi.dy.masa.litematica.util.EasyPlaceUtils;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.gen.Invoker;
//#else
//$$ import com.liuyue.igny.utils.compat.DummyClass;
//#endif
import org.spongepowered.asm.mixin.Mixin;


//#if MC <= 12006
//$$ @Mixin(DummyClass.class)
//#else
@Mixin(EasyPlaceUtils.class)
//#endif
public interface EasyPlaceUtilsInvoker {
    //#if MC >= 12101
    @Invoker("cacheEasyPlacePosition")
    static void invokeCacheEasyPlacePosition(BlockPos pos) {}
    //#endif
}
