package com.liuyue.igny.mixins.features.rule.furnaceHasIncombustibleHighlight;


import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.network.packet.block.HighlightPayload;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
    private RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;
    //#endif

    @Unique
    private int highlightColor = 0x32FF0000;

    public AbstractFurnaceBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
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
            AbstractFurnaceBlockEntityMixin self = (AbstractFurnaceBlockEntityMixin) (Object) blockEntity;
            if (level != null && !level.isClientSide() && level.getGameTime() % 5 == 0) {
                ItemStack itemStack = blockEntity.getItem(0);
                if (!itemStack.isEmpty() && self.quickCheck.getRecipeFor(
                        //#if MC <= 12006
                        //$$ blockEntity
                        //#else
                        new SingleRecipeInput(itemStack)
                        //#endif
                        , level).isEmpty()) {
                    self.syncHighlightToClient(level, blockPos, self.highlightColor);
                }
            }
        }
    }
    @Unique
    private void syncHighlightToClient(
            //#if MC >= 12102
            //$$ ServerLevel world,
            //#else
            Level world,
            //#endif
            BlockPos pos, int color) {
        if (!world.isClientSide()) {
            //#if MC < 12005
            //$$ FriendlyByteBuf buf = PacketByteBufs.create();
            //$$ buf.writeBlockPos(pos);
            //$$ buf.writeInt(color);
            //$$ buf.writeInt(10);
            //#endif
            world.players().stream()
                    .filter(player -> player instanceof ServerPlayer)
                    .forEach(player -> {
                        if (ServerPlayNetworking.canSend((ServerPlayer) player,
                                //#if MC >= 12005
                                HighlightPayload.TYPE
                                //#else
                                //$$ IGNYServer.HIGHLIGHT_PACKET_ID
                                //#endif

                        )) {
                            ServerPlayNetworking.send(
                                    (ServerPlayer) player,
                                    //#if MC >= 12005
                                    new HighlightPayload(pos, color, 10)
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
