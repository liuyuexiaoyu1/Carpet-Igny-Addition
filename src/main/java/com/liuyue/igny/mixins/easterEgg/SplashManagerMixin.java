package com.liuyue.igny.mixins.easterEgg;

import com.liuyue.igny.manager.EasterEggDataManager;
import com.liuyue.igny.utils.FestivalUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#if >= 1.21.11
/*$$import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.network.chat.Component;$$*/
//#endif

import java.util.ArrayList;
import java.util.List;

@Mixin(SplashManager.class)
public class SplashManagerMixin {
    //#if >= 1.21.11
    /*$$@Shadow
    private static Component literalSplash(String par1) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }$$*/
    //#endif

    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/List;", at = @At(value = "RETURN"), cancellable = true)
    private void prepare(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<List<String>> cir) //#replace >= 1.21.11 ? private void prepare(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<List<Component>> cir)
    {
        if (EasterEggDataManager.INSTANCE.isSplashEnabled()) {
            List<String> extra = new ArrayList<>(); //#replace >= 1.21.11 ? List<Component> extra = new ArrayList<>();
            String currentLang = Minecraft.getInstance().getLanguageManager().getSelected();
            if (currentLang.contains("zh")) {
                extra.add("关注六月谢谢喵！！"); //#replace >= 1.21.11 ? extra.add(literalSplash("关注六月谢谢喵！！"));
            } else {
                extra.add("Follow Liuyue_awa!!"); //#replace >= 1.21.11 ? extra.add(Component.literal("Follow Liuyue_awa!!"));
            }
            if (cir.getReturnValue() != null) {
                try {
                    if (FestivalUtil.isAuthorsBirthday()) {
                        cir.getReturnValue().clear();
                        cir.getReturnValue().add("Happy birthday, Liuyue_awa!!!"); //#replace >= 1.21.11 ? cir.getReturnValue().add(literalSplash("Happy birthday, Liuyue_awa!!!"));
                    } else {
                        cir.getReturnValue().addAll(extra);
                    }
                } catch (UnsupportedOperationException ignored) {
                    List<String> arrayListTexts = new ArrayList<>(cir.getReturnValue()); //#replace >= 1.21.11 ? List<Component> arrayListTexts = new ArrayList<>(cir.getReturnValue());
                    if (FestivalUtil.isAuthorsBirthday()) {
                        arrayListTexts.clear();
                        arrayListTexts.add("Happy birthday, Liuyue_awa!!!"); //#replace >= 1.21.11 ? arrayListTexts.add(literalSplash("Happy birthday, Liuyue_awa!!!"));
                    }
                    arrayListTexts.addAll(extra);
                    cir.setReturnValue(arrayListTexts);
                }
            }
        }
    }
}
