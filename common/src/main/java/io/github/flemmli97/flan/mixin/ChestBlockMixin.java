package io.github.flemmli97.flan.mixin;

import io.github.flemmli97.flan.event.BlockInteractEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin {

    /*@Inject(method = "candidatePartnerFacing", at = @At("HEAD"), cancellable = true)
    private void checkCandidate(Level level, BlockPos clickedPos, Direction direction, CallbackInfoReturnable<Direction> cir) {
        BlockPos pos = context.getClickedPos().relative(direction);
        if (BlockInteractEvents.useBlocks(context.getPlayer(), context.getLevel(), context.getHand(),
                new BlockHitResult(context.getClickLocation(), direction, pos, false)) == InteractionResult.FAIL) {
            info.setReturnValue(null);
            if (context.getPlayer() instanceof ServerPlayer player)
                player.level().getServer().schedule(new TickTask(1, () -> player.connection.send(new ClientboundBlockUpdatePacket(pos, context.getLevel().getBlockState(pos)))));
        }
    }*/

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void checkCandidate(BlockPlaceContext context, CallbackInfoReturnable<BlockState> info) {

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        if (BlockInteractEvents.useBlocks(context.getPlayer(), context.getLevel(), context.getHand(),
                new BlockHitResult(context.getClickLocation(), context.getHorizontalDirection().getOpposite(), pos, false)) == InteractionResult.FAIL) {
            info.getReturnValue().setValue(ChestBlock.TYPE, ChestType.SINGLE);
            if (context.getPlayer() instanceof ServerPlayer player)
                player.level().getServer().schedule(new TickTask(1, () -> player.connection.send(new ClientboundBlockUpdatePacket(pos, context.getLevel().getBlockState(pos)))));
        }
    }
}
