package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

//#if >= 1.21.1
import fi.dy.masa.litematica.util.EasyPlaceUtils;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.gen.Invoker;
//#else
//$$ import com.liuyue.igny.utils.compat.DummyClass;
//#endif
import org.spongepowered.asm.mixin.Mixin;


@Mixin(EasyPlaceUtils.class) //#replace <= 1.20.6 ? @Mixin(DummyClass.class)
public interface EasyPlaceUtilsInvoker {
    //#if >= 1.21.1
    @Invoker("cacheEasyPlacePosition")
    static void invokeCacheEasyPlacePosition(BlockPos pos) {}
    //#endif
}
