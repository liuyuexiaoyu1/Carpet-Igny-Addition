package com.liuyue.igny.helper.betterEasyPlaceProtocol;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter.*;
import com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol.BeaconBlockEntityAccessor;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.ItemStackProtocolDataAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//#if MC >= 12003
//#endif
//#if MC >= 12105
//$$ import net.minecraft.world.level.block.FlowerBedBlock;
//#else
//$$ import net.minecraft.world.level.block.PinkPetalsBlock;
//#endif

import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.decodeProtocolValueFromHitDim;
import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.getRelativeHitX;
import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.getRelativeHitZ;
import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.isExtraProtocol;
import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.isProtocol;

public class BetterEasyPlaceProtocolHandler {
    public static final long EASY_PLACE_RAIL_BLOCK_NO_SHAPE_UPDATE = 1L;
    public static final long EASY_PLACE_PISTON_NO_UPDATE = 1L << 1;
    public static final long EASY_PLACE_PISTON_PLACE_HEAD = 1L << 3;

    private static final Map<Class<? extends Block>, BlockProtocolStateAdapter> ADAPTERS = new HashMap<>();

    static {
        register(BannerBlock.class, new BannerBlockProtocolAdapter());
        register(BeaconBlock.class, new BeaconBlockProtocolAdapter());
        register(BellBlock.class, new BellBlockProtocolAdapter());
        register(CampfireBlock.class, new CampfireBlockProtocolAdapter());
        register(CandleBlock.class, new CandleBlockProtocolAdapter());
        register(CeilingHangingSignBlock.class, new CeilingHangingSignBlockProtocolAdapter());
        register(CommandBlock.class, new CommandBlockProtocolAdapter());
        register(ComparatorBlock.class, new ComparatorBlockProtocolAdapter());
        register(ComposterBlock.class, new ComposterBlockProtocolAdapter());
        register(BaseCoralWallFanBlock.class, new CoralWallFanBlockProtocolAdapter());
        register(DaylightDetectorBlock.class, new DaylightDetectorBlockProtocolAdapter());
        register(DetectorRailBlock.class, new DetectorRailBlockProtocolAdapter());
        register(FaceAttachedHorizontalDirectionalBlock.class, new FaceAttachedHorizontalDirectionalBlockProtocolAdapter());
        register(FenceGateBlock.class, new FenceGateBlockProtocolAdapter());
        register(HugeMushroomBlock.class, new HugeMushroomBlockProtocolAdapter());
        register(JigsawBlock.class, new JigsawBlockProtocolAdapter());
        register(LanternBlock.class, new LanternBlockProtocolAdapter());
        register(LeverBlock.class, new LeverBlockProtocolAdapter());
        register(LightBlock.class, new LightBlockProtocolAdapter());
        register(MultifaceBlock.class, new MultifaceBlockProtocolAdapter());
        register(NoteBlock.class, new NoteBlockProtocolAdapter());
        register(PistonBaseBlock.class, new PistonBaseBlockProtocolAdapter());
        register(PoweredRailBlock.class, new PoweredRailBlockProtocolAdapter());
        register(RailBlock.class, new RailBlockProtocolAdapter());
        register(RedStoneWireBlock.class, new RedStoneWireBlockProtocolAdapter());
        register(RedstoneLampBlock.class, new RedstoneLampBlockProtocolAdapter());
        register(RedstoneTorchBlock.class, new RedstoneTorchBlockProtocolAdapter());
        register(RedstoneWallTorchBlock.class, new RedstoneWallTorchBlockProtocolAdapter());
        register(RepeaterBlock.class, new RepeaterBlockProtocolAdapter());
        register(SeaPickleBlock.class, new SeaPickleBlockProtocolAdapter());
        register(SkullBlock.class, new SkullBlockProtocolAdapter());
        register(SnowLayerBlock.class, new SnowLayerBlockProtocolAdapter());
        register(StairBlock.class, new StairBlockProtocolAdapter());
        register(StandingSignBlock.class, new StandingSignBlockProtocolAdapter());
        register(StructureBlock.class, new StructureBlockProtocolAdapter());
        register(TrapDoorBlock.class, new TrapDoorBlockProtocolAdapter());
        register(TurtleEggBlock.class, new TurtleEggBlockProtocolAdapter());
        register(VineBlock.class, new VineBlockProtocolAdapter());
        //#if MC >= 12003
        register(CopperBulbBlock.class, new CopperBulbBlockProtocolAdapter());
        register(CrafterBlock.class, new CrafterBlockProtocolAdapter());
        //#endif
        //#if MC >= 12105
        //$$ register(FlowerBedBlock.class, new FlowerBedBlockProtocolAdapter());
        //#else
        register(PinkPetalsBlock.class, new FlowerBedBlockProtocolAdapter());
        //#endif
        register(HorizontalDirectionalBlock.class, new HorizontalDirectionalBlockProtocolAdapter());
        register(HopperBlock.class, new HopperBlockProtocolAdapter());
        register(WallBlock.class, new WallBlockProtocolAdapter());
        register(CrossCollisionBlock.class,  new CrossCollisionBlockProtocolAdapter());
    }

