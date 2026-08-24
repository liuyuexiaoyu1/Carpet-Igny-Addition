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
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

//#if MC >= 12003
//#endif
//#if MC >= 12105
//$$ import net.minecraft.world.level.block.FlowerBedBlock;
//#else
//$$ import net.minecraft.world.level.block.PinkPetalsBlock;
//#endif

import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.decodeProtocolValueFromHitDim;
import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.getRelativeHitZ;
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
        //#if MC >= 26.3
        //$$ register(RedstoneWireBlock.class, new RedStoneWireBlockProtocolAdapter());
        //#else
        register(RedStoneWireBlock.class, new RedStoneWireBlockProtocolAdapter());
        //#endif
        register(RedstoneLampBlock.class, new RedstoneLampBlockProtocolAdapter());
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

    public static @Nullable BlockState decodePlacementState(Block block, BlockPlaceContext context, @Nullable BlockState baseState) {
        if (!isRuleEnabled()) return null;
        if (baseState == null) {
            baseState = block.getStateForPlacement(context);
            if (baseState == null) {
                baseState = block.defaultBlockState();
            }
        }

        BlockProtocolStateAdapter adapter = getAdapter(block);
        if (adapter == null || adapter instanceof ItemStackProtocolDataAdapter) {
            return baseState;
        }

        double relativeHitZ = getRelativeHitZ(context.getClickLocation(), context.getClickedPos());
        if (!isProtocol(relativeHitZ)) {
            return baseState;
        }
        int additionValue = decodeProtocolValueFromHitDim(relativeHitZ);
        return adapter.igny$fromProtocolValue(additionValue, baseState, context);
    }

    public static @Nullable BlockState decodeAttachablePlacementState(Block standingBlock, Block wallBlock, BlockPlaceContext context) {
        if (!isRuleEnabled()) return null;

        BlockState baseState = null;
        if (context.getClickedFace().getAxis() != Direction.Axis.Y) {
            baseState = wallBlock.getStateForPlacement(context);
        }
        if (baseState == null) {
            baseState = standingBlock.getStateForPlacement(context);
        }
        if (baseState == null) {
            return null;
        }

        double relativeHitZ = getRelativeHitZ(context.getClickLocation(), context.getClickedPos());
        if (!isProtocol(relativeHitZ)) {
            return baseState;
        }
        int additionValue = decodeProtocolValueFromHitDim(relativeHitZ);
        BlockProtocolStateAdapter adapter = getAdapter(baseState.getBlock());
        if (adapter == null || adapter instanceof ItemStackProtocolDataAdapter) {
            return baseState;
        }
        BlockState applied = adapter.igny$fromProtocolValue(additionValue, baseState, context);
        return applied != null ? applied : baseState;
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