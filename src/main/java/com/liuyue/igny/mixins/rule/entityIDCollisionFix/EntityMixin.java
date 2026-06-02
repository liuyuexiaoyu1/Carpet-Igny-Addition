package com.liuyue.igny.mixins.rule.entityIDCollisionFix;

//#if MC >= 26.2
//$$ import com.liuyue.igny.utils.compat.DummyClass;
//#else
import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.concurrent.atomic.AtomicInteger;
//#endif
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 26.2
//$$ @Mixin(DummyClass.class)
//#else
@Mixin(Entity.class)
//#endif
public class EntityMixin {
    //#if MC < 26.2
    @Shadow
    private int id;

    @Shadow
    @Final
    private static AtomicInteger ENTITY_COUNTER;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;getDimensions()Lnet/minecraft/world/entity/EntityDimensions;"))
    private void init(EntityType<?> entityType, Level level, CallbackInfo ci) {
        if (IGNYSettings.ENTITY_ID_COLLISION_FIX.value() && !level.isClientSide()) {
            this.id = getNextEntityId((ServerLevel) level);
        }
    }

    @Unique
    private int getNextEntityId(ServerLevel level) {
        int id = 0;

        while (id == 0 || ((ChunkMapAccessor) level.getChunkSource().chunkMap).getEntityMap().containsKey(id)) {
            id = ENTITY_COUNTER.incrementAndGet();
        }

        return id;
    }
    //#endif
}
