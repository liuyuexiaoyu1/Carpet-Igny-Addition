package com.liuyue.igny.helper.happyGhastNoClip;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.player.Player;

public final class NoClipHelper {

    public static boolean isActive() {
        return IGNYSettings.HAPPY_GHAST_NO_CLIP.value();
    }

    public static boolean isActiveGhast(Entity entity) {
        return isActive() && entity instanceof HappyGhast;
    }

    public static boolean isActiveGhastWithRider(Entity entity) {
        return isActiveGhast(entity) && entity.isVehicle();
    }

    public static boolean isActiveRider(Entity entity) {
        return isActive() && entity instanceof Player player && player.getRootVehicle() instanceof HappyGhast;
    }

    public static boolean isActiveGhastOrRider(Entity entity) {
        return isActiveGhast(entity) || isActiveRider(entity);
    }
}
