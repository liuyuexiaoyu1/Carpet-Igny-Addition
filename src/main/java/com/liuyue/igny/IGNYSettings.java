package com.liuyue.igny;


import carpet.api.settings.Rule;

import static com.liuyue.igny.utils.IGNYRuleCategory.*;


public class IGNYSettings
{
    //假玩家生成内存泄露修复
    public static boolean fakePlayerSpawnMemoryLeakFix = false;

    @Rule(
            categories = {IGNY,SURVIVAL,FEATURE}
    )
    public static boolean wardenNeverDig = false;

    @Rule(
            categories = {IGNY,SURVIVAL,FEATURE}
    )
    public static boolean playerLevitationFreeShulkerBullet = false;

    @Rule(
            categories = {IGNY,SURVIVAL,FEATURE}
    )
    public static boolean playerMiningFatigueFreeGuardian = false;

    @Rule(
            categories = {IGNY,COMMAND,FEATURE},
            options = {"true", "false"}
    )
    public static boolean showRuleChangeHistory = false;

    @Rule(
            categories = {IGNY,SURVIVAL,FEATURE},
            options = {"true", "false"}
    )
    public static boolean fakePlayerCanPush = true;

    @Rule(
            categories = {IGNY,SURVIVAL,FEATURE},
            options = {"true", "false"}
    )
    public static Boolean wetSpongeCanAbsorbLava = false;

    @Rule(
            categories = {IGNY, COMMAND, FEATURE},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandPlayerEnderChestDrop = "ops";

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean noWardenDarkness = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean floatingIceWater = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean noZombifiedPiglinNetherPortalSpawn = false;

    //#if MC >= 12102
    //$$ @Rule(
    //$$        categories = {IGNY,FEATURE},
    //$$        options = {"false", "true"}
    //$$ )
    //$$ public static Boolean projectileDuplicationReintroduced = false;
    //#endif

    //#if MC >= 12104
    //$$ @Rule(
    //$$        categories = {IGNY,FEATURE},
    //$$        options = {"false", "true"}
    //$$ )
    //$$ public static Boolean skeletonsPickupSwordsReintroduced = false;
    //#endif

    //#if MC >= 12102
    //$$ @Rule(
    //$$        categories = {IGNY,FEATURE},
    //$$        options = {"false", "true"}
    //$$ )
    //$$ public static Boolean minecartMotionFix = false;
    //#endif

    //#if MC < 12109
    @Rule(
            categories = {IGNY,FEATURE},
            options = {"false", "true"}
    )
    public static Boolean tntMinecartEmptyDamageSourceFix = false;
    //#endif

    //#if MC < 12111
    @Rule(
            categories = {IGNY,FEATURE,BUGFIX},
            options = {"false", "true"}
    )
    public static Boolean fakePlayerBoatYawFix = false;
    //#endif

    @Rule(
            categories = {IGNY,FEATURE},
            options = {"false", "cantrade", "true"}
    )
    public static String killFakePlayerRemoveVehicle = "true";

    @Rule(
            categories = {IGNY,FEATURE},
            options = {"false", "true"}
    )
    public static Boolean candlePlaceOnIncompleteBlock = false;

    @Rule(
            categories = {IGNY, COMMAND, CREATIVE, FEATURE},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandFixnotepitch = "ops";

    @Rule(
            categories = {IGNY, COMMAND, CREATIVE},
            options = {"false", "true"}
    )
    public static Boolean fixnotepitchUpdateBlock = false;

    //#if MC >= 12106
    //$$ @Rule(
    //$$        categories = {IGNY, SURVIVAL, CLIENT, FEATURE},
    //$$        options = {"false", "true"}
    //$$ )
    //$$ public static Boolean happyGhastNoClip = false;
    //#endif

    @Rule(
            categories = {IGNY, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean noWitherEffect = false;

    //#if MC>=12106
    //$$ @Rule(
    //$$        categories = {IGNY, FEATURE},
    //$$        options = {"false", "true"}
    //$$ )
    //$$ public static Boolean locatorBarNoFakePlayer = false;
    //#endif

    @Rule(
            categories = {IGNY, COMMAND, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean fakePlayerLoginLogoutNoChatInfo = false;

    @Rule(
            categories = {IGNY, COMMAND, FEATURE},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandPlayerOperate = "ops";

    @Rule(
            categories = {IGNY, COMMAND, FEATURE},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandClearLightQueue = "ops";

    @Rule(
            categories = {IGNY, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean fakePlayerNoBreakingCoolDown = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean creativeDestroyWaterloggedBlockNoWater = false;

    //#if MC >= 12005
    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Integer trialSpawnerCoolDown = 36000;
    //#endif

    @Rule(
            categories = {IGNY, OPTIMIZATION, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean optimizedPiglin = false;

    @Rule(
            categories = {IGNY,OPTIMIZATION,FEATURE}
    )
    public static Integer optimizedPiglinLimit = 100;

    @Rule(
            categories = {IGNY, OPTIMIZATION, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean optimizedWarden = false;

    @Rule(
            categories = {IGNY,OPTIMIZATION,FEATURE}
    )
    public static Integer optimizedWardenLimit = 100;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"0", "1", "2", "5", "10"}
    )
    public static int realPlayerBreakLimitPerTick = 0;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"0", "1", "2", "5", "10"}
    )
    public static int realPlayerPlaceLimitPerTick = 0;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"0", "5", "10", "20", "50"}
    )
    public static int fakePlayerBreakLimitPerTick = 0;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"0", "5", "10", "20", "50"}
    )
    public static int fakePlayerPlaceLimitPerTick = 0;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"true", "false"}
    )
    public static boolean playerOperationLimiter = false;

    @Rule(
            categories = {IGNY, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean generateNetherPortal = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static Integer placeComposterCompost = 0;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Integer enderDragonDeathRiseLimit = -1145;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Integer enderDragonDeathDropExp = -1;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static Boolean instantSpawnEnderDragon = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static Integer maxEndPortalSize = -1;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean allowRectangularEndPortal = false;

    //#if MC >= 12005
    @Rule(
            categories = {IGNY, CREATIVE, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean instantVaultSpawnLoot = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static int trialSpawnerLootMultiplier = 1;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static int trialSpawnerDropKeyProbability = -1;

    @Rule(
            categories = {IGNY, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean instantTrialSpawnerSpawnLoot = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE},
            options = {"false", "true"},
            strict = false
    )
    public static String simpleSoundSuppression = "false";
    //#endif

    @Rule(
            categories = {IGNY, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean safeSoundSuppression = false;

    @Rule(
            categories = {IGNY, COMMAND, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean twoChangedRuleValueSetDefault = false;
}
