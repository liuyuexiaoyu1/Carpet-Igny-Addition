package com.liuyue.igny;

import carpet.api.settings.Rule;
import com.liuyue.igny.rule.annotation.ObservedRule;
import com.liuyue.igny.rule.callback.*;
import com.liuyue.igny.rule.validators.*;
import net.minecraft.core.BlockPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.liuyue.igny.utils.IGNYRuleCategory.*;

public class IGNYSettings {
    public static final ThreadLocal<Boolean> CREATIVE_BREAKING = ThreadLocal.withInitial(() -> false);
    public static Set<String> CRAMMING_ENTITIES = new HashSet<>();
    public static List<BlockPos> noUpdatePos = new ArrayList<>();
    public static final Map<String, List<String>> MOD_RULE_TREE = new ConcurrentHashMap<>();
    //假玩家生成内存泄露修复
    public static final ThreadLocal<Boolean> fakePlayerSpawnMemoryLeakFix = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<Boolean> itemStackCountChanged = ThreadLocal.withInitial(() -> true);
    public static float originalTPS = 20.0f;
    public static final Set<UUID> sprintWhitelistPlayers = new HashSet<>();
    public static Set<String> EIDWhitelist = new HashSet<>();
    public static final ThreadLocal<Boolean> effectCommandRegistering = ThreadLocal.withInitial(() -> false);
    public static ThreadLocal<Boolean> movingBlocks = ThreadLocal.withInitial(() -> false);

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean wardenNeverDig = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean playerLevitationFreeShulkerBullet = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean playerMiningFatigueFreeGuardian = false;

    @Rule(
            categories = {IGNY, COMMAND, FEATURE}
    )
    public static boolean showRuleChangeHistory = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean fakePlayerCanPush = true;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean wetSpongeCanAbsorbLava = false;

