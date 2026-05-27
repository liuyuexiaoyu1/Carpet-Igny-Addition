package com.liuyue.igny;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import com.liuyue.igny.manager.LinkedContainerManager.LinkedContainerSetting;
import com.liuyue.igny.rule.*;
import com.liuyue.igny.rule.validators.EntityValidator;
import com.liuyue.igny.rule.validators.SyncmaticaValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.liuyue.igny.utils.IGNYRuleCategory.*;

public class IGNYSettings {
    public static final ThreadLocal<Boolean> CREATIVE_BREAKING = ThreadLocal.withInitial(() -> false);
    public static Set<String> CRAMMING_ENTITIES = new HashSet<>();
    public static List<BlockPos> noUpdatePos = new ArrayList<>();
    public static final Map<String, List<String>> MOD_RULE_TREE = new ConcurrentHashMap<>();
    public static final ThreadLocal<Boolean> fakePlayerSpawnMemoryLeakFix = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<Boolean> itemStackCountChanged = ThreadLocal.withInitial(() -> true);
    public static float originalTPS = 20.0f;
    public static final Set<UUID> sprintWhitelistPlayers = new HashSet<>();
    public static Set<String> EIDWhitelist = new HashSet<>();
    public static final ThreadLocal<Boolean> effectCommandRegistering = ThreadLocal.withInitial(() -> false);
    public static ThreadLocal<Boolean> movingBlocks = ThreadLocal.withInitial(() -> false);
    /**
     * 物品展示框是否正在保存NBT数据
     */
    public static final ThreadLocal<@NotNull Boolean> ITEM_FRAME_SAVE_NBT = ThreadLocal.withInitial(() -> false);

    private static final Set<RuleContext<?>> RULES = new LinkedHashSet<>();

    private static <T> RuleAccessor<T> register(RuleContext<T> context) {
        RULES.add(context);
        return new RuleAccessor<>(context);
    }

    public static void register() {
        SettingsManager settingManager = CarpetServer.settingsManager;
        for (RuleContext<?> context : RULES) {
            CarpetRule<?> rule = context.rule();
            try {
                settingManager.addCarpetRule(rule);
            } catch (UnsupportedOperationException e) {
                IGNYServer.LOGGER.error("{}: {} conflicts with another Carpet extension, disabling rule",
                        IGNYServer.fancyName, rule.name());
            }
        }
        IGNYServer.LOGGER.debug("{} rules registered", RULES.size());
    }

    public static Set<RuleContext<?>> listRules() {
        return RULES;
    }

