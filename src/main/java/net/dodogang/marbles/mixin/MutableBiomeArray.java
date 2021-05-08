package net.dodogang.marbles.mixin;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public interface MutableBiomeArray {
    void setBiome(int x, int y, int z, RegistryKey<Biome> biome);
}
