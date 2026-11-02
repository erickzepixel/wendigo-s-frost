package com.exemplo.wendigosfrost.blockentity;

import com.exemplo.wendigosfrost.data.WendigoMark;
import com.exemplo.wendigosfrost.entity.WendigoEntity;
import com.exemplo.wendigosfrost.registry.ModBlockEntities;
import com.exemplo.wendigosfrost.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/** Ritual validado pelo servidor; interrompe por distância e dano. */
public class PedestalBlockEntity extends BlockEntity {
    private final SimpleContainer inventory=new SimpleContainer(3);
    private UUID ritualPlayer;
    private int ticks;
    private float startHealth;
    private long summonCooldownUntil;
    public PedestalBlockEntity(BlockPos pos, BlockState state){ super(ModBlockEntities.PEDESTAL.get(),pos,state); }
    public boolean summonInstantly(ServerPlayer player, ItemStack held) {
        if (ritualPlayer != null) {
            player.displayClientMessage(Component.translatable("message.wendigosfrost.summon_busy"), false);
            return false;
        }
        if (com.exemplo.wendigosfrost.ritual.WendigoSummoningRitual.activate(
                player, worldPosition, held, summonCooldownUntil)) {
            summonCooldownUntil = player.serverLevel().getGameTime() + 2400L;
            setChanged();
            return true;
        }
        return false;
    }
    public boolean insert(ItemStack held){
        if(ritualPlayer!=null) return false;
        int slot=held.is(ModItems.FROSTBLOOM_CRYSTAL.get())?2:
                held.is(ModItems.WENDIGO_HORN.get())?1:
                held.is(ModItems.FROZEN_HEART.get())?0:-1;
        if(slot<0 || !inventory.getItem(slot).isEmpty()) return false;
        inventory.setItem(slot,held.split(1));setChanged();return true;
    }
    public boolean start(ServerPlayer player){
        if(ritualPlayer!=null || !WendigoMark.isMarked(player) ||
                player.distanceToSqr(worldPosition.getX()+0.5,worldPosition.getY()+0.5,worldPosition.getZ()+0.5)>9)
            return false;
        for(int i=0;i<3;i++) if(inventory.getItem(i).isEmpty()) return false;
        ritualPlayer=player.getUUID();ticks=0;startHealth=player.getHealth();setChanged();
        player.displayClientMessage(Component.translatable("message.wendigosfrost.ritual_start"), true);
        return true;
    }
    public static void serverTick(net.minecraft.world.level.Level level,BlockPos pos,BlockState state,PedestalBlockEntity be){
        if(!(level instanceof ServerLevel server) || be.ritualPlayer==null) return;
        ServerPlayer player=server.getServer().getPlayerList().getPlayer(be.ritualPlayer);
        if(player==null || !player.isAlive() || player.level()!=level ||
                player.distanceToSqr(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5)>9 ||
                player.hurtTime > 0 || player.getHealth()<be.startHealth || !WendigoMark.isMarked(player)){
            if (player!=null) player.displayClientMessage(Component.translatable("message.wendigosfrost.ritual_failed"), true);
            be.ritualPlayer=null;be.ticks=0;be.setChanged();return;
        }
        if (be.ticks % 10 == 0) {
            server.sendParticles(ParticleTypes.SNOWFLAKE, pos.getX()+.5,pos.getY()+1.4,pos.getZ()+.5,
                    18,.65,.7,.65,.03);
            for (int i=0;i<12;i++) {
                double angle=i*Math.PI/6+be.ticks*.065;
                server.sendParticles(ParticleTypes.END_ROD,
                    pos.getX()+.5+Math.cos(angle)*1.2, pos.getY()+.2+(be.ticks%60)/60.0,
                    pos.getZ()+.5+Math.sin(angle)*1.2,1,0,0,0,0);
            }
        }
        if (be.ticks % 40 == 0) {
            final int percent = Math.min(100,
                (int)(100.0 * be.ticks / (com.exemplo.wendigosfrost.config.ModConfig.RITUAL_SECONDS.get()*20)));
            player.displayClientMessage(Component.literal("❄ Ritual de purificação: " + percent + "%"),true);
            server.playSound(null,pos,SoundEvents.AMETHYST_BLOCK_CHIME,SoundSource.BLOCKS,.7f,.6f+be.ticks*.001f);
        }
        if(++be.ticks>=com.exemplo.wendigosfrost.config.ModConfig.RITUAL_SECONDS.get()*20){
            UUID owner=player.getUUID();
            WendigoMark.clear(player);
            player.removeEffect(com.exemplo.wendigosfrost.registry.ModEffects.FROST_DELIRIUM.get());
            server.getEntitiesOfClass(WendigoEntity.class,player.getBoundingBox().inflate(128),
                    w->owner.equals(w.getOwnerId())).forEach(WendigoEntity::discard);
            for(int i=0;i<3;i++)be.inventory.setItem(i,ItemStack.EMPTY);
            server.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, .8f);
            player.displayClientMessage(Component.translatable("message.wendigosfrost.ritual_complete"), false);
            var advancement = server.getServer().getAdvancements().getAdvancement(
                new net.minecraft.resources.ResourceLocation("wendigosfrost", "freedom"));
            if (advancement != null) player.getAdvancements().award(advancement, "cleansed");
            be.ritualPlayer=null;be.ticks=0;be.setChanged();
        }
    }
    public void dropContents(){
        if(level!=null) Containers.dropContents(level,worldPosition,inventory);
    }
    @Override protected void saveAdditional(CompoundTag tag){
        super.saveAdditional(tag);
        for(int i=0;i<3;i++)tag.put("Slot"+i,inventory.getItem(i).save(new CompoundTag()));
        if(ritualPlayer!=null)tag.putUUID("RitualPlayer",ritualPlayer);
        tag.putInt("RitualTicks",ticks);
        tag.putFloat("StartHealth",startHealth);
        tag.putLong("SummonCooldownUntil", summonCooldownUntil);
    }
    @Override public void load(CompoundTag tag){
        super.load(tag);
        for(int i=0;i<3;i++)inventory.setItem(i,ItemStack.of(tag.getCompound("Slot"+i)));
        ritualPlayer=tag.hasUUID("RitualPlayer")?tag.getUUID("RitualPlayer"):null;
        ticks=tag.getInt("RitualTicks");startHealth=tag.getFloat("StartHealth");
        summonCooldownUntil=tag.getLong("SummonCooldownUntil");
    }
}
