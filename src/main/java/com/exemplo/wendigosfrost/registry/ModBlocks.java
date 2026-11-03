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

    // Lore monuments: cracked frost masonry, a talking rune, soul light and an ossuary marker.
    public static final RegistryObject<Block> ANCIENT_FROST_BRICKS = BLOCKS.register(
            "ancient_frost_bricks", () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.ICE).strength(2.6f, 8f)
                    .requiresCorrectToolForDrops().sound(net.minecraft.world.level.block.SoundType.DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> WHISPERING_RUNESTONE = BLOCKS.register(
            "whispering_runestone", () -> new com.exemplo.wendigosfrost.block.WhisperingRunestoneBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE)
                        .strength(3f, 9f).lightLevel(state -> 3)
                        .requiresCorrectToolForDrops().sound(net.minecraft.world.level.block.SoundType.DEEPSLATE)));
    public static final RegistryObject<Block> SOUL_FROST_LANTERN = BLOCKS.register(
            "soul_frost_lantern", () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.ICE).strength(1.8f, 5f)
                    .lightLevel(state -> 13).sound(net.minecraft.world.level.block.SoundType.GLASS)));
    public static final RegistryObject<Block> PALE_BONE_TOTEM = BLOCKS.register(
            "pale_bone_totem", () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN).strength(3f, 8f)
                    .sound(net.minecraft.world.level.block.SoundType.BONE_BLOCK)));

    private ModBlocks() {}
}
