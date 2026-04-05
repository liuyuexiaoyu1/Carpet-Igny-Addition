package com.liuyue.igny.mixins.logger.allFurnace;

import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import com.liuyue.igny.IGNYServerMod;
import com.liuyue.igny.logging.IGNYLoggers;
import com.liuyue.igny.mixins.logger.LoggerAccessor;
import com.liuyue.igny.network.packet.block.HighlightPayload;

import com.liuyue.igny.network.packet.block.RemoveHighlightPayload;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
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

//#if MC <= 12006
//$$ import net.minecraft.world.Container;
//#endif

//#if MC < 12005
//$$ import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import com.liuyue.igny.IGNYServer;
//#endif

//#if MC >= 12106
//$$ import net.minecraft.world.level.storage.ValueInput;
//#endif

@Mixin(value = AbstractFurnaceBlockEntity.class, priority = 940)
public abstract class AbstractFurnaceBlockEntityMixin extends BlockEntity {
    @Shadow
    @Final
    //#if MC <= 12006
    //$$ private RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> quickCheck;
    //#else
    private

    RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;
    //#endif

    //#if MC >= 12104
    //$$ @Shadow int cookingTimer;
    //#else
    @Shadow int cookingProgress;
    //#endif

    //#if MC < 26.1
    @Shadow
    protected abstract boolean isLit();
    //#endif

    //#if MC >= 12104
    //$$ @Shadow private int litTimeRemaining;
    //#else
    @Shadow int litTime;
    //#endif

    @Unique
    private Logger logger;

    @Unique boolean isSleeping = false;

    @Unique private static int counter = 0;
    @Unique public int id = 0;

