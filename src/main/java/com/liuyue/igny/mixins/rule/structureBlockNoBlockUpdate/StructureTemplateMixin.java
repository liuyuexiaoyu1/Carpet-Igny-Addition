package com.liuyue.igny.mixins.rule.structureBlockNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin {
    @Shadow
    public static List<StructureTemplate.StructureBlockInfo> processBlockInfos(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, BlockPos blockPos2, StructurePlaceSettings structurePlaceSettings, List<StructureTemplate.StructureBlockInfo> list) {
        return null;
    }

    @Inject(method = "placeInWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;processBlockInfos(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Ljava/util/List;)Ljava/util/List;"))
    private static void setBlock(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, BlockPos blockPos2, StructurePlaceSettings structurePlaceSettings, RandomSource randomSource, int i, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) List<StructureTemplate.StructureBlockInfo> list) {
        List<StructureTemplate.StructureBlockInfo> list1 = processBlockInfos(serverLevelAccessor, blockPos, blockPos2, structurePlaceSettings, list);
        if (IGNYSettings.structureBlockNoBlockUpdate && list1 != null) {
            for (StructureTemplate.StructureBlockInfo structureBlockInfo : list1) {
                IGNYSettings.noUpdatePos.add(structureBlockInfo.pos());
            }
        }
    }

    @Inject(method = "placeInWorld", at = @At(value = "RETURN"))
    private static void clearBlockList(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, BlockPos blockPos2, StructurePlaceSettings structurePlaceSettings, RandomSource randomSource, int i, CallbackInfoReturnable<Boolean> cir) {
        IGNYSettings.noUpdatePos.clear();
    }
}
