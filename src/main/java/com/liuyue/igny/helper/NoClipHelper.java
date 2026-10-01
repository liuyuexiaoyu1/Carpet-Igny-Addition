package com.liuyue.igny.helper;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
//#if >= 1.21.11
//$$ import net.minecraft.world.entity.animal.happyghast.HappyGhast;
//#elseif >= 1.21.6
//$$ import net.minecraft.world.entity.animal.HappyGhast;
//#endif

public final class NoClipHelper {

    public static boolean isActiveFlyingPlayer(Entity entity) {
        return IGNYSettings.SURVIVAL_FLY_NO_CLIP.value()
                && entity instanceof Player player
                && !player.isCreative()
                && player.getAbilities().mayfly
                && player.getAbilities().flying;
    }

    //#if >= 1.21.6
    /*$$public static boolean isHappyGhastNoClip() {
        return IGNYSettings.HAPPY_GHAST_NO_CLIP.value();
    }

    public static boolean isActiveGhast(Entity entity) {
        return isHappyGhastNoClip() && entity instanceof HappyGhast;
    }

    public static boolean isActiveGhastWithRider(Entity entity) {
        return isActiveGhast(entity) && entity.isVehicle();
    }

    public static boolean isActiveRider(Entity entity) {
        return isHappyGhastNoClip() && entity instanceof Player player && player.getRootVehicle() instanceof HappyGhast;
    }

    public static boolean isActiveGhastOrRider(Entity entity) {
        return isActiveGhast(entity) || isActiveRider(entity);
    }$$*/
    //#endif
}
