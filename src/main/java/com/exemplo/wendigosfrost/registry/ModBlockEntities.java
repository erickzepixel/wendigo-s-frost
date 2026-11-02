package com.exemplo.wendigosfrost.registry;
import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.blockentity.PedestalBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> TYPES=DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES,WendigosFrost.MOD_ID);
    public static final RegistryObject<BlockEntityType<PedestalBlockEntity>> PEDESTAL=TYPES.register("deep_glacier_pedestal",
            ()->BlockEntityType.Builder.of(PedestalBlockEntity::new,ModBlocks.PEDESTAL.get()).build(null));
}
