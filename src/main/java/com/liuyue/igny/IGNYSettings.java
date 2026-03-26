package com.liuyue.igny;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import com.liuyue.igny.rule.RuleCallback;
import com.liuyue.igny.rule.annotation.ObservedRule;
import com.liuyue.igny.rule.validators.CrammingEntityValidator;
import com.liuyue.igny.rule.validators.EndPortalSizeValidator;
import com.liuyue.igny.rule.validators.SyncmaticaValidator;
import com.liuyue.igny.utils.TickUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;

import java.util.*;

import static com.liuyue.igny.utils.IGNYRuleCategory.*;

public class IGNYSettings {
    public static final ThreadLocal<Boolean> CREATIVE_BREAKING = ThreadLocal.withInitial(() -> false);
    public static Set<String> CRAMMING_ENTITIES = new HashSet<>();
    public static List<BlockPos> noUpdatePos = new ArrayList<>();
    public static final Map<String, List<String>> modRuleTree = new HashMap<>();
    //假玩家生成内存泄露修复
    public static final ThreadLocal<Boolean> fakePlayerSpawnMemoryLeakFix = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<Boolean> itemStackCountChanged = ThreadLocal.withInitial(() -> true);
    public static float originalTPS = 20.0f;
    public static final Set<UUID> sprintWhitelistPlayers = new HashSet<>();

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
    public static Boolean wetSpongeCanAbsorbLava = false;