    private static boolean easyPlaceState = false;
    private static long placeProperty = 0;
    private static BlockPos placeTargetPos = BlockPos.ZERO;
    private static Block placeTargetBlock = Blocks.AIR;

    private BetterEasyPlaceProtocolHandler() {
    }

    public static boolean isRuleEnabled() {
        return IGNYSettings.BETTER_EASY_PLACE_PROTOCOL.value();
    }

    public static boolean isEasyPlaceState() {
        return easyPlaceState;
    }

    public static void setEasyPlaceState(boolean value) {
        easyPlaceState = value;
    }

    public static long getPlaceProperty() {
        return placeProperty;
    }

    public static void setPlaceProperty(long val) {
        placeProperty = val;
    }

    public static BlockPos getPlaceTargetPos() {
        return placeTargetPos;
    }

    public static void setPlaceTargetPos(BlockPos pos) {
        placeTargetPos = pos;
    }

    public static Block getPlaceTargetBlock() {
        return placeTargetBlock;
    }

    public static void setPlaceTargetBlock(Block block) {
        placeTargetBlock = block;
    }

    public static boolean hasPlaceFlag(long flag) {
        return (placeProperty & flag) == flag;
    }

    public static void setPlaceFlag(long flag) {
        placeProperty |= flag;
    }

    public static void register(Class<? extends Block> blockClass, BlockProtocolStateAdapter adapter) {
        ADAPTERS.put(blockClass, adapter);
    }

    public static @Nullable BlockProtocolStateAdapter getAdapter(Block block) {
        for (Class<?> cls = block.getClass(); cls != null && cls != Block.class; cls = cls.getSuperclass()) {
            BlockProtocolStateAdapter adapter = ADAPTERS.get(cls);
            if (adapter != null) {
                return adapter;
            }
        }
        return null;
    }

    public static boolean isExtraProtocolPlacement(Block block, BlockPlaceContext context) {
        if (!isRuleEnabled()) return false;

        double relativeHitX = getRelativeHitX(context.getClickLocation(), context.getClickedPos());
        if (!isProtocol(relativeHitX)) return false;

        int protocolValue = decodeProtocolValueFromHitDim(relativeHitX);
        if (!isExtraProtocol(protocolValue)) return false;

        return getAdapter(block) != null;
    }

    public static @Nullable BlockState decodePlacementState(Block block, BlockPlaceContext context) {
        if (!isRuleEnabled()) return null;

        double relativeHitX = getRelativeHitX(context.getClickLocation(), context.getClickedPos());
        if (!isProtocol(relativeHitX)) return null;

        
        int protocolValue = decodeProtocolValueFromHitDim(relativeHitX);
        if (!isExtraProtocol(protocolValue)) {
            
            return decodeVanillaPlacementState(block, context, relativeHitX);
        }

        BlockProtocolStateAdapter adapter = getAdapter(block);
        if (adapter == null) return null;

        BlockState vanillaState = block.getStateForPlacement(context);
        if (vanillaState == null) {
            
            
            if (adapter.igny$getProtocolType() != BlockProtocolStateAdapter.ProtocolType.REPLACE) {
                return null;
            }
            vanillaState = block.defaultBlockState();
        }

        if (adapter.igny$getProtocolType() == BlockProtocolStateAdapter.ProtocolType.REPLACE) {
            
            int rawProtocolValue = EasyPlaceExtraProtocolHelper.extraProtocolValueToRawProtocolValue(protocolValue);
            return adapter.igny$fromProtocolValue(rawProtocolValue, vanillaState, context);
        }
        
        return adapter.igny$fromProtocolValue(protocolValue, vanillaState, context);
    }

