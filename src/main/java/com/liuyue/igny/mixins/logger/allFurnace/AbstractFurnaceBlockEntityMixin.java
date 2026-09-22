package com.liuyue.igny.mixins.logger.allFurnace;

import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import com.liuyue.igny.IGNYServerMod;
import com.liuyue.igny.logger.IGNYLoggers;
import com.liuyue.igny.mixins.logger.LoggerAccessor;
import com.liuyue.igny.network.packet.block.HighlightPayload;

import com.liuyue.igny.network.packet.block.RemoveHighlightPayload;
import com.liuyue.igny.utils.interfaces.allFurnace.SleepingBlock;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.Level;

//?<= 1.20.6 ? import net.minecraft.world.Container;

//#if < 1.20.5
/*$$import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import com.liuyue.igny.IGNYServer;$$*/
//#endif

//?>= 1.21.6 ? import net.minecraft.world.level.storage.ValueInput;

@Restriction(conflict = @Condition("pca"))
@Mixin(value = AbstractFurnaceBlockEntity.class, priority = 999)
public abstract class AbstractFurnaceBlockEntityMixin extends BlockEntity implements SleepingBlock {
    @Shadow
    @Final
    private RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck; //#replace <= 1.20.6 ? private RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> quickCheck;

    @Shadow int cookingProgress; //#replace >= 1.21.4 ? @Shadow int cookingTimer;

    //#if < 26.1
    @Shadow
    protected abstract boolean isLit();
    //#endif

    @Shadow int litTime; //#replace >= 1.21.4 ? @Shadow private int litTimeRemaining;

    @Unique
    private Logger logger;

    @Unique boolean isSleeping = false;

    @Unique private static int counter = 0;
    @Unique public int id = 0;

    @Override
    public boolean igny$isSleeping() {
        return isSleeping;
    }

    @Override
    public void igny$setSleeping(boolean sleeping) {
        isSleeping = sleeping;
    }

