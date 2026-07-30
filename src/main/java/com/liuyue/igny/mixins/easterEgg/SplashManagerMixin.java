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
//#if MC >= 12111
//$$ import net.minecraft.network.chat.Component;
//#endif

import java.util.ArrayList;
import java.util.List;

@Mixin(SplashManager.class)
public class SplashManagerMixin {
    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/List;", at = @At(value = "RETURN"), cancellable = true)
    //#if MC >= 12111
    //$$ private void prepare(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<List<Component>> cir)
    //#else
    private void prepare(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<List<String>> cir)
            //#endif
    {
        if (EasterEggDataManager.INSTANCE.isSplashEnabled()) {
            //#if MC >= 12111
            //$$ List<Component> extra = new ArrayList<>();
            //#else
            List<String> extra = new ArrayList<>();
            //#endif
            String currentLang = Minecraft.getInstance().getLanguageManager().getSelected();
            if (currentLang.contains("zh")) {
                //#if MC >= 12111
                //$$ extra.add(Component.literal("关注六月谢谢喵！！"));
                //#else
                extra.add("关注六月谢谢喵！！");
                //#endif
            } else {
                //#if MC >= 12111
                //$$ extra.add(Component.literal("Follow Liuyue_awa!!"));
                //#else
                extra.add("Follow Liuyue_awa!!");
                //#endif
            }
            if (cir.getReturnValue() != null) {
                try {
                    if (FestivalUtil.isAuthorsBirthday()) {
                        cir.getReturnValue().clear();
                        //#if MC >= 12111
                        //$$ cir.getReturnValue().add(Component.literal("Happy birthday, Liuyue_awa!!!"));
                        //#else
                        cir.getReturnValue().add("Happy birthday, Liuyue_awa!!!");
                        //#endif
                    } else {
                        cir.getReturnValue().addAll(extra);
                    }
                } catch (UnsupportedOperationException ignored) {
                    //#if MC >= 12111
                    //$$ List<Component> arrayListTexts = new ArrayList<>(cir.getReturnValue());
                    //#else
                    List<String> arrayListTexts = new ArrayList<>(cir.getReturnValue());
                    //#endif
                    if (FestivalUtil.isAuthorsBirthday()) {
                        arrayListTexts.clear();
                        //#if MC >= 12111
                        //$$ arrayListTexts.add(Component.literal("Happy birthday, Liuyue_awa!!!"));
                        //#else
                        arrayListTexts.add("Happy birthday, Liuyue_awa!!!");
                        //#endif
                    }
                    arrayListTexts.addAll(extra);
                    cir.setReturnValue(arrayListTexts);
                }
            }
        }
    }
}