    public AbstractFurnaceBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "loadAdditional", at = @At(value = "RETURN"))
    private void loadAdditional(
            //#if MC >= 12106
            //$$ ValueInput input,
            //#else
            CompoundTag tag,
                                //#if MC >= 12005
                                HolderLookup.Provider registries,
                                //#endif
            //#endif
                                CallbackInfo ci) {
        if (isSleeping && this.level != null && !this.level.isClientSide()) {
            isSleeping = false;
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (isSleeping && this.level != null && !this.level.isClientSide()) {
            isSleeping = false;
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, RecipeType<?> recipeType, CallbackInfo ci) {
        this.id = counter++;
    }

    @Inject(method = "setItem", at = @At("HEAD"))
    private void onSetItem(int slot, ItemStack itemStack, CallbackInfo ci) {
        if (IGNYLoggers.allFurnace && this.logger.hasOnlineSubscribers() && slot == 0 && this.level != null && !this.level.isClientSide()) {
            //#if MC <= 12006
            //$$ AbstractFurnaceBlockEntity abstractFurnaceBlockEntity = (AbstractFurnaceBlockEntity) (Object) this;
            //#endif
            if (this.level instanceof ServerLevel serverLevel) {
                if (!itemStack.isEmpty() && this.quickCheck.getRecipeFor(
                        //#if MC <= 12006
                        //$$ abstractFurnaceBlockEntity
                        //#else
                        new SingleRecipeInput(itemStack)
                        //#endif
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
            //#if MC >= 12102
            //$$ ServerLevel level,
            //#else
            Level level,
            //#endif
            BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity blockEntity, Operation<Void> original) {
        if (blockEntity == null) return;
        AbstractFurnaceBlockEntityMixin self = (AbstractFurnaceBlockEntityMixin) (Object) blockEntity;
        self.logger = LoggerRegistry.getLogger("allFurnace");
        ItemStack itemStack = blockEntity.getItem(0);
        boolean hasRecipe = self.quickCheck.getRecipeFor(
                //#if MC <= 12006
                //$$ blockEntity
                //#else
                new SingleRecipeInput(itemStack)
                //#endif
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
            if (!hasRecipe && self.isSleeping) return;
        }
        original.call(level, blockPos, blockState, blockEntity);
        //#if MC >= 12104
        //$$ if (IGNYServerMod.LITHIUM) {
        //$$     if (self.litTimeRemaining == 5201314) {
        //$$         self.litTimeRemaining = 0;
        //$$     }
        //$$ }
        //#else
        if (IGNYServerMod.LITHIUM) {
            if (self.litTime == 5201314) {
                self.litTime = 0;
            }
        }
        //#endif
        self.igny$checkSleep(blockState);
    }

    @Inject(method = "serverTick", at = @At(value = "RETURN"))
    private static void onServerTick(
            //#if MC >= 12102
            //$$ ServerLevel level,
            //#else
            Level level,
            //#endif
            BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        AbstractFurnaceBlockEntityMixin self = (AbstractFurnaceBlockEntityMixin) (Object) blockEntity;
        if (self != null) {
            //#if MC >= 12104
            //$$ if (IGNYServerMod.LITHIUM) {
            //$$     if (self.litTimeRemaining <= 0) {
            //$$         self.litTimeRemaining = 5201314;
            //$$     }
            //$$ }
            //#else
            if (IGNYServerMod.LITHIUM) {
                if (self.litTime <= 0) {
                    self.litTime = 5201314;
                }
            }
            //#endif
        }
    }

    @Unique
    private void igny$checkSleep(BlockState state) {
        if (this.level != null &&
                //#if MC >= 26.1
                //$$ this.litTimeRemaining <= 0
                //#else
                !this.isLit()
                //#endif
                &&
                //#if MC >= 12104
                //$$ this.cookingTimer == 0
                //#else
                this.cookingProgress == 0
                //#endif
                && (state.is(Blocks.FURNACE) || state.is(Blocks.BLAST_FURNACE) || state.is(Blocks.SMOKER))) {
            isSleeping = true;
        }
    }

    @Unique
    public void sendHighlightToClient(
            //#if MC >= 12102
            //$$ ServerLevel level,
            //#else
            Level level,
            //#endif
            BlockPos pos, boolean permanent) {
        if (!level.isClientSide()) {

            LevelChunk chunk = level.getChunkAt(pos);
            ChunkSource chunkSource = level.getChunkSource();
            if (chunkSource instanceof ServerChunkCache serverChunkCache) {
                serverChunkCache.chunkMap.getPlayers(chunk.getPos(), false)
                        .forEach(player -> {
                            String name = player.getGameProfile()
                                    //#if MC >= 12110
                                    //$$ .name();
                                    //#else
                                    .getName();
                                    //#endif
                            if (!((LoggerAccessor) this.logger).getSubscribedOnlinePlayers().containsKey(name)) return;
                            String option = ((LoggerAccessor) this.logger).getSubscribedOnlinePlayers().get(name);
                            if (!checkOptionIsInt(option)) return;
                            //#if MC < 12005
                            //$$ FriendlyByteBuf buf = PacketByteBufs.create();
                            //$$ buf.writeBlockPos(pos);
                            //$$ buf.writeInt(Integer.decode(option));
                            //$$ buf.writeInt(70);
                            //$$ buf.writeBoolean(permanent);
                            //#endif
                            if (ServerPlayNetworking.canSend(player,
                                    //#if MC >= 12005
                                    HighlightPayload.TYPE
                                    //#else
                                    //$$ IGNYServer.HIGHLIGHT_PACKET_ID
                                    //#endif

                            )) {
                                ServerPlayNetworking.send(
                                        player,
                                        //#if MC >= 12005
                                        new HighlightPayload(pos, Integer.decode(option), 70, permanent)
                                        //#else
                                        //$$ IGNYServer.HIGHLIGHT_PACKET_ID,
                                        //$$ buf
                                        //#endif
                                );
                            }
                        });
            }
        }
    }

    @Unique
    private void removeHighlightToClient(
            //#if MC >= 12102
            //$$ ServerLevel level,
            //#else
            Level level,
            //#endif
            BlockPos pos) {
        if (!level.isClientSide()) {
            //#if MC < 12005
            //$$ FriendlyByteBuf buf = PacketByteBufs.create();
            //$$ buf.writeBlockPos(pos);
            //#endif
                level.players().stream()
                        .filter(player -> player instanceof ServerPlayer)
                        .forEach(player -> {
                            if (ServerPlayNetworking.canSend((ServerPlayer) player,
                                    //#if MC >= 12005
                                    RemoveHighlightPayload.TYPE
                                    //#else
                                    //$$ IGNYServer.REMOVE_HIGHLIGHT_PACKET_ID
                                    //#endif

                            )) {
                                ServerPlayNetworking.send(
                                        (ServerPlayer) player,
                                        //#if MC >= 12005
                                        new RemoveHighlightPayload(pos)
                                        //#else
                                        //$$ IGNYServer.REMOVE_HIGHLIGHT_PACKET_ID,
                                        //$$ buf
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
