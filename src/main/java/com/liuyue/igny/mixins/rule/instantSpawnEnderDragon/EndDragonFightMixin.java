package com.liuyue.igny.mixins.rule.instantSpawnEnderDragon;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.RuleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
//#if MC >= 26.1
//$$ import net.minecraft.world.level.dimension.end.DragonRespawnStage;
//$$ import net.minecraft.world.level.dimension.end.EnderDragonFight;
//$$ import net.minecraft.world.level.levelgen.feature.EndSpikeFeature;
//$$ import net.minecraft.world.level.levelgen.feature.configurations.EndSpikeConfiguration;
//#else
import net.minecraft.world.level.dimension.end.DragonRespawnAnimation;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
//#endif
import net.minecraft.world.level.levelgen.feature.Feature;
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

//#if MC >= 26.1
//$$ @Mixin(value = EnderDragonFight.class, priority = 1100)
//#else
@Mixin(value = EndDragonFight.class, priority = 1100)
//#endif
public abstract class EndDragonFightMixin {

    @Shadow
    protected abstract void spawnExitPortal(boolean bl);

    @Shadow
    protected abstract EnderDragon createNewDragon();

    @Shadow
    private List<EndCrystal> respawnCrystals;

    @Shadow
    @Nullable
    //#if MC >= 26.1
    //$$ private DragonRespawnStage respawnStage;
    //#else
    private DragonRespawnAnimation respawnStage;
    //#endif

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

    @Shadow
    @Nullable
    protected abstract BlockPattern.BlockPatternMatch findExitPortal();

    @Shadow
    @Final
    private BlockPattern exitPortalPattern;

    @Unique
    private List<EndCrystal> getRespawnCrystals() {
        return this.respawnCrystals;
    }

    @Inject(method = "respawnDragon", at = @At(value = "HEAD"), cancellable = true)
    private void respawnDragon(List<EndCrystal> list , CallbackInfo ci) {
        if (IGNYSettings.instantSpawnEnderDragon) {
            //#if MC >= 26.1
            //$$ EnderDragonFight self = (EnderDragonFight) (Object) this;
            //#else
            EndDragonFight self = (EndDragonFight) (Object) this;
            //#endif
            if (!this.dragonKilled || this.respawnStage != null) {
                return;
            }
            for(BlockPattern.BlockPatternMatch blockPatternMatch = this.findExitPortal(); blockPatternMatch != null; blockPatternMatch = this.findExitPortal()) {
                for(int i = 0; i < this.exitPortalPattern.getWidth(); ++i) {
                    for(int j = 0; j < this.exitPortalPattern.getHeight(); ++j) {
                        for(int k = 0; k < this.exitPortalPattern.getDepth(); ++k) {
                            BlockInWorld blockInWorld = blockPatternMatch.getBlock(i, j, k);
                            if (blockInWorld.getState().is(Blocks.BEDROCK) || blockInWorld.getState().is(Blocks.END_PORTAL)) {
                                this.level.setBlockAndUpdate(blockInWorld.getPos(), Blocks.END_STONE.defaultBlockState());
                            }
                        }
                    }
                }
            }
            this.respawnTime = 0;
            this.spawnExitPortal(false);
            this.respawnCrystals = list;
            ci.cancel();
            List<EndCrystal> crystals = getRespawnCrystals();
            if (crystals != null) {
                for (var crystal : crystals) {
                    crystal.setBeamTarget(null);
                    this.level.explode(crystal, crystal.getX(), crystal.getY(), crystal.getZ(), 6.0F, net.minecraft.world.level.Level.ExplosionInteraction.NONE);
                    crystal.discard();
                }
            }
            if (!RuleUtil.getCarpetRulesValue("carpet-ams-addition", "preventEndSpikeRespawn").equals("true")) {
                //#if MC >= 26.1
                //$$ List<EndSpikeFeature.EndSpike> spikes = EndSpikeFeature.getSpikesForLevel(this.level);
                //#else
                List<SpikeFeature.EndSpike> spikes = SpikeFeature.getSpikesForLevel(this.level);
                //#endif
                if (RuleUtil.getCarpetRulesValue("carpet-ams-addition", "preventEndSpikeRespawn").equals("keepEndCrystal")) {
                    //#if MC >= 26.1
                    //$$ for (EndSpikeFeature.EndSpike spike : spikes) {
                    //#else
                    for (SpikeFeature.EndSpike spike : spikes) {
                        //#endif
                        EndCrystal crystal = getEndCrystal(spike);
                        this.level.addFreshEntity(crystal);
                    }
                } else if (RuleUtil.getCarpetRulesValue("carpet-ams-addition", "preventEndSpikeRespawn").equals("false")) {
                    RandomSource random = RandomSource.create();
                    //#if MC >= 26.1
                    //$$ for (EndSpikeFeature.EndSpike spike : spikes) {
                    //#else
                    for (SpikeFeature.EndSpike spike : spikes) {
                        //#endif
                        for (BlockPos pos : BlockPos.betweenClosed(
                                new BlockPos(spike.getCenterX() - 10, spike.getHeight() - 10, spike.getCenterZ() - 10),
                                new BlockPos(spike.getCenterX() + 10, spike.getHeight() + 10, spike.getCenterZ() + 10)
                        )) {
                            this.level.removeBlock(pos, false);
                        }
                        //#if MC >= 26.1
                        //$$ EndSpikeConfiguration config = new EndSpikeConfiguration(true, List.of(spike), BlockPos.ZERO);
                        //#else
                        SpikeConfiguration config = new SpikeConfiguration(true, List.of(spike), BlockPos.ZERO);
                        //#endif
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
    //#if MC >= 26.1
    //$$ private @NotNull EndCrystal getEndCrystal(EndSpikeFeature.EndSpike spike) {
    //#else
    private @NotNull EndCrystal getEndCrystal(SpikeFeature.EndSpike spike) {
        //#endif
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
}