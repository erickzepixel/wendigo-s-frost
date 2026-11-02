package com.exemplo.wendigosfrost.config;

import net.minecraftforge.common.ForgeConfigSpec;

/** Configuracoes do servidor, salvas em serverconfig. */
public final class ModConfig {
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final ForgeConfigSpec.IntValue DAYS_TO_AWAKEN;
    public static final ForgeConfigSpec.IntValue PERCEPTION_RANGE;
    public static final ForgeConfigSpec.IntValue WENDIGO_HEALTH;
    public static final ForgeConfigSpec.DoubleValue WENDIGO_DAMAGE;
    public static final ForgeConfigSpec.IntValue RITUAL_SECONDS;
    public static final ForgeConfigSpec.IntValue DELIRIUM_SECONDS;
    public static final ForgeConfigSpec.IntValue GROWTH_DIVISOR;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HALLUCINATIONS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_JUMPSCARES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_FOG;
    public static final ForgeConfigSpec.DoubleValue VISUAL_INTENSITY;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("wendigo");
        DAYS_TO_AWAKEN = b.defineInRange("daysToAwaken", 3, 1, 30);
        PERCEPTION_RANGE = b.defineInRange("perceptionRange", 10, 1, 64);
        WENDIGO_HEALTH = b.defineInRange("health", 100, 1, 1000);
        WENDIGO_DAMAGE = b.defineInRange("damage", 10.0, 1.0, 100.0);
        b.pop();
        b.push("ritual");
        RITUAL_SECONDS = b.defineInRange("durationSeconds", 15, 1, 300);
        b.pop();
        b.push("frostbloom");
        GROWTH_DIVISOR = b.defineInRange("growthDivisor", 14, 1, 1000);
        b.pop();
        b.push("delirium");
        DELIRIUM_SECONDS = b.defineInRange("durationSeconds", 360, 300, 480);
        ENABLE_HALLUCINATIONS = b.define("hallucinations", true);
        ENABLE_JUMPSCARES = b.define("jumpscares", false);
        ENABLE_FOG = b.define("fog", true);
        VISUAL_INTENSITY = b.defineInRange("visualIntensity", 0.5, 0.0, 1.0);
        b.pop();
        SERVER_SPEC = b.build();
    }
    private ModConfig() {}
}
