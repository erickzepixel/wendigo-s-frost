package com.exemplo.wendigosfrost.event;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

/** Sementes selvagens podem ser obtidas destruindo grama em florestas e taigas. */
@Mod.EventBusSubscriber(modid = WendigosFrost.MOD_ID)
public final class FrostbloomEvents {
    private FrostbloomEvents() {}
    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || event.getPlayer().isCreative() || event.isCanceled()) return;
        BlockPos pos = event.getPos();
        BlockState old = event.getState();
        if (!(old.is(Blocks.GRASS) || old.is(Blocks.TALL_GRASS) || old.is(Blocks.FERN) || old.is(Blocks.LARGE_FERN))) return;
        if (!level.getBiome(pos).is(BiomeTags.IS_FOREST) && !level.getBiome(pos).is(BiomeTags.IS_TAIGA)) return;
        if (level.random.nextInt(30) != 0) return;
        ItemEntity drop = new ItemEntity(level, pos.getX()+.5, pos.getY()+.3, pos.getZ()+.5,
                new ItemStack(ModItems.FROSTBLOOM_SEEDS.get()));
        level.addFreshEntity(drop);
    }
}
