package com.liuyue.igny.mixins.rule.instantSpawnEnderDragon;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import com.liuyue.igny.IGNYServerMod;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.dimension.end.DragonRespawnAnimation;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(EndDragonFight.class)
public abstract class EndDragonFightMixin {

    @Shadow
    protected abstract void spawnExitPortal(boolean bl);

    @Shadow
    protected abstract EnderDragon createNewDragon();

    @Shadow
    private java.util.List<net.minecraft.world.entity.boss.enderdragon.EndCrystal> respawnCrystals;

    @Shadow
    @Nullable
    private DragonRespawnAnimation respawnStage;

    @Shadow
    private boolean dragonKilled;

    @Shadow
    @Nullable
    private UUID dragonUUID;

    @Shadow
    @Final
    private ServerLevel level;

    @Shadow
    private int respawnTime;

    @Unique
    private java.util.List<net.minecraft.world.entity.boss.enderdragon.EndCrystal> getRespawnCrystals() {
        return this.respawnCrystals;
    }

    @Inject(method = "respawnDragon", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z",shift = At.Shift.AFTER), cancellable = true)
    private void onTryRespawn(List<EndCrystal> list , CallbackInfo ci) {
        if (IGNYSettings.instantSpawnEnderDragon) {
            EndDragonFight self = (EndDragonFight) (Object) this;
            if (!this.dragonKilled || this.respawnStage != null) {
                return;
            }
            this.respawnTime = 0;
            this.spawnExitPortal(false);
            this.respawnCrystals = list;
            ci.cancel();
            this.spawnExitPortal(false);
            java.util.List<net.minecraft.world.entity.boss.enderdragon.EndCrystal> crystals = getRespawnCrystals();
            if (crystals != null) {
                for (var crystal : crystals) {
                    crystal.setBeamTarget(null);
                    this.level.explode(crystal, crystal.getX(), crystal.getY(), crystal.getZ(), 6.0F, net.minecraft.world.level.Level.ExplosionInteraction.NONE);
                    crystal.discard();
                }
            }
            if (!getCarpetAMSAdditionSetting().equals("true")) {
                List<SpikeFeature.EndSpike> spikes = SpikeFeature.getSpikesForLevel(this.level);
                if (getCarpetAMSAdditionSetting().equals("keepEndCrystal")) {
                    for (SpikeFeature.EndSpike spike : spikes) {
                        EndCrystal crystal = getEndCrystal(spike);
                        this.level.addFreshEntity(crystal);
                    }
                } else if (getCarpetAMSAdditionSetting().equals("false")) {
                    RandomSource random = RandomSource.create();
                    for (SpikeFeature.EndSpike spike : spikes) {
                        for (BlockPos pos : BlockPos.betweenClosed(
                                new BlockPos(spike.getCenterX() - 10, spike.getHeight() - 10, spike.getCenterZ() - 10),
                                new BlockPos(spike.getCenterX() + 10, spike.getHeight() + 10, spike.getCenterZ() + 10)
                        )) {
                            this.level.removeBlock(pos, false);
                        }
                        SpikeConfiguration config = new SpikeConfiguration(true, List.of(spike), BlockPos.ZERO);
                        Feature.END_SPIKE.place(
                                config,
                                this.level,
                                this.level.getChunkSource().getGenerator(),
                                random,
                                new BlockPos(spike.getCenterX(), 45, spike.getCenterZ())
                        );
                    }
                }
            }
            EnderDragon dragon = this.createNewDragon();
            if (dragon != null) {
                this.dragonKilled = false;
                this.dragonUUID = dragon.getUUID();
            }
            self.resetSpikeCrystals();
        }
    }

    @Unique
    private @NotNull EndCrystal getEndCrystal(SpikeFeature.EndSpike spike) {
        BlockPos crystalPos = new BlockPos(spike.getCenterX(), spike.getHeight() + 1, spike.getCenterZ());
        EndCrystal crystal = new EndCrystal(
                this.level,
                crystalPos.getX() + 0.5,
                crystalPos.getY(),
                crystalPos.getZ() + 0.5
        );
        crystal.setShowBottom(false);
        crystal.setBeamTarget(new BlockPos(0, 128, 0));
        return crystal;
    }

    @Unique
    private static String getCarpetAMSAdditionSetting() {
        if(IGNYServerMod.CARPET_ADDITION_MOD_IDS.contains("carpet-ams-addition")){
            CarpetRule<?> carpetRule = CarpetServer.settingsManager.getCarpetRule("fakePlayerSpawnMemoryLeakFix");
            if (carpetRule == null) {
                return "false";
            }
            return carpetRule.value() == null ? "false" : carpetRule.value().toString();
        }
        return "false";
    }
}