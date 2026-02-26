package com.liuyue.igny.mixins.rule.dyedFrog;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//#if MC >= 12105
//$$ import net.minecraft.world.entity.animal.frog.FrogVariants;
//$$ import net.minecraft.core.registries.Registries;
//#endif
import net.minecraft.world.entity.animal.FrogVariant;
//#if MC > 12004
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
//#endif
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC >= 12106
//$$ import net.minecraft.world.level.storage.ValueInput;
//$$ import net.minecraft.world.level.storage.ValueOutput;
//#endif

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(Tadpole.class)
public class TadpoleMixin {
    @Unique private int greenDyeCount = 0;
    @Unique private int grayDyeCount = 0;
    @Unique private int orangeDyeCount = 0;
    @Unique private int slimeBallCount = 0;

    @WrapOperation(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/frog/Tadpole;isFood(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean isFood(Tadpole instance, ItemStack itemStack, Operation<Boolean> original) {
        if (IGNYSettings.dyedFrog) {
            return itemStack.is(Items.GREEN_DYE) || itemStack.is(Items.LIGHT_GRAY_DYE) || itemStack.is(Items.ORANGE_DYE) || original.call(instance, itemStack);
        }
        return original.call(instance, itemStack);
    }

    @Inject(method = "feed", at = @At(value = "HEAD"))
    private void feed(Player player, ItemStack itemStack, CallbackInfo ci) {
        switch (itemStack.getItem()) {
            case Item item when item == Items.GREEN_DYE -> greenDyeCount++;
            case Item item when item ==  Items.LIGHT_GRAY_DYE -> grayDyeCount++;
            case Item item when item == Items.ORANGE_DYE -> orangeDyeCount++;
            default -> slimeBallCount++;
        }
    }

    @WrapOperation(
            //#if MC >= 26.1
            //$$ method = "lambda$ageUp$0",
            //#elseif MC >= 12105
            //$$ method = "method_63651",
            //#else
            method = "ageUp()V",
            //#endif
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/frog/Frog;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;)Lnet/minecraft/world/entity/SpawnGroupData;"
            )
    )
    @SuppressWarnings("unchecked")
    private SpawnGroupData finalizeSpawn(Frog instance, ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, Operation<SpawnGroupData> original) {
        SpawnGroupData result = original.call(instance, serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
        //#if MC <= 12004
        //$$ List<Map.Entry<FrogVariant, Integer>> stats = new ArrayList<>();
        //$$ stats.add(new AbstractMap.SimpleEntry<>(FrogVariant.COLD, greenDyeCount));
        //$$ stats.add(new AbstractMap.SimpleEntry<>(FrogVariant.WARM, grayDyeCount));
        //$$ stats.add(new AbstractMap.SimpleEntry<>(FrogVariant.TEMPERATE, orangeDyeCount));
        //#else
        List<Map.Entry<ResourceKey<FrogVariant>, Integer>> stats = new ArrayList<>();
        //#if MC >= 12105
        //$$ stats.add(new AbstractMap.SimpleEntry<>(FrogVariants.COLD, greenDyeCount));
        //$$ stats.add(new AbstractMap.SimpleEntry<>(FrogVariants.WARM, grayDyeCount));
        //$$ stats.add(new AbstractMap.SimpleEntry<>(FrogVariants.TEMPERATE, orangeDyeCount));
        //#else
        stats.add(new AbstractMap.SimpleEntry<>(FrogVariant.COLD, greenDyeCount));
        stats.add(new AbstractMap.SimpleEntry<>(FrogVariant.WARM, grayDyeCount));
        stats.add(new AbstractMap.SimpleEntry<>(FrogVariant.TEMPERATE, orangeDyeCount));
        //#endif
        //#endif
        stats.add(new AbstractMap.SimpleEntry<>(null, slimeBallCount));
        stats.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        Map.Entry<?, Integer> winner = stats.getFirst();
        if (winner.getValue() > 0 && winner.getKey() != null) {
            //#if MC <= 12004
            //$$ instance.setVariant((FrogVariant) winner.getKey());
            //#else
            //#if MC >= 12103
            //#if MC >= 12105
            //$$ serverLevelAccessor.registryAccess().lookup(Registries.FROG_VARIANT).flatMap(registry -> registry.get((ResourceKey<FrogVariant>) winner.getKey())).ifPresent(holder -> instance.getEntityData().set(FrogAccessor.getVariantId(), holder));
            //#else
            //$$ instance.setVariant(BuiltInRegistries.FROG_VARIANT.getOrThrow((ResourceKey<FrogVariant>) winner.getKey()));
            //#endif
            //#else
            instance.setVariant(BuiltInRegistries.FROG_VARIANT.getHolderOrThrow((ResourceKey<FrogVariant>) winner.getKey()));
            //#endif
            //#endif
        }
        return result;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    //#if MC >= 12106
    //$$ private void saveCounts(ValueOutput nbt, CallbackInfo ci) {
    //#else
    private void saveCounts(CompoundTag nbt, CallbackInfo ci) {
        //#endif
        nbt.putInt("GreenDyeCount", greenDyeCount);
        nbt.putInt("GrayDyeCount", grayDyeCount);
        nbt.putInt("OrangeDyeCount", orangeDyeCount);
        nbt.putInt("SlimeBallCount", slimeBallCount);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    //#if MC >= 12106
    //$$ private void loadCounts(ValueInput nbt, CallbackInfo ci) {
    //#else
    private void loadCounts(CompoundTag nbt, CallbackInfo ci) {
        //#endif
        //#if MC >= 12105
        //$$ this.greenDyeCount = nbt.getInt("GreenDyeCount").orElse(0);
        //$$ this.grayDyeCount = nbt.getInt("GrayDyeCount").orElse(0);
        //$$ this.orangeDyeCount = nbt.getInt("OrangeDyeCount").orElse(0);
        //$$ this.slimeBallCount = nbt.getInt("SlimeBallCount").orElse(0);
        //#else
        this.greenDyeCount = nbt.getInt("GreenDyeCount");
        this.grayDyeCount = nbt.getInt("GrayDyeCount");
        this.orangeDyeCount = nbt.getInt("OrangeDyeCount");
        this.slimeBallCount = nbt.getInt("SlimeBallCount");
        //#endif
    }
}