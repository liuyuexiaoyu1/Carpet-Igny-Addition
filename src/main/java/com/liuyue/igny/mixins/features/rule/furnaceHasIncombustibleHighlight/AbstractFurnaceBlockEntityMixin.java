package com.liuyue.igny.mixins.features.rule.furnaceHasIncombustibleHighlight;


import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.network.packet.block.HighlightPayload;

import com.liuyue.igny.network.packet.block.RemoveHighlightPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
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

//#if MC >= 12102
//$$ import net.minecraft.server.level.ServerLevel;
//#else
import net.minecraft.world.level.Level;
//#endif

//#if MC <= 12006
//$$ import net.minecraft.world.Container;
//#else
import net.minecraft.world.item.crafting.SingleRecipeInput;
//#endif

//#if MC < 12005
//$$ import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import com.liuyue.igny.IGNYServer;
//#endif

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin extends BlockEntity {
    @Shadow
    @Final
    //#if MC <= 12006
    //$$ private RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> quickCheck;
    //#else
    private

    RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;
    //#endif

    @Unique
    public int highlightColor = 0x32FF0000;


    @Unique private static int counter = 0;
    @Unique
    public int id = 0;

    public AbstractFurnaceBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, RecipeType<?> recipeType, CallbackInfo ci) {
        this.id = counter++;
    }


    @Inject(method = "setItem", at = @At("HEAD"))
    private void onSetItem(int slot, ItemStack itemStack, CallbackInfo ci) {
        if (IGNYSettings.furnaceHasIncombustibleHighlight && slot == 0 && this.level != null && !this.level.isClientSide()) {
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
                    this.sendHighlightToClient(
                            serverLevel, this.worldPosition, this.highlightColor, true);
                } else {
                    this.removeHighlightToClient(
                            serverLevel, this.worldPosition);
                }
            }
        }
    }

    @Inject(method = "serverTick", at = @At("HEAD"))
    private static void onTick(
            //#if MC >= 12102
            //$$ ServerLevel level,
            //#else
            Level level,
            //#endif
            BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        if (IGNYSettings.furnaceHasIncombustibleHighlight) {
            if (blockEntity == null) return;
            if (level instanceof ServerLevel) {
                AbstractFurnaceBlockEntityMixin self = (AbstractFurnaceBlockEntityMixin) (Object) blockEntity;
                if (level != null && !level.isClientSide() && (level.getGameTime() + self.id) % 60 == 0) {
                    ItemStack itemStack = blockEntity.getItem(0);
                    if (!itemStack.isEmpty() && self.quickCheck.getRecipeFor(
                            //#if MC <= 12006
                            //$$ blockEntity
                            //#else
                            new SingleRecipeInput(itemStack)
                            //#endif
                            , level).isEmpty()) {
                        self.sendHighlightToClient(level, blockPos, self.highlightColor, false);
                    }
                }
            }
        }
    }

    @Unique
    public void sendHighlightToClient(
            //#if MC >= 12102
            //$$ ServerLevel world,
            //#else
            Level level,
            //#endif
            BlockPos pos, int color, boolean permanent) {
        if (!level.isClientSide()) {
            //#if MC < 12005
            //$$ FriendlyByteBuf buf = PacketByteBufs.create();
            //$$ buf.writeBlockPos(pos);
            //$$ buf.writeInt(color);
            //$$ buf.writeInt(70);
            //$$ buf.writeBoolean(permanent);
            //#endif
            LevelChunk chunk = level.getChunkAt(pos);
            ChunkSource chunkSource = level.getChunkSource();
            if (chunkSource instanceof ServerChunkCache serverChunkCache) {
                serverChunkCache.chunkMap.getPlayers(chunk.getPos(), false)
                        .forEach(player -> {
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
                                        new HighlightPayload(pos, color, 70, permanent)
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
            //$$ ServerLevel world,
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
}