    @Rule(
            categories = {IGNY, COMMAND, FEATURE},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandPlayerEnderChestDrop = "ops";

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static Boolean noWardenDarkness = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static Boolean floatingIceWater = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static Boolean noZombifiedPiglinNetherPortalSpawn = false;

    //#if MC >= 12102
    //$$ @Rule(
    //$$        categories = {IGNY,FEATURE}
    //$$ )
    //$$ public static Boolean projectileDuplicationReintroduced = false;
    //#endif

    //#if MC >= 12104
    //$$ @Rule(
    //$$        categories = {IGNY,FEATURE}
    //$$ )
    //$$ public static Boolean skeletonsPickupSwordsReintroduced = false;
    //#endif

    //#if MC >= 12102
    //$$ @Rule(
    //$$        categories = {IGNY,FEATURE}
    //$$ )
    //$$ public static Boolean teleportInheritMinecartsMotionReintroduced = false;
    //#endif

    //#if MC < 12109
    @Rule(
            categories = {IGNY,FEATURE}
    )
    public static Boolean tntMinecartEmptyDamageSourceFix = false;
    //#endif

    //#if MC < 12111
    @Rule(
            categories = {IGNY,FEATURE,BUGFIX}
    )
    public static Boolean fakePlayerBoatYawFix = false;
    //#endif

    @Rule(
            categories = {IGNY,FEATURE},
            options = {"false", "canBoatTrade", "true"}
    )
    public static String killFakePlayerRemoveVehicle = "true";

    @Rule(
            categories = {IGNY,FEATURE}
    )
    public static Boolean candlePlaceOnIncompleteBlock = false;

    @Rule(
            categories = {IGNY, COMMAND, CREATIVE, FEATURE},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandFixnotepitch = "ops";

    @Rule(
            categories = {IGNY, COMMAND, CREATIVE}
    )
    public static Boolean fixnotepitchUpdateBlock = false;

    //#if MC >= 12106
    //$$ @Rule(
    //$$        categories = {IGNY, SURVIVAL, CLIENT, FEATURE}
    //$$ )
    //$$ public static Boolean happyGhastNoClip = false;
    //#endif

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Boolean noWitherEffect = false;

    //#if MC>=12106
    //$$ @Rule(
    //$$        categories = {IGNY, FEATURE}
    //$$ )
    //$$ public static Boolean locatorBarNoFakePlayer = false;
    //#endif

    @Rule(
            categories = {IGNY, COMMAND, FEATURE}
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
            categories = {IGNY, FEATURE}
    )
    public static Boolean fakePlayerNoBreakingCoolDown = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static Boolean creativeDestroyWaterloggedBlockNoWater = false;

    //#if MC >= 12005
    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Integer trialSpawnerCoolDown = 36000;
    //#endif

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"0", "1", "2", "5", "10"},
            strict = false
    )
    public static Integer realPlayerBreakLimitPerTick = 0;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"0", "1", "2", "5", "10"},
            strict = false
    )
    public static Integer realPlayerPlaceLimitPerTick = 0;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"0", "5", "10", "20", "50"},
            strict = false
    )
    public static Integer fakePlayerBreakLimitPerTick = 0;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"0", "5", "10", "20", "50"},
            strict = false
    )
    public static Integer fakePlayerPlaceLimitPerTick = 0;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static Boolean playerOperationLimiter = false;

    @Rule(
            categories = {IGNY, FEATURE}
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
            categories = {IGNY, CREATIVE, FEATURE},
            validators = EndPortalSizeValidator.class
    )
    public static Integer maxEndPortalSize = -1;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static Boolean allowRectangularEndPortal = false;

    //#if MC >= 12005
    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
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
            categories = {IGNY, FEATURE}
    )
    public static Boolean instantTrialSpawnerSpawnLoot = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE},
            strict = false
    )
    public static String simpleSoundSuppression = "false";
    //#endif

    //#if MC >= 12000
    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Boolean safeSoundSuppression = false;
    //#endif

    @Rule(
            categories = {IGNY, COMMAND, FEATURE}
    )
    public static Boolean twoChangedRuleValueSetDefault = false;

    @Rule(
            categories = {IGNY, OPTIMIZATION, FEATURE},
            options = {"#none", "minecraft:warden", "minecraft:piglin", "minecraft:warden,minecraft:piglin"},
            validators = CrammingEntityValidator.class,
            strict = false
    )
    public static String optimizedEntityList = "#none";

    @Rule(
            categories = {IGNY,OPTIMIZATION,FEATURE}
    )
    public static Integer optimizedEntityLimit = 100;

    @Rule(
            categories = {IGNY, BUGFIX}
    )
    public static Boolean optimizedTNTErrorScopeFix = false;

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
    public static Boolean playerHungryValueNoDecrease = false;

    @Rule(
            categories = {IGNY, SURVIVAL, CLIENT, FEATURE}
    )
    public static Boolean playerLowHungryValueCanSprint = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Boolean instantFrogEat = false;

    //#if MC >= 12111
    //$$ @Rule(
    //$$        categories = {IGNY, FEATURE}
    //$$ )
    //$$ public static Boolean allowInvalidMotion = false;
    //#endif

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Boolean accelerateBabyVillagerGrowth = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Boolean lightningBoltNoFire = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static Boolean dispenserTrade = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static Boolean dispenserTradeFailDisperseItem = true;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static Boolean renewableCalcite = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static Boolean pureShulkerBoxDispense = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static Boolean structureBlockNoBlockUpdate = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Boolean spawnMaxCountIgnoresChunkOverlap = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static Boolean prioritizeFlyingUseItem = false;

    @Rule(
            categories = {IGNY, OPTIMIZATION}
    )
    public static Boolean optimizedSpawning = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static Boolean dyedFrog = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static Boolean betterLoyaltyTrident = false;

    @Rule(
            categories = {IGNY},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"},
            validators = SyncmaticaValidator.class
    )
    public static String removeSyncmaticaPermission = "true";

    @Rule(
            categories = {IGNY}
    )
    public static Boolean disableWatchDog = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static Boolean podzolSpread = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Boolean noOwnerTntLootingIII = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static Boolean globalDaylightDetector = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static Boolean noteBlockSelfCheck = false;

    @Rule(
            categories = {IGNY, CREATIVE, FEATURE}
    )
    public static Boolean noCreativeDestroyAttachmentDrops = false;

    @Rule(
            categories = {IGNY}
    )
    public static Boolean showRuleSource = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Boolean theEndCanCreateNetherPortal = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Boolean renewableEndGatewayPortal = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Boolean endGatewayPortalNoCooldown = false;

    @Rule(
            categories = {IGNY, FEATURE}
    )
    public static Boolean liquidSourceCanDestroy = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE},
            options = {"false", "true", "playerJoin"}
    )
    @ObservedRule(callback = GameTickCallback.class)
    public static String betterSprintGameTick = "false";

    static class GameTickCallback implements RuleCallback<String> {
        @Override
        public void onChange(CommandSourceStack source, CarpetRule<String> rule, String oldValue, String newValue) {
            TickUtil.checkTickRate(IGNYServer.getInstance().getMinecraftServer());
        }
    }

    //#if MC >= 26.1
    //$$ @Rule(
    //$$         categories = {IGNY, FEATURE}
    //$$ )
    //$$ public static Boolean tripwireHookDupeReintroduced = false;
    //#endif

    @Rule(
            categories = {IGNY}
    )
    public static Boolean showClassMixinList = false;

    @Rule(
            categories = {IGNY, SURVIVAL, FEATURE}
    )
    public static Boolean piglinNeutralBehavior = false;

    @Rule(
            categories = {IGNY, BUGFIX}
    )
    public static Boolean fakePlayerMemoryLeakFix = false;
}