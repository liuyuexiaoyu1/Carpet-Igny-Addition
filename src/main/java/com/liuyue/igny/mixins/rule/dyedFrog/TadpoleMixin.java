package com.liuyue.igny.mixins.rule.dyedFrog;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.FrogVariant;
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
            method = "ageUp()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/frog/Frog;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;)Lnet/minecraft/world/entity/SpawnGroupData;"
            )
    )
    private SpawnGroupData finalizeSpawn(Frog instance, ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, Operation<SpawnGroupData> original) {
        SpawnGroupData result = original.call(instance, serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
        List<Map.Entry<ResourceKey<FrogVariant>, Integer>> stats = new ArrayList<>();
        stats.add(new AbstractMap.SimpleEntry<>(FrogVariant.COLD, greenDyeCount));
        stats.add(new AbstractMap.SimpleEntry<>(FrogVariant.WARM, grayDyeCount));
        stats.add(new AbstractMap.SimpleEntry<>(FrogVariant.TEMPERATE, orangeDyeCount));
        stats.add(new AbstractMap.SimpleEntry<>(null, slimeBallCount));
        stats.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        Map.Entry<ResourceKey<FrogVariant>, Integer> winner = stats.getFirst();
        if (winner.getValue() > 0 && winner.getKey() != null) {
            instance.setVariant(BuiltInRegistries.FROG_VARIANT.getHolderOrThrow(winner.getKey()));
        }
        return result;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveCounts(CompoundTag nbt, CallbackInfo ci) {
        nbt.putInt("GreenDyeCount", greenDyeCount);
        nbt.putInt("GrayDyeCount", grayDyeCount);
        nbt.putInt("OrangeDyeCount", orangeDyeCount);
        nbt.putInt("SlimeBallCount", slimeBallCount);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadCounts(CompoundTag nbt, CallbackInfo ci) {
        this.greenDyeCount = nbt.getInt("GreenDyeCount");
        this.grayDyeCount = nbt.getInt("GrayDyeCount");
        this.orangeDyeCount = nbt.getInt("OrangeDyeCount");
        this.slimeBallCount = nbt.getInt("SlimeBallCount");
    }
}