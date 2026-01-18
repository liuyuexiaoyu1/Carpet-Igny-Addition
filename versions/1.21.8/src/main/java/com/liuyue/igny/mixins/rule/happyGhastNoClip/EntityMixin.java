package com.liuyue.igny.mixins.rule.happyGhastNoClip;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin{

    @Inject(method = "getBoundingBox", at = @At("HEAD"), cancellable = true)
    private void HappyGhastNoBoundingBox(CallbackInfoReturnable<AABB> cir) {
       Entity self = (Entity)(Object)this;
       if(self instanceof HappyGhast&&self.isVehicle()&&IGNYSettings.happyGhastNoClip){
           cir.setReturnValue(new AABB(0, 0, 0, 0, 0, 0));
       }

   }

   @Inject(method = "tick", at = @At("HEAD"))
   private void setNoPhysics(CallbackInfo ci) {
       Entity self = (Entity)(Object)this;
       if(self instanceof HappyGhast){
               self.noPhysics = self.isVehicle()&&IGNYSettings.happyGhastNoClip;
       }
   }

   @Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
   private void onIsInWall(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity)(Object)this;
        if (self instanceof HappyGhast && self.isVehicle()&&IGNYSettings.happyGhastNoClip||(self instanceof Player && self.getRootVehicle() instanceof HappyGhast&&IGNYSettings.happyGhastNoClip)) {
            cir.setReturnValue(false);
        }
        if (self instanceof HappyGhast&& IGNYSettings.happyGhastNoClip) {
            if(((HappyGhastInvoker)self).invokeScanPlayerAboveGhast()){
                cir.setReturnValue(false);
            }
        }
   }

}