package com.exemplo.wendigosfrost.block;

import com.exemplo.wendigosfrost.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;

public class FrostbloomCrop extends CropBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_5;

    public FrostbloomCrop(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected IntegerProperty getAgeProperty() { return AGE; }

    @Override
    public int getMaxAge() { return 5; }

    @Override
    protected Item getBaseSeedId() { return ModItems.FROSTBLOOM_SEEDS.get(); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Block below = level.getBlockState(pos.below()).getBlock();
        return below == Blocks.FARMLAND || below == Blocks.DIRT || below == Blocks.GRASS_BLOCK
                || below == Blocks.PODZOL;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Aproxima 4 dias com randomTickSpeed=3 e chunk carregado.
        // Variacao estatistica e chunk descarregado afetam o intervalo.
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9
                && random.nextInt(com.exemplo.wendigosfrost.config.ModConfig.GROWTH_DIVISOR.get()) == 0) {
            int age = state.getValue(AGE);
            if (age < getMaxAge()) level.setBlock(pos, state.setValue(AGE, age + 1), 2);
        }
    }

}