    private static final Set<String> VANILLA_V3_WHITELISTED_PROPERTY_NAMES = new HashSet<>(Arrays.asList(
            "inverted",
            "open",
            "attachment",
            "axis",
            "half",
            "face",
            "type",
            "mode",
            "hinge",
            "orientation",
            "shape",
            "straight_shape",
            "bites",
            "delay",
            "note",
            "rotation"
    ));

    public static @Nullable BlockState decodeVanillaPlacementState(Block block, BlockPlaceContext context, double relativeHitX) {
        if (!isProtocol(relativeHitX)) return null;

        int raw = ((int) relativeHitX - 2) >>> 1;
        if (raw < 0) return null;

        boolean v2AdditionBlock = block instanceof net.minecraft.world.level.block.CakeBlock
                || block instanceof net.minecraft.world.level.block.StairBlock
                || block instanceof net.minecraft.world.level.block.TrapDoorBlock
                || block instanceof net.minecraft.world.level.block.SlabBlock;

        if (v2AdditionBlock && (raw & 0b0000_1000) == 0) {
            return decodeVanillaV2PlacementState(block, context, raw);
        }
        return decodeVanillaV3PlacementState(block, context, raw);
    }

    private static @Nullable BlockState decodeVanillaV3PlacementState(Block block, BlockPlaceContext context, int raw) {
        BlockState state = block.getStateForPlacement(context);
        if (state == null) {
            state = block.defaultBlockState();
        }
        boolean decoded = false;

        
        
        Property<Direction> directionProperty = EasyPlaceExtraProtocolHelper.getFirstDirectionProperty(state);
        int shiftAmount = 3;
        if (directionProperty != null && !"vertical_direction".equals(directionProperty.getName())) {
            int facingOrdinal = raw & 0b0000_0111;
            state = state.setValue(directionProperty, Direction.values()[facingOrdinal % 6]);
            decoded = true;
        } else {
            shiftAmount = 0;
            directionProperty = null;
        }

        
        List<Property<?>> props = new ArrayList<>(state.getProperties());
        props.sort(Comparator.comparing(Property::getName));

        for (Property<?> property : props) {
            if (property == directionProperty) continue;
            if (!VANILLA_V3_WHITELISTED_PROPERTY_NAMES.contains(property.getName())) continue;

            @SuppressWarnings({"unchecked", "rawtypes"})
            List<Comparable> values = new ArrayList<>((java.util.Collection) property.getPossibleValues());
            @SuppressWarnings({"unchecked", "rawtypes"})
            Comparator<Comparable> naturalOrder = Comparator.naturalOrder();
            values.sort(naturalOrder);
            int requiredBits = Mth.ceil(Mth.log2(values.size()));
            int valueIndex = (raw >>> shiftAmount) & ((1 << requiredBits) - 1);
            shiftAmount += requiredBits;

            if (valueIndex >= values.size()) {
                continue;
            }
            @SuppressWarnings({"unchecked", "rawtypes"})
            BlockState newState = state.setValue((Property) property, values.get(valueIndex));
            state = newState;
            decoded = true;
        }

        return decoded ? state : null;
    }

