package com.exemplo.wendigosfrost.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** An etched rune that whispers a clue when a survivor touches it. */
public final class WhisperingRunestoneBlock extends Block {
    public WhisperingRunestoneBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            int clue = Math.floorMod(pos.getX() * 31 + pos.getZ() * 17, 3);
            player.displayClientMessage(
                Component.translatable("message.wendigosfrost.rune_" + clue), false);
            if (world instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.SOUL, pos.getX()+.5, pos.getY()+.9, pos.getZ()+.5,
                    20, .35, .4, .35, .018);
                server.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.BLOCKS, .7f, .58f);
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }
}
