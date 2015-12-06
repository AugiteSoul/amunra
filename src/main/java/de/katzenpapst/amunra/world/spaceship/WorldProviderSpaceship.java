package de.katzenpapst.amunra.world.spaceship;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderOrbit;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.world.gen.ChunkProviderOrbit;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderSpaceship extends WorldProviderOrbit {
	@Override
    public CelestialBody getCelestialBody()
    {
        return GalacticraftCore.satelliteSpaceStation;
    }
	@Override
    public Class<? extends IChunkProvider> getChunkProviderClass()
    {
        return ChunkProviderOrbit.class;
    }

	@Override
    public String getDimensionName()
    {
        return "Space Ship " + this.spaceStationDimensionID;
    }

    @Override
    public String getPlanetToOrbit()
    {
        return "Overworld";
    }


    @Override
    public String getSaveFolder()
    {
        return "DIM_SPACESHIP" + this.spaceStationDimensionID;
    }

    @Override
    public double getSolarEnergyMultiplier()
    {
        return ConfigManagerCore.spaceStationEnergyScalar;
    }
}