    @Rule(
            categories = {IGNY, COMMAND, FEATURE},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandPlayerEnderChestDrop = "ops";

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean noWardenDarkness = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean floatingIceWater = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean noZombifiedPiglinNetherPortalSpawn = false;

    //#if MC >= 12102
    //$$ @Rule(
    //$$        categories = {IGNY, FEATURE, PORTING}
    //$$ )
    //$$ public static boolean projectileDuplicationReintroduced = false;
    //#endif

    //#if MC >= 12104
    //$$ @Rule(
    //$$        categories = {IGNY, FEATURE, PORTING}
    //$$ )
    //$$ public static boolean skeletonsPickupSwordsReintroduced = false;
    //#endif

    //#if MC >= 12102
    //$$ @Rule(
    //$$        categories = {IGNY, FEATURE, PORTING}
    //$$ )
    //$$ public static boolean teleportInheritMinecartsMotionReintroduced = false;
    //#endif

    //#if MC < 12109
    @Rule(
            categories = {IGNY,FEATURE}
    )
    public static boolean tntMinecartEmptyDamageSourceFix = false;
    //#endif

    //#if MC < 12111
    @Rule(
            categories = {IGNY,FEATURE,BUGFIX}
    )
    public static boolean fakePlayerBoatYawFix = false;
    //#endif

    @Rule(
            categories = {IGNY,FEATURE},
            options = {"false", "canBoatTrade", "true"}
    )
    public static String killFakePlayerRemoveVehicle = "true";

    @Rule(
            categories = {IGNY,FEATURE}
    )
    public static boolean candlePlaceOnIncompleteBlock = false;

    @Rule(
            categories = {IGNY, COMMAND, CREATIVE, FEATURE},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandFixnotepitch = "ops";

    @Rule(
            categories = {IGNY, COMMAND, CREATIVE}
    )
    public static boolean fixnotepitchUpdateBlock = false;

    //#if MC >= 12106
    //$$ @Rule(
    //$$        categories = {IGNY, SURVIVAL, CLIENT, FEATURE}
    //$$ )
    //$$ public static boolean happyGhastNoClip = false;
    //#endif

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean noWitherEffect = false;

    //#if MC>=12106
    //$$ @Rule(
    //$$        categories = {IGNY, FEATURE}
    //$$ )
    //$$ @ObservedRule(LocatorBarCallback.class)
    //$$ public static boolean locatorBarNoFakePlayer = false;
    //#endif

    @Rule(
            categories = {IGNY, COMMAND, FEATURE}
    )
    public static boolean fakePlayerLoginLogoutNoChatInfo = false;

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
            categories = {IGNY, FEATURE}
    )
    public static boolean fakePlayerNoBreakingCoolDown = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static boolean creativeDestroyWaterloggedBlockNoWater = false;

    //#if MC >= 12005
    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static int trialSpawnerCoolDown = 36000;
    //#endif

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"0", "1", "2", "5", "10"},
            strict = false
    )
    public static int realPlayerBreakLimitPerTick = 0;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"0", "1", "2", "5", "10"},
            strict = false
    )
    public static int realPlayerPlaceLimitPerTick = 0;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"0", "5", "10", "20", "50"},
            strict = false
    )
    public static int fakePlayerBreakLimitPerTick = 0;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"0", "5", "10", "20", "50"},
            strict = false
    )
    public static int fakePlayerPlaceLimitPerTick = 0;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean playerOperationLimiter = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean generateNetherPortal = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static int placeComposterCompost = 0;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static int enderDragonDeathRiseLimit = -1145;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static int enderDragonDeathDropExp = -1;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static boolean instantSpawnEnderDragon = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static int maxEndPortalSize = -1;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static boolean allowRectangularEndPortal = false;

    //#if MC >= 12005
    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static boolean instantVaultSpawnLoot = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static int trialSpawnerLootMultiplier = 1;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static int trialSpawnerDropKeyProbability = -1;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean instantTrialSpawnerSpawnLoot = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE},
            options = {"false", "true"},
            strict = false
    )
    public static String simpleSoundSuppression = "false";
    //#endif

    //#if MC >= 12000
    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean safeSoundSuppression = false;
    //#endif

    @Rule(
            categories = {IGNY, COMMAND, FEATURE}
    )
    public static boolean twoChangedRuleValueSetDefault = false;

    @Rule(
            categories = {IGNY, OPTIMIZATION, FEATURE},
            options = {"#none", "minecraft:warden", "minecraft:piglin", "minecraft:warden,minecraft:piglin"},
            validators = EntityValidator.class,
            strict = false
    )
    public static String optimizedEntityList = "#none";

    @Rule(
            categories = {IGNY,OPTIMIZATION,FEATURE}
    )
    public static int optimizedEntityLimit = 100;

    @Rule(
            categories = {IGNY, BUGFIX}
    )
    public static boolean optimizedTNTErrorScopeFix = false;

    @Rule(
            categories = {IGNY, COMMAND, CREATIVE, FEATURE},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandCustomPlayerPickupItem = "false";

    //#if MC >= 12006
    @Rule(
            categories = {IGNY, COMMAND, CREATIVE, FEATURE},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandCustomItemMaxStackSize = "false";
    //#endif

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean playerHungryValueNoDecrease = false;

    @Rule(
            categories = {IGNY, SURVIVAL, CLIENT, FEATURE}
    )
    public static boolean playerLowHungryValueCanSprint = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean instantFrogEat = false;

    //#if MC >= 12111
    //$$ @Rule(
    //$$        categories = {IGNY, FEATURE}
    //$$ )
    //$$ public static boolean allowInvalidMotion = false;
    //#endif

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean accelerateBabyVillagerGrowth = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean lightningBoltNoFire = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean dispenserTrade = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean dispenserTradeFailDisperseItem = true;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean renewableCalcite = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean pureShulkerBoxDispense = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static boolean structureBlockNoBlockUpdate = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean spawnMaxCountIgnoresChunkOverlap = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean prioritizeFlyingUseItem = false;

    @Rule(
            categories = {IGNY, OPTIMIZATION}
    )
    public static boolean optimizedSpawning = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean dyedFrog = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean betterLoyaltyTrident = false;

    @Rule(
            categories = {IGNY},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"},
            validators = SyncmaticaValidator.class
    )
    public static String removeSyncmaticaPermission = "true";

    @Rule(
            categories = {IGNY}
    )
    public static boolean disableWatchDog = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean podzolSpread = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean noOwnerTntLootingIII = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean globalDaylightDetector = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static boolean noteBlockSelfCheck = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static boolean noCreativeDestroyAttachmentDrops = false;

    @Rule(
            categories = {IGNY}
    )
    public static boolean showRuleSource = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean theEndCanCreateNetherPortal = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean renewableEndGatewayPortal = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean endGatewayPortalNoCooldown = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean liquidSourceCanDestroy = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"false", "true", "playerJoin"}
    )
    @ObservedRule(GameTickCallback.class)
    public static String betterSprintGameTick = "false";

    //#if MC >= 26.1
    //$$ @Rule(
    //$$         categories = {IGNY, FEATURE, PORTING}
    //$$ )
    //$$ public static boolean tripwireHookDupeReintroduced = false;
    //#endif

    @Rule(
            categories = {IGNY}
    )
    public static boolean showClassMixinList = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean piglinNeutralBehavior = false;

    @Rule(
            categories = {IGNY, BUGFIX}
    )
    public static boolean fakePlayerMemoryLeakFix = false;

    @Rule(
            categories = {IGNY, FEATURE},
            options = {"false", "true", "liquid_source"}
    )
    public static String liquidNeverSpread = "false";

    @Rule(
            categories = {IGNY, FEATURE}
    )
    @ObservedRule(EffectLevelCallback.class)
    public static boolean superEffectLevel = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE},
            options = {"false", "true"},
            strict = false
    )
    public static String simpleEntityIDSuppression = "false";

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE},
            options = {"#all", "#none", "minecraft:item"},
            strict = false,
            validators = EntityValidator.class
    )
    public static String entityIDSuppressionWhitelist = "#all";

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean shulkerBoxInShulkerBox = false;

    //#if MC >= 26.2
    //$$ @Rule(
    //$$         categories = {IGNY, FEATURE, PORTING}
    //$$ )
    //$$ public static boolean comparatorDupeReintroduced = false;
    //#endif
    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean festiveEasterEgg = true;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"false", "nonFluid", "true"}
    )
    public static String drillAnvil = "false";

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean transparentNightmarishBlock = false;

    //#if MC >= 12102
    //$$ @Rule(
    //$$         categories = {IGNY, BUGFIX}
    //$$ )
    //$$ public static boolean ghostEnderPearlFix = false;
    //#endif

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static boolean betterFlippinCactus = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean blockableAmethyst = false;

    @Rule(
            categories = {IGNY, FEATURE},
            options = {"false", "minimum", "average", "maximum"}
    )
    public static String stableTNTExplosion = "false";

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static boolean constantSpeedHopperMinecart = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean fastAnvilBreaking = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean fireworksExtra = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean safePlayerDeathDrop = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static int fireworksStacking = 1;

    //#if MC >= 12104
    //$$ @Rule(
    //$$         categories = {IGNY, SURVIVAL, FEATURE}
    //$$ )
    //$$ public static boolean stickyResinBlock = false;
    //#endif

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static boolean renewablePowderSnow = false;
}