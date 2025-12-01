package com.liuyue.igny;


import carpet.api.settings.Rule;

import static com.liuyue.igny.utils.IGNYRuleCategory.*;


public class IGNYSettings
{
    @Rule(
            categories = {IGNY,SURVIVAL,FEATURE}
    )
    public static boolean WardenNeverDig = false;

    @Rule(
            categories = {IGNY,SURVIVAL,FEATURE}
    )
    public static boolean PlayerLevitationFreeShulkerBullet = false;

    @Rule(
            categories = {IGNY,SURVIVAL,FEATURE}
    )
    public static boolean PlayerMiningFatigueFreeGuardian = false;

    @Rule(
            categories = {IGNY,FEATURE},
            options = {"true", "false"}
    )
    public static boolean ShowRuleChangeHistory = false;

    @Rule(
            categories = {IGNY,SURVIVAL,FEATURE},
            options = {"true", "false"}
    )
    public static boolean FakePlayerCanPush = true;

    @Rule(
            categories = {IGNY,SURVIVAL,FEATURE},
            options = {"true", "false"}
    )
    public static Boolean WetSpongeCanAbsorbLava = false;

    @Rule(
            categories = {IGNY, COMMAND, FEATURE},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"}
    )
    public static String CommandPlayerEnderChestDrop = "ops";

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean NoWardenDarkness = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean FloatingIceWater = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean NoZombifiedPiglinNetherPortalSpawn = false;

    //#if MC >= 12102
    //$$ @Rule(
    //$$        categories = {IGNY,FEATURE},
    //$$        options = {"false", "true"}
    //$$ )
    //$$ public static Boolean ProjectileDuplicationReintroduced = false;
    //#endif

    //#if MC >= 12104
    //$$ @Rule(
    //$$        categories = {IGNY,FEATURE},
    //$$        options = {"false", "true"}
    //$$ )
    //$$ public static Boolean SkeletonsPickupSwordsReintroduced = false;
    //#endif

    //#if MC >= 12102
    //$$ @Rule(
    //$$        categories = {IGNY,FEATURE},
    //$$        options = {"false", "true"}
    //$$ )
    //$$ public static Boolean MinecartMotionFix = false;
    //#endif

    //#if MC < 12109
    @Rule(
            categories = {IGNY,FEATURE},
            options = {"false", "true"}
    )
    public static Boolean TntMinecartEmptyDamageSourceFix = false;
    //#endif

    @Rule(
            categories = {IGNY,FEATURE,BUGFIX},
            options = {"false", "true"}
    )
    public static Boolean FakePlayerBoatYawFix = false;

    @Rule(
            categories = {IGNY,FEATURE},
            options = {"false", "cantrade", "true"}
    )
    public static String KillFakePlayerRemoveVehicle = "true";

    @Rule(
            categories = {IGNY,FEATURE},
            options = {"false", "true"}
    )
    public static Boolean CandlePlaceOnIncompleteBlock = false;

    @Rule(
            categories = {IGNY, COMMAND, CREATIVE, FEATURE},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"}
    )
    public static String CommandFixnotepitch = "ops";

    @Rule(
            categories = {IGNY, COMMAND, CREATIVE, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean FixnotepitchUpdateBlock = false;

    //#if MC >= 12106
    //$$ @Rule(
    //$$        categories = {IGNY,SURVIVAL,FEATURE},
    //$$        options = {"false", "true"}
    //$$ )
    //$$ public static Boolean HappyGhastNoClip = false;
    //#endif

    @Rule(
            categories = {IGNY, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean OnlyPlayerCreateNetherPortal = false;
}
