package com.liuyue.igny;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.liuyue.igny.utils.IGNYRuleCategory.*;

public class IGNYSettings
{
    public static Set<String> CRAMMING_ENTITIES = new HashSet<>();

    //假玩家生成内存泄露修复
    public static boolean fakePlayerSpawnMemoryLeakFix = false;

    public static final ThreadLocal<Boolean> itemStackCountChanged = ThreadLocal.withInitial(() -> true);
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
    //$$ public static Boolean teleportInheritMinecartsMotionReintroduced = false;
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
            options = {"false", "canBoatTrade", "true"}
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

    //#if MC >= 12000
    @Rule(
            categories = {IGNY, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean safeSoundSuppression = false;
    //#endif

    @Rule(
            categories = {IGNY, COMMAND, FEATURE},
            options = {"false", "true"}
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

    public static class CrammingEntityValidator extends Validator<String> {
        @Override
        public String validate(CommandSourceStack source, CarpetRule<String> rule, String newValue, String string) {
            try {
                if (newValue == null || newValue.equals("#none")) {
                    CRAMMING_ENTITIES.clear();
                    return "#none";
                }

                if (source != null) {
                    var registry = source.getServer().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);

                    CRAMMING_ENTITIES = Arrays.stream(newValue.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .peek(name -> {
                                if (!isValidEntityName(registry, name)) {
                                    source.sendFailure(Component.translatable("igny.settings.failure.unknown_entity", name));
                                    throw new IllegalArgumentException("Unknown entity " + name);
                                }
                            })
                            .collect(Collectors.toSet());
                    return newValue;
                }
                return "#none";
            }catch (IllegalArgumentException e){
                return "#none";
            }
        }

        private boolean isValidEntityName(Registry<EntityType<?>> registry, String name) {
            try {
                ResourceLocation id = ResourceLocation.tryParse(name);
                return id != null && registry.containsKey(id);
            } catch (Exception e) {
                return false;
            }
        }
    }

    //#if MC < 12102
    @Rule(
            categories = {IGNY, BUGFIX},
            options = {"false", "true"}
    )
    public static Boolean optimizedTNTErrorScopeFix = false;
    //#endif

    @Rule(
            categories = {IGNY, CLIENT, SURVIVAL, FEATURE},
            options = {"false", "true"}
    )
    public static Boolean furnaceHasIncombustibleHighlight = false;

    @Rule(
            categories = {IGNY, COMMAND, CREATIVE, FEATURE},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandCustomPlayerPickupItem = "false";

    @Rule(
            categories = {IGNY, COMMAND, CREATIVE, FEATURE},
            options = {"false", "true", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandCustomItemMaxStackSize = "false";
}