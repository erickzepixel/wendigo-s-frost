package com.exemplo.wendigosfrost.worldgen;

import com.exemplo.wendigosfrost.registry.ModBlocks;
import com.exemplo.wendigosfrost.registry.ModItems;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Pequena sala subterrânea rara, construída durante worldgen em florestas e taigas. */
public final class FrozenSanctuaryFeature extends Feature<NoneFeatureConfiguration> {
    public FrozenSanctuaryFeature(Codec<NoneFeatureConfiguration> codec) { super(codec); }
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos center = context.origin();
        RandomSource random = context.random();
        // Não escrevemos em chunks vizinhos durante a geração.
        if ((center.getX() & 15) < 3 || (center.getX() & 15) > 12 ||
                (center.getZ() & 15) < 3 || (center.getZ() & 15) > 12) return false;
        // Evita santuários abertos ao céu: exigimos ao menos 4 camadas sólidas acima.
        if (world.isEmptyBlock(center.above(4)) || !world.getFluidState(center).isEmpty()) return false;
        for (int x=-3; x<=3; x++) for (int z=-3; z<=3; z++) for (int y=-1; y<=4; y++) {
            BlockPos p = center.offset(x,y,z);
            boolean wall = x==-3 || x==3 || z==-3 || z==3;
            boolean floor = y==-1, roof = y==4;
            BlockState toPlace = floor || roof || wall
                    ? (random.nextInt(5)==0 ? Blocks.BLUE_ICE : Blocks.PACKED_ICE).defaultBlockState()
                    : Blocks.AIR.defaultBlockState();
            world.setBlock(p,toPlace,2);
        }
        world.setBlock(center, ModBlocks.PEDESTAL.get().defaultBlockState(), 2);
        world.setBlock(center.offset(2,0,1), Blocks.CHEST.defaultBlockState(), 2);
        if (world.getBlockEntity(center.offset(2,0,1)) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(ModItems.FROSTBLOOM_SEEDS.get(), 2 + random.nextInt(3)));
            if(random.nextBoolean()) chest.setItem(4, new ItemStack(ModItems.FROSTBLOOM_CRYSTAL.get()));
        }
        for (int dx : new int[]{-2, 2}) for (int dz : new int[]{-2, 2})
            world.setBlock(center.offset(dx,0,dz), Blocks.SOUL_LANTERN.defaultBlockState(), 2);
        return true;
    }
}
