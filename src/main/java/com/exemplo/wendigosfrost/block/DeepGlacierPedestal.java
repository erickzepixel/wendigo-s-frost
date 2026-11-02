package com.exemplo.wendigosfrost.block;

import com.exemplo.wendigosfrost.blockentity.PedestalBlockEntity;
import com.exemplo.wendigosfrost.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

/** Três ingredientes são inseridos com clique direito; mão vazia inicia ritual. */
public class DeepGlacierPedestal extends BaseEntityBlock {
    public DeepGlacierPedestal(BlockBehaviour.Properties properties){ super(properties); }
    @Nullable @Override public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
            Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type){
        return level.isClientSide ? null :
                createTickerHelper(type,ModBlockEntities.PEDESTAL.get(),PedestalBlockEntity::serverTick);
    }
    @Override public RenderShape getRenderShape(BlockState state){ return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        return ModBlockEntities.PEDESTAL.get().create(pos,state);
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                             InteractionHand hand, BlockHitResult hit){
        if(level.isClientSide) return InteractionResult.SUCCESS;
        if(!(player instanceof ServerPlayer sp) || !(level.getBlockEntity(pos) instanceof PedestalBlockEntity be))
            return InteractionResult.PASS;
        ItemStack stack=player.getItemInHand(hand);
        if(stack.isEmpty()) return be.start(sp) ? InteractionResult.CONSUME : InteractionResult.PASS;
        return be.insert(stack) ? InteractionResult.CONSUME : InteractionResult.PASS;
    }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving){
        if(!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof PedestalBlockEntity be){
            if(!level.isClientSide) be.dropContents();
        }
        super.onRemove(state,level,pos,next,moving);
    }
}