    public AbstractFurnaceBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "loadAdditional", at = @At(value = "RETURN"))
    private void loadAdditional(
            //#if >= 1.21.6
            //$$ ValueInput input,
            //#else
            CompoundTag tag,
                                HolderLookup.Provider registries, //?>= 1.20.5
            //#endif
                                CallbackInfo ci) {
        if (igny$isSleeping() && this.level != null && !this.level.isClientSide()) {
            igny$setSleeping(false);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, RecipeType<?> recipeType, CallbackInfo ci) {
        this.id = counter++;
    }

    @Inject(method = "setItem", at = @At("HEAD"))
    private void onSetItem(int slot, ItemStack itemStack, CallbackInfo ci) {
        if (IGNYLoggers.allFurnace && this.logger.hasOnlineSubscribers() && slot == 0 && this.level != null && !this.level.isClientSide()) {
            //?<= 1.20.6 ? AbstractFurnaceBlockEntity abstractFurnaceBlockEntity = (AbstractFurnaceBlockEntity) (Object) this;
            if (this.level instanceof ServerLevel serverLevel) {
                if (!itemStack.isEmpty() && this.quickCheck.getRecipeFor(
                        new SingleRecipeInput(itemStack) //#replace <= 1.20.6 ? abstractFurnaceBlockEntity
                        ,
                        serverLevel).isEmpty()) {
                    this.sendHighlightToClient(serverLevel, this.worldPosition, true);
                    return;
                }
                this.removeHighlightToClient(serverLevel, this.worldPosition);
            }
        }
    }

    @WrapMethod(method = "serverTick")
    private static void onTick(
            Level level, //#replace >= 1.21.2 ? ServerLevel level,
            BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity blockEntity, Operation<Void> original) {
        if (blockEntity == null) return;
        AbstractFurnaceBlockEntityMixin self = (AbstractFurnaceBlockEntityMixin) (Object) blockEntity;
        self.logger = LoggerRegistry.getLogger("allFurnace");
        ItemStack itemStack = blockEntity.getItem(0);
        boolean hasRecipe = self.quickCheck.getRecipeFor(
                new SingleRecipeInput(itemStack) //#replace <= 1.20.6 ? blockEntity
                , level).isPresent();
        if (IGNYLoggers.allFurnace && self.logger.hasOnlineSubscribers()) {
            if (level instanceof ServerLevel) {
                if (level != null && !level.isClientSide() && (level.getGameTime() + self.id) % 60 == 0) {
                    if (!itemStack.isEmpty() && !hasRecipe) {
                        self.sendHighlightToClient(level, blockPos, false);
                    }
                }
            }
        }
        if (IGNYServerMod.LITHIUM) {
            if (!hasRecipe && self.igny$isSleeping()) return;
        }
        original.call(level, blockPos, blockState, blockEntity);
        self.igny$checkSleep(blockState);
    }

    @Unique
    private void igny$checkSleep(BlockState state) {
        if (this.level != null &&
                !this.isLit() //#replace >= 26.1 ? this.litTimeRemaining <= 0
                &&
                this.cookingProgress == 0 //#replace >= 1.21.4 ? this.cookingTimer == 0
                && (state.is(Blocks.FURNACE) || state.is(Blocks.BLAST_FURNACE) || state.is(Blocks.SMOKER))) {
            igny$setSleeping(true);
        }
    }

    @Unique
    public void sendHighlightToClient(
            Level level, //#replace >= 1.21.2 ? ServerLevel level,
            BlockPos pos, boolean permanent) {
        if (!level.isClientSide()) {

            LevelChunk chunk = level.getChunkAt(pos);
            ChunkSource chunkSource = level.getChunkSource();
            if (chunkSource instanceof ServerChunkCache serverChunkCache) {
                serverChunkCache.chunkMap.getPlayers(chunk.getPos(), false)
                        .forEach(player -> {
                            String name = player.getGameProfile()
                                    .getName(); //#replace >= 1.21.10 ? .name();
                            if (!((LoggerAccessor) this.logger).getSubscribedOnlinePlayers().containsKey(name)) return;
                            String option = ((LoggerAccessor) this.logger).getSubscribedOnlinePlayers().get(name);
                            if (!checkOptionIsInt(option)) return;
                            //#if < 1.20.5
                            /*$$FriendlyByteBuf buf = PacketByteBufs.create();
                            buf.writeBlockPos(pos);
                            buf.writeInt(Integer.decode(option));
                            buf.writeInt(70);
                            buf.writeBoolean(permanent);$$*/
                            //#endif
                            if (ServerPlayNetworking.canSend(player,
                                    HighlightPayload.TYPE //#replace < 1.20.5 ? IGNYServer.HIGHLIGHT_PACKET_ID

                            )) {
                                ServerPlayNetworking.send(
                                        player,
                                        //#if >= 1.20.5
                                        new HighlightPayload(pos, Integer.decode(option), 70, permanent)
                                        //#else
                                        /*$$IGNYServer.HIGHLIGHT_PACKET_ID,
                                        buf$$*/
                                        //#endif
                                );
                            }
                        });
            }
        }
    }

    @Unique
    private void removeHighlightToClient(
            Level level, //#replace >= 1.21.2 ? ServerLevel level,
            BlockPos pos) {
        if (!level.isClientSide()) {
            //#if < 1.20.5
            /*$$FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(pos);$$*/
            //#endif
                level.players().stream()
                        .filter(player -> player instanceof ServerPlayer)
                        .forEach(player -> {
                            if (ServerPlayNetworking.canSend((ServerPlayer) player,
                                    RemoveHighlightPayload.TYPE //#replace < 1.20.5 ? IGNYServer.REMOVE_HIGHLIGHT_PACKET_ID

                            )) {
                                ServerPlayNetworking.send(
                                        (ServerPlayer) player,
                                        //#if >= 1.20.5
                                        new RemoveHighlightPayload(pos)
                                        //#else
                                        /*$$IGNYServer.REMOVE_HIGHLIGHT_PACKET_ID,
                                        buf$$*/
                                        //#endif
                                );
                            }
                        });
        }
    }

    @Unique
    private boolean checkOptionIsInt(String option) {
        try {
            Integer.decode(option);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
