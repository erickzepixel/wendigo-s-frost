package com.exemplo.wendigosfrost.registry;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.block.FrostbloomCrop;
import com.exemplo.wendigosfrost.block.DeepGlacierPedestal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, WendigosFrost.MOD_ID);

    public static final RegistryObject<Block> FROSTBLOOM_CROP = BLOCKS.register(
            "frostbloom_crop",
            () -> new FrostbloomCrop(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.CROP)));

    public static final RegistryObject<Block> PEDESTAL = BLOCKS.register("deep_glacier_pedestal",
            () -> new DeepGlacierPedestal(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE).strength(5.0f, 1200.0f).lightLevel(s -> 5)));
    private ModBlocks() {}
}
