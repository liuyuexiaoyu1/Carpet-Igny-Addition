package com.liuyue.igny.mixins.rule.openDecoratedPotGui;

//#if MC >= 12005
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.inventory.DecoratedPotContainer;
import com.liuyue.igny.helper.inventory.DecoratedPotMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
//$$ import com.liuyue.igny.utils.compat.DummyClass;
//#endif
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12005
@Mixin(DecoratedPotBlock.class)
//#else
//$$ @Mixin(DummyClass.class)
//#endif
public class DecoratedPotBlockMixin {

    //#if MC >= 12005
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!IGNYSettings.OPEN_DECORATED_POT_GUI.value()) return;
        if (level.isClientSide()) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof DecoratedPotBlockEntity pot)) {
            cir.setReturnValue(InteractionResult.PASS);
            return;
        }

        MenuProvider menuProvider = new SimpleMenuProvider(
                (id, inv, p) -> new DecoratedPotMenu(id, inv, new DecoratedPotContainer(pot)),
                Component.translatable("block.minecraft.decorated_pot")
        );
        serverPlayer.openMenu(menuProvider);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
    //#endif
}
