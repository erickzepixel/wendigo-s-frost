package com.exemplo.wendigosfrost.worldgen;

import com.exemplo.wendigosfrost.registry.ModBlocks;
import com.exemplo.wendigosfrost.registry.ModItems;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Two small, procedurally assembled lore sites.
 * Limit edits to the generating chunk (no cross-chunk writes/ticket cascades).
 * Neither spawns a boss; players discover the mythology and ritual pieces here.
 */
public final class LostRuinFeature extends Feature<NoneFeatureConfiguration> {
    private final boolean shrine;
    public LostRuinFeature(Codec<NoneFeatureConfiguration> codec, boolean shrine) {
        super(codec);
        this.shrine = shrine;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel world = ctx.level();
        BlockPos center = ctx.origin();
        RandomSource rand = ctx.random();

        // Entire 7x7 / 5x5 structure must remain in its source chunk.
        int inset = shrine ? 4 : 3;
        int x = center.getX() & 15, z = center.getZ() & 15;
        if (x < inset || z < inset || x > 15-inset || z > 15-inset) return false;
        if (center.getY() < world.getMinBuildHeight()+2 ||
            center.getY() > world.getMaxBuildHeight()-7) return false;

        BlockState ground = world.getBlockState(center.below());
        if (!(ground.is(Blocks.GRASS_BLOCK) || ground.is(Blocks.DIRT) ||
              ground.is(Blocks.PODZOL) || ground.is(Blocks.SNOW_BLOCK) ||
              ground.is(Blocks.STONE) || ground.is(Blocks.COARSE_DIRT))) return false;
        for (int dx=-2;dx<=2;dx+=2) for (int dz=-2;dz<=2;dz+=2) {
            BlockPos point = center.offset(dx,-1,dz);
            if (!world.getFluidState(point).isEmpty() ||
                world.getBlockState(point).isAir()) return false;
        }
        for (int y=0;y<=4;y++) {
            if (!world.getFluidState(center.above(y)).isEmpty()) return false;
        }
        if (shrine) buildShrine(world, center, rand);
        else buildCairn(world, center, rand);
        return true;
    }

    private static void set(WorldGenLevel world, BlockPos base, int x,int y,int z,BlockState state) {
        world.setBlock(base.offset(x,y,z), state, 2);
    }
    private static BlockState bricks() { return ModBlocks.ANCIENT_FROST_BRICKS.get().defaultBlockState(); }
    private static BlockState runestone() { return ModBlocks.WHISPERING_RUNESTONE.get().defaultBlockState(); }
    private static BlockState lantern() { return ModBlocks.SOUL_FROST_LANTERN.get().defaultBlockState(); }
    private static BlockState totem() { return ModBlocks.PALE_BONE_TOTEM.get().defaultBlockState(); }

    private static void rewardChest(WorldGenLevel world, BlockPos pos, RandomSource random,
                                    boolean rich) {
        world.setBlock(pos, Blocks.CHEST.defaultBlockState(), 2);
        if (world.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(ModItems.LOST_PAGE.get()));
            chest.setItem(3, new ItemStack(ModItems.WHISPER_SHARD.get(), rich ? 2+random.nextInt(2) : 1));
            chest.setItem(6, new ItemStack(ModItems.FROSTBLOOM_SEEDS.get(), 1+random.nextInt(3)));
            if (rich && random.nextInt(3)==0)
                chest.setItem(8, new ItemStack(ModItems.FROSTBLOOM_CRYSTAL.get()));
        }
    }

    /** A half-collapsed 7x7 sanctuary. Runes, lanterns, journal, relic shards. */
    private static void buildShrine(WorldGenLevel w, BlockPos c, RandomSource rng) {
        for (int x=-3;x<=3;x++) for (int z=-3;z<=3;z++) {
            if (Math.abs(x)==3 || Math.abs(z)==3 || rng.nextInt(8)!=0)
                set(w,c,x,-1,z,bricks());
        }
        for (int x : new int[]{-3,3}) for (int z : new int[]{-3,3}) {
            int height=2+rng.nextInt(3);
            for (int y=0;y<height;y++) set(w,c,x,y,z,
                y==height-1 ? runestone() : bricks());
            if (rng.nextBoolean()) set(w,c,x,height,z,lantern());
        }
        for (int x=-2;x<=2;x++) {
            set(w,c,x,0,-3,bricks());
            if (rng.nextInt(4)!=0) set(w,c,x,0,3,bricks());
        }
        for (int z=-2;z<=2;z++) {
            if (rng.nextInt(3)!=0) set(w,c,-3,0,z,bricks());
            if (rng.nextInt(3)!=0) set(w,c,3,0,z,bricks());
        }
        set(w,c,0,0,0,totem());
        set(w,c,0,1,0,runestone());
        set(w,c,-1,0,-1,lantern());
        set(w,c,1,0,-1,lantern());
        rewardChest(w,c.offset(2,0,1),rng,true);
    }

    /** An ancient hunter's 5x5 cairn: bones, runes and a smaller supply chest. */
    private static void buildCairn(WorldGenLevel w, BlockPos c, RandomSource rng) {
        for (int x=-2;x<=2;x++) for (int z=-2;z<=2;z++) {
            if (Math.abs(x)==2 || Math.abs(z)==2 || (x==0 && z==0))
                set(w,c,x,-1,z,bricks());
        }
        set(w,c,0,0,0,totem());
        set(w,c,0,1,0,runestone());
        set(w,c,-2,0,-2,lantern());
        set(w,c,2,0,2,lantern());
        for (int x : new int[]{-2,2}) for (int z : new int[]{-2,2}) {
            if (x==-2 && z==-2 || x==2 && z==2) continue;
            int height=1+rng.nextInt(3);
            for(int y=0;y<height;y++) set(w,c,x,y,z,bricks());
        }
        rewardChest(w,c.offset(1,0,-1),rng,false);
    }
}