    public static final RuleAccessor<Boolean> WARDEN_NEVER_DIG = register(
            RuleFactory.of("wardenNeverDig", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> PLAYER_LEVITATION_FREE_SHULKER_BULLET = register(
            RuleFactory.of("playerLevitationFreeShulkerBullet", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> PLAYER_MINING_FATIGUE_FREE_GUARDIAN = register(
            RuleFactory.of("playerMiningFatigueFreeGuardian", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> SHOW_RULE_CHANGE_HISTORY = register(
            RuleFactory.of("showRuleChangeHistory", false)
                    .addCategories(COMMAND, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> FAKE_PLAYER_CAN_PUSH = register(
            RuleFactory.of("fakePlayerCanPush", true)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> WET_SPONGE_CAN_ABSORB_LAVA = register(
            RuleFactory.of("wetSpongeCanAbsorbLava", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<CommandPermissionLevel> COMMAND_PLAYER_ENDER_CHEST_DROP = register(
            RuleFactory.of("commandPlayerEnderChestDrop", CommandPermissionLevel.OPS)
                    .addCategories(COMMAND, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> NO_WARDEN_DARKNESS = register(
            RuleFactory.of("noWardenDarkness", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> FLOATING_ICE_WATER = register(
            RuleFactory.of("floatingIceWater", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> NO_ZOMBIFIED_PIGLIN_NETHER_PORTAL_SPAWN = register(
            RuleFactory.of("noZombifiedPiglinNetherPortalSpawn", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    //#if MC >= 12102
    //$$ public static final RuleAccessor<Boolean> PROJECTILE_DUPLICATION_REINTRODUCED = register(
    //$$         RuleFactory.of("projectileDuplicationReintroduced", false)
    //$$                 .addCategories(FEATURE, PORTING)
    //$$                 .build()
    //$$ );
    //#endif

    //#if MC >= 12104
    //$$ public static final RuleAccessor<Boolean> SKELETONS_PICKUP_SWORDS_REINTRODUCED = register(
    //$$         RuleFactory.of("skeletonsPickupSwordsReintroduced", false)
    //$$                 .addCategories(FEATURE, PORTING)
    //$$                 .build()
    //$$ );
    //#endif

    //#if MC >= 12102
    //$$ public static final RuleAccessor<Boolean> TELEPORT_INHERIT_MINECARTS_MOTION_REINTRODUCED = register(
    //$$         RuleFactory.of("teleportInheritMinecartsMotionReintroduced", false)
    //$$                 .addCategories(FEATURE, PORTING)
    //$$                 .build()
    //$$ );
    //#endif

    //#if MC < 12109
    public static final RuleAccessor<Boolean> TNT_MINECART_EMPTY_DAMAGE_SOURCE_FIX = register(
            RuleFactory.of("tntMinecartEmptyDamageSourceFix", false)
                    .addCategories(FEATURE)
                    .build()
    );
    //#endif

    //#if MC < 12111
    public static final RuleAccessor<Boolean> FAKE_PLAYER_BOAT_YAW_FIX = register(
            RuleFactory.of("fakePlayerBoatYawFix", false)
                    .addCategories(FEATURE, BUGFIX)
                    .build()
    );
    //#endif

    public static final RuleAccessor<String> KILL_FAKE_PLAYER_REMOVE_VEHICLE = register(
            RuleFactory.of("killFakePlayerRemoveVehicle", "true")
                    .addCategories(FEATURE)
                    .addOptions("false", "canBoatTrade", "true")
                    .build()
    );

    public static final RuleAccessor<Boolean> CANDLE_PLACE_ON_INCOMPLETE_BLOCK = register(
            RuleFactory.of("candlePlaceOnIncompleteBlock", false)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<CommandPermissionLevel> COMMAND_FIXNOTEPITCH = register(
            RuleFactory.of("commandFixnotepitch", CommandPermissionLevel.OPS)
                    .addCategories(COMMAND, CREATIVE, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> FIXNOTEPITCH_UPDATE_BLOCK = register(
            RuleFactory.of("fixnotepitchUpdateBlock", false)
                    .addCategories(COMMAND, CREATIVE)
                    .build()
    );

    //#if MC >= 12106
    //$$ public static final RuleAccessor<Boolean> HAPPY_GHAST_NO_CLIP = register(
    //$$         RuleFactory.of("happyGhastNoClip", false)
    //$$                 .addCategories(SURVIVAL, CLIENT, FEATURE)
    //$$                 .build()
    //$$ );
    //#endif

    public static final RuleAccessor<Boolean> NO_WITHER_EFFECT = register(
            RuleFactory.of("noWitherEffect", false)
                    .addCategories(FEATURE)
                    .build()
    );

    //#if MC>=12106
    //$$ public static final RuleAccessor<Boolean> LOCATOR_BAR_NO_FAKE_PLAYER = register(
    //$$         RuleFactory.of("locatorBarNoFakePlayer", false)
    //$$                 .addCategories(FEATURE)
    //$$                 .addListener((source, value) -> {
    //$$                      if (source != null) {
    //$$                          net.minecraft.server.players.PlayerList list;
    //$$                          list = source.getServer().getPlayerList();
    //$$                          for (net.minecraft.server.level.ServerPlayer player : list.getPlayers()) {
    //$$                              if (!(player instanceof carpet.patches.EntityPlayerMPFake)) continue;
    //$$                              net.minecraft.server.waypoints.ServerWaypointManager manager = player.level().getWaypointManager();
    //$$                              if (value) {
    //$$                                  manager.removePlayer(player);
    //$$                              } else {
    //$$                                  manager.addPlayer(player);
    //$$                              }
    //$$                          }
    //$$                      }
    //$$                  })
    //$$                 .build()
    //$$ );
    //#endif

    public static final RuleAccessor<Boolean> FAKE_PLAYER_LOGIN_LOGOUT_NO_CHAT_INFO = register(
            RuleFactory.of("fakePlayerLoginLogoutNoChatInfo", false)
                    .addCategories(COMMAND, FEATURE)
                    .build()
    );

    public static final RuleAccessor<CommandPermissionLevel> COMMAND_PLAYER_OPERATE = register(
            RuleFactory.of("commandPlayerOperate", CommandPermissionLevel.OPS)
                    .addCategories(COMMAND, FEATURE)
                    .build()
    );

    public static final RuleAccessor<CommandPermissionLevel> COMMAND_CLEAR_LIGHT_QUEUE = register(
            RuleFactory.of("commandClearLightQueue", CommandPermissionLevel.OPS)
                    .addCategories(COMMAND, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> FAKE_PLAYER_NO_BREAKING_COOL_DOWN = register(
            RuleFactory.of("fakePlayerNoBreakingCoolDown", false)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> CREATIVE_DESTROY_WATERLOGGED_BLOCK_NO_WATER = register(
            RuleFactory.of("creativeDestroyWaterloggedBlockNoWater", false)
                    .addCategories(CREATIVE, FEATURE)
                    .build()
    );

    //#if MC >= 12005
    public static final RuleAccessor<Integer> TRIAL_SPAWNER_COOL_DOWN = register(
            RuleFactory.of("trialSpawnerCoolDown", 36000)
                    .addCategories(FEATURE)
                    .build()
    );
    //#endif

    public static final RuleAccessor<Integer> REAL_PLAYER_BREAK_LIMIT_PER_TICK = register(
            RuleFactory.of("realPlayerBreakLimitPerTick", 0)
                    .addCategories(SURVIVAL, FEATURE)
                    .addOptions("0", "1", "2", "5", "10")
                    .setLenient()
                    .build()
    );

    public static final RuleAccessor<Integer> REAL_PLAYER_PLACE_LIMIT_PER_TICK = register(
            RuleFactory.of("realPlayerPlaceLimitPerTick", 0)
                    .addCategories(SURVIVAL, FEATURE)
                    .addOptions("0", "1", "2", "5", "10")
                    .setLenient()
                    .build()
    );

    public static final RuleAccessor<Integer> FAKE_PLAYER_BREAK_LIMIT_PER_TICK = register(
            RuleFactory.of("fakePlayerBreakLimitPerTick", 0)
                    .addCategories(SURVIVAL, FEATURE)
                    .addOptions("0", "5", "10", "20", "50")
                    .setLenient()
                    .build()
    );

    public static final RuleAccessor<Integer> FAKE_PLAYER_PLACE_LIMIT_PER_TICK = register(
            RuleFactory.of("fakePlayerPlaceLimitPerTick", 0)
                    .addCategories(SURVIVAL, FEATURE)
                    .addOptions("0", "5", "10", "20", "50")
                    .setLenient()
                    .build()
    );

    public static final RuleAccessor<Boolean> PLAYER_OPERATION_LIMITER = register(
            RuleFactory.of("playerOperationLimiter", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> GENERATE_NETHER_PORTAL = register(
            RuleFactory.of("generateNetherPortal", false)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<Integer> PLACE_COMPOSTER_COMPOST = register(
            RuleFactory.of("placeComposterCompost", 0)
                    .addCategories(CREATIVE, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Integer> ENDER_DRAGON_DEATH_RISE_LIMIT = register(
            RuleFactory.of("enderDragonDeathRiseLimit", -1145)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<Integer> ENDER_DRAGON_DEATH_DROP_EXP = register(
            RuleFactory.of("enderDragonDeathDropExp", -1)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> INSTANT_SPAWN_ENDER_DRAGON = register(
            RuleFactory.of("instantSpawnEnderDragon", false)
                    .addCategories(CREATIVE, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Integer> MAX_END_PORTAL_SIZE = register(
            RuleFactory.of("maxEndPortalSize", -1)
                    .addCategories(CREATIVE, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> ALLOW_RECTANGULAR_END_PORTAL = register(
            RuleFactory.of("allowRectangularEndPortal", false)
                    .addCategories(CREATIVE, FEATURE)
                    .build()
    );

    //#if MC >= 12005
    public static final RuleAccessor<Boolean> INSTANT_VAULT_SPAWN_LOOT = register(
            RuleFactory.of("instantVaultSpawnLoot", false)
                    .addCategories(CREATIVE, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Integer> TRIAL_SPAWNER_LOOT_MULTIPLIER = register(
            RuleFactory.of("trialSpawnerLootMultiplier", 1)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<Integer> TRIAL_SPAWNER_DROP_KEY_PROBABILITY = register(
            RuleFactory.of("trialSpawnerDropKeyProbability", -1)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> INSTANT_TRIAL_SPAWNER_SPAWN_LOOT = register(
            RuleFactory.of("instantTrialSpawnerSpawnLoot", false)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<String> SIMPLE_SOUND_SUPPRESSION = register(
            RuleFactory.of("simpleSoundSuppression", "false")
                    .addCategories(CREATIVE, FEATURE)
                    .addOptions("false", "true")
                    .setLenient()
                    .build()
    );
    //#endif

    //#if MC >= 12000
    public static final RuleAccessor<Boolean> SAFE_SOUND_SUPPRESSION = register(
            RuleFactory.of("safeSoundSuppression", false)
                    .addCategories(FEATURE)
                    .build()
    );
    //#endif

    public static final RuleAccessor<Boolean> TWO_CHANGED_RULE_VALUE_SET_DEFAULT = register(
            RuleFactory.of("twoChangedRuleValueSetDefault", false)
                    .addCategories(COMMAND, FEATURE)
                    .build()
    );

    public static final RuleAccessor<String> OPTIMIZED_ENTITY_LIST = register(
            RuleFactory.of("optimizedEntityList", "#none")
                    .addCategories(OPTIMIZATION, FEATURE)
                    .addOptions("#none", "minecraft:warden", "minecraft:piglin", "minecraft:warden,minecraft:piglin")
                    .setLenient()
                    .addValidator(EntityValidator.createOptimizedEntityValidator())
                    .addListener(EntityValidator::onOptimizedEntityListChanged)
                    .build()
    );

    public static final RuleAccessor<Integer> OPTIMIZED_ENTITY_LIMIT = register(
            RuleFactory.of("optimizedEntityLimit", 100)
                    .addCategories(OPTIMIZATION, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> OPTIMIZED_TNT_ERROR_SCOPE_FIX = register(
            RuleFactory.of("optimizedTNTErrorScopeFix", false)
                    .addCategories(BUGFIX)
                    .build()
    );

    public static final RuleAccessor<CommandPermissionLevel> COMMAND_CUSTOM_PLAYER_PICKUP_ITEM = register(
            RuleFactory.of("commandCustomPlayerPickupItem", CommandPermissionLevel.FALSE)
                    .addCategories(COMMAND, CREATIVE, FEATURE)
                    .build()
    );

    //#if MC >= 12006
    public static final RuleAccessor<CommandPermissionLevel> COMMAND_CUSTOM_ITEM_MAX_STACK_SIZE = register(
            RuleFactory.of("commandCustomItemMaxStackSize", CommandPermissionLevel.FALSE)
                    .addCategories(COMMAND, CREATIVE, FEATURE)
                    .build()
    );
    //#endif

    public static final RuleAccessor<Boolean> PLAYER_HUNGRY_VALUE_NO_DECREASE = register(
            RuleFactory.of("playerHungryValueNoDecrease", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> PLAYER_LOW_HUNGRY_VALUE_CAN_SPRINT = register(
            RuleFactory.of("playerLowHungryValueCanSprint", false)
                    .addCategories(SURVIVAL, CLIENT, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> INSTANT_FROG_EAT = register(
            RuleFactory.of("instantFrogEat", false)
                    .addCategories(FEATURE)
                    .build()
    );

    //#if MC >= 12111
    //$$ public static final RuleAccessor<Boolean> ALLOW_INVALID_MOTION = register(
    //$$         RuleFactory.of("allowInvalidMotion", false)
    //$$                 .addCategories(FEATURE)
    //$$                 .build()
    //$$ );
    //#endif

    public static final RuleAccessor<Boolean> ACCELERATE_BABY_VILLAGER_GROWTH = register(
            RuleFactory.of("accelerateBabyVillagerGrowth", false)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> LIGHTNING_BOLT_NO_FIRE = register(
            RuleFactory.of("lightningBoltNoFire", false)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> DISPENSER_TRADE = register(
            RuleFactory.of("dispenserTrade", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> DISPENSER_TRADE_FAIL_DISPERSE_ITEM = register(
            RuleFactory.of("dispenserTradeFailDisperseItem", true)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> RENEWABLE_CALCITE = register(
            RuleFactory.of("renewableCalcite", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> PURE_SHULKER_BOX_DISPENSE = register(
            RuleFactory.of("pureShulkerBoxDispense", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> STRUCTURE_BLOCK_NO_BLOCK_UPDATE = register(
            RuleFactory.of("structureBlockNoBlockUpdate", false)
                    .addCategories(CREATIVE, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> SPAWN_MAX_COUNT_IGNORES_CHUNK_OVERLAP = register(
            RuleFactory.of("spawnMaxCountIgnoresChunkOverlap", false)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> PRIORITIZE_FLYING_USE_ITEM = register(
            RuleFactory.of("prioritizeFlyingUseItem", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> OPTIMIZED_SPAWNING = register(
            RuleFactory.of("optimizedSpawning", false)
                    .addCategories(OPTIMIZATION)
                    .build()
    );

    public static final RuleAccessor<Boolean> DYED_FROG = register(
            RuleFactory.of("dyedFrog", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> BETTER_LOYALTY_TRIDENT = register(
            RuleFactory.of("betterLoyaltyTrident", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<CommandPermissionLevel> REMOVE_SYNCMATICA_PERMISSION = register(
            RuleFactory.of("removeSyncmaticaPermission", CommandPermissionLevel.TRUE)
                    .addValidator(SyncmaticaValidator.createValidator())
                    .build()
    );

    public static final RuleAccessor<Boolean> DISABLE_WATCH_DOG = register(
            RuleFactory.of("disableWatchDog", false)
                    .addCategories(IGNY)
                    .build()
    );

    public static final RuleAccessor<Boolean> PODZOL_SPREAD = register(
            RuleFactory.of("podzolSpread", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> NO_OWNER_TNT_LOOTING_III = register(
            RuleFactory.of("noOwnerTntLootingIII", false)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> GLOBAL_DAYLIGHT_DETECTOR = register(
            RuleFactory.of("globalDaylightDetector", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> NOTE_BLOCK_SELF_CHECK = register(
            RuleFactory.of("noteBlockSelfCheck", false)
                    .addCategories(CREATIVE, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> NO_CREATIVE_DESTROY_ATTACHMENT_DROPS = register(
            RuleFactory.of("noCreativeDestroyAttachmentDrops", false)
                    .addCategories(CREATIVE, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> SHOW_RULE_SOURCE = register(
            RuleFactory.of("showRuleSource", false)
                    .build()
    );

    public static final RuleAccessor<Boolean> THE_END_CAN_CREATE_NETHER_PORTAL = register(
            RuleFactory.of("theEndCanCreateNetherPortal", false)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> RENEWABLE_END_GATEWAY_PORTAL = register(
            RuleFactory.of("renewableEndGatewayPortal", false)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> END_GATEWAY_PORTAL_NO_COOLDOWN = register(
            RuleFactory.of("endGatewayPortalNoCooldown", false)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> LIQUID_SOURCE_CAN_DESTROY = register(
            RuleFactory.of("liquidSourceCanDestroy", false)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<String> BETTER_SPRINT_GAME_TICK = register(
            RuleFactory.of("betterSprintGameTick", "false")
                    .addCategories(SURVIVAL, FEATURE)
                    .addOptions("false", "true", "playerJoin")
                    .addListener((source, value) -> {
                        var server = IGNYServer.getInstance().getMinecraftServer();
                        if (server != null) {
                            com.liuyue.igny.utils.TickUtil.checkTickRate(server);
                        }
                    })
                    .build()
    );

    //#if MC >= 26.1
    //$$ public static final RuleAccessor<Boolean> TRIPWIRE_HOOK_DUPE_REINTRODUCED = register(
    //$$         RuleFactory.of("tripwireHookDupeReintroduced", false)
    //$$                 .addCategories(FEATURE, PORTING)
    //$$                 .build()
    //$$ );
    //#endif

    public static final RuleAccessor<Boolean> SHOW_CLASS_MIXIN_LIST = register(
            RuleFactory.of("showClassMixinList", false)
                    .build()
    );

    public static final RuleAccessor<Boolean> PIGLIN_NEUTRAL_BEHAVIOR = register(
            RuleFactory.of("piglinNeutralBehavior", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> FAKE_PLAYER_MEMORY_LEAK_FIX = register(
            RuleFactory.of("fakePlayerMemoryLeakFix", false)
                    .addCategories(BUGFIX)
                    .build()
    );

    public static final RuleAccessor<String> LIQUID_NEVER_SPREAD = register(
            RuleFactory.of("liquidNeverSpread", "false")
                    .addCategories(FEATURE)
                    .addOptions("false", "true", "liquid_source")
                    .build()
    );

    public static final RuleAccessor<Boolean> SUPER_EFFECT_LEVEL = register(
            RuleFactory.of("superEffectLevel", false)
                    .addCategories(FEATURE)
                    .addListener((source, value) -> {
                        if (source != null) {
                            for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
                                source.getServer().getCommands().sendCommands(player);
                            }
                        }
                    })
                    .build()
    );

    public static final RuleAccessor<String> SIMPLE_ENTITY_ID_SUPPRESSION = register(
            RuleFactory.of("simpleEntityIDSuppression", "false")
                    .addCategories(CREATIVE, FEATURE)
                    .addOptions("false", "true")
                    .setLenient()
                    .build()
    );

    public static final RuleAccessor<String> ENTITY_ID_SUPPRESSION_WHITELIST = register(
            RuleFactory.of("entityIDSuppressionWhitelist", "#all")
                    .addCategories(CREATIVE, FEATURE)
                    .addOptions("#all", "#none", "minecraft:item")
                    .setLenient()
                    .addValidator(EntityValidator.createEntityIDSuppressionValidator())
                    .addListener(EntityValidator::onEntityIDWhitelistChanged)
                    .build()
    );

    public static final RuleAccessor<Boolean> SHULKER_BOX_IN_SHULKER_BOX = register(
            RuleFactory.of("shulkerBoxInShulkerBox", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    //#if MC >= 26.2
    //$$ public static final RuleAccessor<Boolean> COMPARATOR_DUPE_REINTRODUCED = register(
    //$$         RuleFactory.of("comparatorDupeReintroduced", false)
    //$$                 .addCategories(FEATURE, PORTING)
    //$$                 .build()
    //$$ );
    //#endif

    public static final RuleAccessor<Boolean> FESTIVE_EASTER_EGG = register(
            RuleFactory.of("festiveEasterEgg", true)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<String> DRILL_ANVIL = register(
            RuleFactory.of("drillAnvil", "false")
                    .addCategories(SURVIVAL, FEATURE)
                    .addOptions("false", "nonFluid", "true")
                    .build()
    );

    public static final RuleAccessor<Boolean> TRANSPARENT_NIGHTMARISH_BLOCK = register(
            RuleFactory.of("transparentNightmarishBlock", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    //#if MC >= 12102
    //$$ public static final RuleAccessor<Boolean> GHOST_ENDER_PEARL_FIX = register(
    //$$         RuleFactory.of("ghostEnderPearlFix", false)
    //$$                 .addCategories(BUGFIX)
    //$$                 .build()
    //$$ );
    //#endif

    public static final RuleAccessor<Boolean> BETTER_FLIPPIN_CACTUS = register(
            RuleFactory.of("betterFlippinCactus", false)
                    .addCategories(FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> BLOCKABLE_AMETHYST = register(
            RuleFactory.of("blockableAmethyst", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<String> STABLE_TNT_EXPLOSION = register(
            RuleFactory.of("stableTNTExplosion", "false")
                    .addCategories(FEATURE)
                    .addOptions("false", "minimum", "average", "maximum")
                    .build()
    );

    public static final RuleAccessor<Boolean> CONSTANT_SPEED_HOPPER_MINECART = register(
            RuleFactory.of("constantSpeedHopperMinecart", false)
                    .addCategories(CREATIVE, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> FAST_ANVIL_BREAKING = register(
            RuleFactory.of("fastAnvilBreaking", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> FIREWORKS_EXTRA = register(
            RuleFactory.of("fireworksExtra", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> SAFE_PLAYER_DEATH_DROP = register(
            RuleFactory.of("safePlayerDeathDrop", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Integer> FIREWORKS_STACKING = register(
            RuleFactory.of("fireworksStacking", 1)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    //#if MC >= 12104
    //$$ public static final RuleAccessor<Boolean> STICKY_RESIN_BLOCK = register(
    //$$         RuleFactory.of("stickyResinBlock", false)
    //$$                 .addCategories(SURVIVAL, FEATURE)
    //$$                 .build()
    //$$ );
    //#endif

    public static final RuleAccessor<Boolean> CONVENIENT_REGENERATE_POWDER_SNOW = register(
            RuleFactory.of("convenientRegeneratePowderSnow", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<LinkedContainerSetting> LINKEABLE_ENDER_CHEST = register(
            RuleFactory.of("linkableEnderChest", LinkedContainerSetting.FALSE)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<String> INVISIBLE_ITEM_FRAMES = register(
            RuleFactory.of("invisibleItemFrames", "false")
                    .addCategories(SURVIVAL, FEATURE)
                    .addOptions("false", "true")
                    .setLenient()
                    .build()
    );

    public static final RuleAccessor<Boolean> BONEMEALABLE_AMETHYST = register(
            RuleFactory.of("bonemealableAmethyst", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> MAGMA_BLOCK_MELT = register(
            RuleFactory.of("magmaBlockMelt", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> ENTITY_DIMENSION_CHANGE_MEMORY_LEAK_FIX = register(
            RuleFactory.of("entityDimensionChangeMemoryLeakFix", false)
                    .addCategories(BUGFIX, FEATURE)
                    .build()
    );

    public static final RuleAccessor<Boolean> DISABLE_SCULK_VEIN_GROWTH = register(
            RuleFactory.of("disableSculkVeinGrowth", false)
                    .addCategories(SURVIVAL, FEATURE)
                    .build()
    );

    //#if MC >= 26.2
    //$$ public static final RuleAccessor<Boolean> ALLOW_INVALID_BEACON_EFFECT = register(
    //$$         RuleFactory.of("allowInvalidBeaconEffect", false)
    //$$                 .addCategories(SURVIVAL, PORTING, FEATURE)
    //$$                 .build()
    //$$ );
    //#endif

    //#if MC >= 26.2
    //$$ public static final RuleAccessor<Boolean>  ENTITY_ID_COLLISION_REINTRODUCE = register(
    //$$         RuleFactory.of("entityIDCollisionReintroduce", false)
    //$$                 .addCategories(PORTING, FEATURE)
    //$$                 .build()
    //$$ );
    //#endif

    //#if MC < 26.2
    public static final RuleAccessor<Boolean>  ENTITY_ID_COLLISION_FIX = register(
            RuleFactory.of("entityIDCollisionFix", false)
                    .addCategories(BUGFIX, FEATURE)
                    .build()
    );
    //#endif
}