    private static @Nullable BlockState decodeVanillaV2PlacementState(Block block, BlockPlaceContext context, int raw) {
        BlockState state = block.getStateForPlacement(context);
        if (state == null) {
            state = block.defaultBlockState();
        }
        boolean decoded = false;

        Property<Direction> directionProperty = EasyPlaceExtraProtocolHelper.getFirstDirectionProperty(state);
        if (directionProperty != null) {
            int facingOrdinal = raw & 0b0000_0111;
            state = state.setValue(directionProperty, Direction.values()[facingOrdinal % 6]);
            decoded = true;
        }

        int addition = raw >>> 4;
        if (addition != 0) {
            switch (block) {
                case CakeBlock cakeBlock -> {
                    state = state.setValue(BlockStateProperties.BITES, Math.min(addition, 6));
                    decoded = true;
                }
                case StairBlock stairBlock when addition == 1 -> {
                    state = state.setValue(BlockStateProperties.HALF, net.minecraft.world.level.block.state.properties.Half.TOP);
                    decoded = true;
                }
                case TrapDoorBlock trapDoorBlock when addition == 1 -> {
                    state = state.setValue(BlockStateProperties.OPEN, true);
                    decoded = true;
                }
                case SlabBlock slabBlock when addition == 1 -> {
                    state = state.setValue(BlockStateProperties.SLAB_TYPE, net.minecraft.world.level.block.state.properties.SlabType.DOUBLE);
                    decoded = true;
                }
                default -> {
                }
            }
        }

        return decoded ? state : null;
    }

    public static @Nullable BlockState decodeAttachablePlacementState(Block standingBlock, Block wallBlock, BlockPlaceContext context) {
        if (!isRuleEnabled()) return null;

        double relativeHitX = getRelativeHitX(context.getClickLocation(), context.getClickedPos());
        if (!isProtocol(relativeHitX)) return null;

        int protocolValue = decodeProtocolValueFromHitDim(relativeHitX);
        boolean isWallType = (protocolValue & 0b0000_0001) == 0b0000_0001;
        protocolValue >>>= 1;

        if (isWallType) {
            BlockState wallState = wallBlock.getStateForPlacement(context);
            if (wallState == null) return null;

            
            Property<Direction> directionProperty = EasyPlaceExtraProtocolHelper.getFirstDirectionProperty(wallState);
            if (directionProperty != null) {
                int facingIndex = (protocolValue & 0b0000_0011) + 2;
                wallState = wallState.setValue(directionProperty, Direction.values()[facingIndex]);
            }
            protocolValue >>>= 2;

            BlockProtocolStateAdapter adapter = getAdapter(wallBlock);
            if (adapter != null) {
                return adapter.igny$fromProtocolValue(protocolValue, wallState, context);
            }
            return wallState;
        }

        BlockProtocolStateAdapter adapter = getAdapter(standingBlock);
        if (adapter == null) return null;

        BlockState standingState = standingBlock.getStateForPlacement(context);
        if (standingState == null) return null;

        return adapter.igny$fromProtocolValue(protocolValue, standingState, context);
    }

    public static @Nullable ItemStack applyItemStackProtocolData(ItemStack stack, BlockPlaceContext context) {
        if (!isRuleEnabled()) return null;
        if (!(stack.getItem() instanceof BlockItem blockItem)) return null;
        BlockProtocolStateAdapter adapter = getAdapter(blockItem.getBlock());
        if (!(adapter instanceof ItemStackProtocolDataAdapter itemStackProtocolDataAdapter)) return null;

        double relativeHitZ = getRelativeHitZ(context.getClickLocation(), context.getClickedPos());
        if (!isProtocol(relativeHitZ)) return null;

        int protocolAdditionValue = decodeProtocolValueFromHitDim(relativeHitZ);
        return itemStackProtocolDataAdapter.igny$fromProtocolValueAddition(protocolAdditionValue, stack);
    }

    public static int encodeBlockEntityProtocolAddition(BlockEntity blockEntity) {
        //#if MC >= 12003
        if (blockEntity instanceof net.minecraft.world.level.block.entity.CrafterBlockEntity crafterBlockEntity) {
            int bits = 0;
            for (int i = 0; i < 9; ++i) {
                if (crafterBlockEntity.isSlotDisabled(i)) {
                    bits |= (1 << i);
                }
            }
            return bits & 0b0001_1111_1111;
        }
        //#endif
        if (blockEntity instanceof net.minecraft.world.level.block.entity.BeaconBlockEntity beaconBlockEntity) {
            BeaconBlockEntityAccessor accessor = (BeaconBlockEntityAccessor) beaconBlockEntity;
            return BeaconBlockProtocolAdapter.encodeEffects(accessor.igny$getPrimaryPower(), accessor.igny$getSecondaryPower());
        }
        return 0;
    }
}