package com.liuyue.igny.helper;

import com.liuyue.igny.IGNYServerMod;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.mixins.rule.survivalFlyNoClip.compat.tis.CreativeNoClipHelperInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
    public static boolean isNoClipping(Player player) {
        if (isActiveFlyingPlayer(player)) {
            return true;
        }
        //#if >= 1.21.6
        /*$$if (isActiveRider(player)) {
            return true;
        }$$*/
        //#endif
        return IGNYServerMod.TIS && CreativeNoClipHelperInvoker.igny$isNoClipPlayer(player);
    }

    public static boolean isEyeInsideBlock(Player player, Level level) {
        Vec3 eye = player.getEyePosition();
        BlockPos pos = BlockPos.containing(eye);
        BlockState state = level.getBlockState(pos);

        if (state.isAir()) {
            return false;
        }

        var shape = state.getCollisionShape(level, pos);

        return !shape.isEmpty() && shape.bounds().move(pos).contains(eye);
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
